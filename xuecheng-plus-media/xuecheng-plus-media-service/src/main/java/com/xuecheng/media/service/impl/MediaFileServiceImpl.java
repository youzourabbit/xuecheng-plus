package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.execption.RestResponse;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    MediaFilesMapper mediaFilesMapper;//媒体文件：代理对象

    @Autowired
    MediaFileService currentProxy;

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    String bucket_videoFiles = "video";

//    @Autowired
//    MediaFiles mediaFiles;

//    MediaFiles mediaFiles = new MediaFiles();

    @Value("${minio.bucket.files}")
    private String minioBucketFiles;

    //2023/07/04，定义接口：检测文件、检测分块、上传分块

    /*如果指定文件（文件夹）不存在，则需要进行创建操作*/
    //先在数据库查询文件信息，数据库中已经存放了桶、文件路径的详细信息
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {//文件的md5值就是存入数据库的id
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //存储目录
            String filePath = mediaFiles.getFilePath();

            //询问Minio是否有此文件信息
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build();
            try {
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                if (inputStream != null) {
                    return RestResponse.success(true);
                }
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                e.printStackTrace();
            }

            //文件流
            /*InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());

                if (stream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {

            }*/
        }
        //文件不存在
        return RestResponse.success(false);
//        return null;
    }


    //这个方法是在获取minio桶中是否存在已经分块分好的数据的
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {


        //得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        //文件流
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(chunkFilePath)
                            .build());

            if (fileInputStream != null) {
                //分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        }
        //分块未存在
        return RestResponse.success(false);
    }

    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .filename(localFilePath)
                    .object(objectName)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传成功");
            return true;
        } catch (IOException | InternalException | XmlParserException | InvalidResponseException | InvalidKeyException |
                 NoSuchAlgorithmException | ErrorResponseException | InsufficientDataException | ServerException e) {
            e.printStackTrace();
            XueChengPlusException.cast("文件上传失败");
        }
        return false;
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {

        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;
        String bucket_videoFiles = "video";
        String mimeType = getMimeType(null);
        boolean b = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucket_videoFiles, chunkFilePath);

        if (!b) {
            return RestResponse.success(false, "上传文件分块失败");
        }

        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergeChunk(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {


        //=====获取分块文件路径=====
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //组成将分块文件路径组成 List<ComposeSource>
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_videoFiles)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        //=====合并=====
        //文件名称
        String fileName = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        //合并文件路径
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);
        try {
            //合并文件
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(mergeFilePath)
                            .sources(sourceObjectList)
                            .build());
            log.debug("合并文件成功:{}", mergeFilePath);
        } catch (Exception e) {
            log.debug("合并文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            return RestResponse.validfail(false, "合并文件失败。");
        }

        // ====验证md5====
        //下载合并后的文件
        File minioFile = downloadFileFromMinIO(bucket_videoFiles, mergeFilePath);
        if (minioFile == null) {
            log.debug("下载合并后文件失败,mergeFilePath:{}", mergeFilePath);
            return RestResponse.validfail(false, "下载合并后文件失败。");
        }

        try (InputStream newFileInputStream = new FileInputStream(minioFile)) {
            //minio上文件的md5值
            String md5Hex = DigestUtils.md5Hex(newFileInputStream);
            //比较md5值，不一致则说明文件不完整
            if (!fileMd5.equals(md5Hex)) {
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            //文件大小
            uploadFileParamsDto.setFileSize(minioFile.length());
        } catch (Exception e) {
            log.debug("校验文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
        } finally {
            if (minioFile != null) {
                minioFile.delete();
            }
        }
        //文件入库
        currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videoFiles, mergeFilePath);
        //=====清除分块文件=====
        clearChunkFiles(chunkFileFolderPath, chunkTotal);
        return RestResponse.success(true);

//        //1.找到分块文件调用Minio的sdk进行文件合并
//        List<ComposeSource> sources = new ArrayList<>();
//        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
//        //源文件名
//        uploadFileParamsDto.getFilename();
//        //指定分块文件信息
//        for (int i = 0; i < chunkTotal; i++) {
//            ComposeSource source = ComposeSource
//                    .builder()
//                    .bucket("video")
//                    .object(chunkFileFolderPath + i)
//                    .build();
//            sources.add(source);
//        }
//        /*合并*/
//        //文件名称
////        String fileName = uploadFileParamsDto.getFilename();
//        //文件拓展名
//        String extName = fileName.substring(fileName.lastIndexOf("."));
//        //合并文件的路径
//        String mergeFilePath = getFilePathByMd5(fileMd5,extName);
//        //合并文件
//        try {
//            ObjectWriteResponse response = minioClient.composeObject(
//                    ComposeObjectArgs.builder()
//                            .bucket(bucket_videoFiles)
//                            .object(mergeFilePath)
//                            .sources(sources)
//                            .build()
//            );
//        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
//                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
//                 XmlParserException e) {
//            e.printStackTrace();
//        }
//
//        //2.检验合并的文件是否Md5值一致，一致才算成功
//        //先下载合并后的文件
//        File minioFile = downloadFileFromMinIO(bucket_videoFiles, mergeFilePath);
//        if (minioFile == null){
//            return RestResponse.validfail(false,"下载后合并失败");
//        }
//        //计算合并后的Md5
//        try {
//            InputStream fileInputStream = new FileInputStream(minioFile);
//            //存到minio时的md5值
//            String md5Hex = DigestUtils.md5Hex(fileInputStream);
//            if (!fileMd5.equals(md5Hex)) {
//                return RestResponse.validfail(false, "上传错误：文件校验结果不一致");
//            }
//            //文件大小
//            uploadFileParamsDto.setFileSize(minioFile.length());
//        } catch (IOException e) {
////            throw new RuntimeException(e);
//            return RestResponse.validfail(false,"文件合并校验失败");
//        }finally {
//            //不论上传成功与否，都对这个File对象执行删除操作
//            minioFile.delete();
//        }
//
//        //3.将文件信息入库
//        currentProxy.addMediaFileToDb(companyId,fileMd5,uploadFileParamsDto,bucket_videoFiles,mergeFilePath);
//
//        //4.清理分块文件
//        clearChunkFiles(chunkFileFolderPath, chunkTotal);
//        return RestResponse.success(true);
    }

    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {


        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            //媒体类型
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}", mediaFiles.toString());
                XueChengPlusException.cast("保存文件信息失败");
            }
            //添加到待处理任务表
            addWaitingTask(mediaFiles);
            log.debug("保存文件信息到数据库成功,{}", mediaFiles.toString());

        }
        return mediaFiles;

        //从数据库查询文件
//        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
//        if (mediaFiles == null) {
//            mediaFiles = new MediaFiles();
//            //拷贝基本信息
//            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
//            mediaFiles.setId(fileMd5);
//            mediaFiles.setFileId(fileMd5);
//            mediaFiles.setCompanyId(companyId);
//            //媒体类型
//            mediaFiles.setUrl("/" + bucket + "/" + objectName);
//            mediaFiles.setBucket(bucket);
//            mediaFiles.setFilePath(objectName);
//            mediaFiles.setCreateDate(LocalDateTime.now());
//            mediaFiles.setAuditStatus("002003");
//            mediaFiles.setStatus("1");
//            //保存文件信息到文件表
//            int insert = mediaFilesMapper.insert(mediaFiles);
//            if (insert < 0) {
//                log.error("保存文件信息到数据库失败,{}", mediaFiles.toString());
//                XueChengPlusException.cast("保存文件信息失败");
//            }
//            //添加到待处理任务表
//            addWaitingTask(mediaFiles);
//            log.debug("保存文件信息到数据库成功,{}", mediaFiles.toString());
//
//        }
//        return mediaFiles;

    }


    /**
     * 得到合并后的文件的地址
     *
     * @param fileMd5 文件id即md5值
     * @param fileExt 文件扩展名
     * @return
     */
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

//    @Override
//    @Transactional
//    public MediaFiles addMediaFileToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
//
//        //从数据库查询文件
//        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
//        if (mediaFiles == null) {
//            mediaFiles = new MediaFiles();
//            //拷贝基本信息
//            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
//            mediaFiles.setId(fileMd5);
//            mediaFiles.setFileId(fileMd5);
//            mediaFiles.setCompanyId(companyId);
//            //媒体类型
//            mediaFiles.setUrl("/" + bucket + "/" + objectName);
//            mediaFiles.setBucket(bucket);
//            mediaFiles.setFilePath(objectName);
//            mediaFiles.setCreateDate(LocalDateTime.now());
//            mediaFiles.setAuditStatus("002003");
//            mediaFiles.setStatus("1");
//            //保存文件信息到文件表
//            int insert = mediaFilesMapper.insert(mediaFiles);
//            if (insert < 0) {
////                log.error("保存文件信息到数据库失败,{}", mediaFiles.toString());
//                XueChengPlusException.cast("保存文件信息失败");
//            }
//            //添加到待处理任务表
//            addWaitingTask(mediaFiles);
////            log.debug("保存文件信息到数据库成功,{}", mediaFiles.toString());
//
//        }
//        return mediaFiles;
//    }


    /**
     * 添加待处理任务
     *
     * @param mediaFiles 媒资文件信息
     */
    private void addWaitingTask(MediaFiles mediaFiles) {

        //文件名称
        String filename = mediaFiles.getFilename();
        //文件扩展名
        String exension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(exension);
        //如果是avi视频添加到视频待处理表
        System.out.println("输入的mimetype为："+mimeType);
        if (mimeType.equals("video/x-msvideo")) {
            System.out.println("输入的文件为avi");
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles, mediaProcess);
            mediaProcess.setStatus("1");//未处理
            mediaProcess.setFailCount(0);//失败次数默认为0
            mediaProcessMapper.insert(mediaProcess);
        }
        //文件名称
//        String filename = mediaFiles.getFilename();
//        //文件扩展名
//        String exension = filename.substring(filename.lastIndexOf("."));
//        //文件mimeType
//        String mimeType = getMimeType(exension);
//        //如果是avi视频添加到视频待处理表
//        if (mimeType.equals("video/x-msvideo")) {//根据对照表，avi的mimeType对应的是x-msvideo
//            MediaProcess mediaProcess = new MediaProcess();
//            BeanUtils.copyProperties(mediaFiles, mediaProcess);
//            mediaProcess.setStatus("1");//未处理
//            mediaProcess.setFailCount(0);//失败次数默认为0
//            mediaProcessMapper.insert(mediaProcess);
//        }
    }

    /**
     * 清除分块文件
     *
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal          分块文件总数
     */
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {

        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("video").objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
//                    log.error("清楚分块文件失败,objectname:{}",deleteError.objectName(),e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("清楚分块文件失败,chunkFileFolderPath:{}",chunkFileFolderPath,e);
        }
    }

//

    /**
     * 从minio下载文件
     *
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinIO(String bucket, String objectName) {
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient
                    .getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    private String getFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
    }

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        //根据媒资文件名称查询
        queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()), MediaFiles::getFilename, queryMediaParamsDto.getFilename());
        //根据媒资类型查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getFileType()), MediaFiles::getFileType, queryMediaParamsDto.getFileType());
        //根据审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getAuditStatus()), MediaFiles::getAuditStatus, queryMediaParamsDto.getAuditStatus());

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;
    }


    @Transactional //事务：确保操作要么同时成功，要么同时失败，若抛出异常，此方法将会回滚事务
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes) {
        //初始化文件id，按二进制字节码生成Md5值，赋值给id
        String fileId = null;
        try {
            fileId = DigestUtils.md5Hex(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            XueChengPlusException.cast("获取文件内容出错");
        }

        //文件名称
        String filename = uploadFileParamsDto.getFilename();
        String objectName = fileId + filename.substring(filename.lastIndexOf("."));
        // 文件存储的目录结构
        String folder = getFileFolder(new Date(), true, true, true);
        objectName = folder + objectName;


        MediaFiles mediaFiles = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(minioBucketFiles)
                            .object(objectName)
                            .stream(
                                    byteArrayInputStream, byteArrayInputStream.available(), -1)
                            .contentType(uploadFileParamsDto.getContentType())
                            .build());

            mediaFiles = getFileById(fileId);
            //主键
            if (mediaFiles == null) {
                mediaFiles = new MediaFiles();
                BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
                mediaFiles.setId(fileId);
                mediaFiles.setFileId(fileId);
                mediaFiles.setCompanyId(companyId);
                mediaFiles.setUrl("/" + minioBucketFiles + "/" + objectName);
                mediaFiles.setBucket("/" + minioBucketFiles + "/" + objectName);
                mediaFiles.setCreateDate(LocalDateTime.now());
                mediaFiles.setStatus("1");
                //写错地方，事实上实在addMediaFileToDb
                //保存文件信息到媒资数据库
                int insert = mediaFilesMapper.insert(mediaFiles);
//                if (insert<=0){
//                    log.debug("向数据库保存文件失败，bucket:{},objectName:{}",minioBucketFiles,objectName);
//                    return null;
//                }

                //记录待处理任务
                //判断如果是avi视频，才去写入待处理任务 （通过MimeType）

                //向MediaProcess插入记录


            }

        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传过程出错");
        }
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    public MediaFiles getFileById(String id) {
        return mediaFilesMapper.selectById(id);

    }
    //根据日期拼接目录

    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuffer folderString = new StringBuffer();
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }


    private String getMimeType(String extension) {
        if (extension == null)
            extension = "";
        //mime:文本图像音频视频专用的数据
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

}
