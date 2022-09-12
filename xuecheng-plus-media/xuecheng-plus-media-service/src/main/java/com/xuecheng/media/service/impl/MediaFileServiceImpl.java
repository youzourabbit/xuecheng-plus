package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/9/10 8:58
 * @version 1.0
 */
 @Service
public class MediaFileServiceImpl implements MediaFileService {

 @Autowired
 MinioClient minioClient;

 @Autowired
 MediaFilesMapper mediaFilesMapper;

 @Value("${minio.bucket.files}")
 private String minioBucketFiles;


 @Override
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
  LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
  //根据媒资文件名称查询
  queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()),MediaFiles::getFilename,queryMediaParamsDto.getFilename());
  //根据媒资类型查询
  queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getFileType()),MediaFiles::getFileType,queryMediaParamsDto.getFileType());
  //根据审核状态查询
  queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getAuditStatus()),MediaFiles::getAuditStatus,queryMediaParamsDto.getAuditStatus());

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


 @Transactional
 @Override
 public UploadFileResultDto uploadFile(Long companyId,UploadFileParamsDto uploadFileParamsDto,byte[] bytes) {
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
           PutObjectArgs.builder().bucket(minioBucketFiles).object(objectName).stream(
                           byteArrayInputStream, byteArrayInputStream.available(), -1)
                   .contentType(uploadFileParamsDto.getContentType())
                   .build());

   mediaFiles = getFileById(fileId);
   //主键
   if(mediaFiles==null){
    mediaFiles = new MediaFiles();
    BeanUtils.copyProperties(uploadFileParamsDto,mediaFiles);
    mediaFiles.setId(fileId);
    mediaFiles.setFileId(fileId);
    mediaFiles.setCompanyId(companyId);
    mediaFiles.setUrl("/"+minioBucketFiles+"/"+objectName);
    mediaFiles.setBucket("/"+minioBucketFiles+"/"+objectName);
    mediaFiles.setCreateDate(LocalDateTime.now());
    mediaFiles.setStatus("1");
    //保存文件信息到媒资数据库
    int insert = mediaFilesMapper.insert(mediaFiles);
   }

  } catch (Exception e) {
   e.printStackTrace();
   XueChengPlusException.cast("上传过程出错");
  }
  UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
  BeanUtils.copyProperties(mediaFiles,uploadFileResultDto);
  return uploadFileResultDto;
 }

 public MediaFiles getFileById(String id){
  return  mediaFilesMapper.selectById(id);

 }

 //根据日期拼接目录
 private String getFileFolder(Date date, boolean year, boolean month, boolean day){
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  //获取当前日期字符串
  String dateString = sdf.format(new Date());
  //取出年、月、日
  String[] dateStringArray = dateString.split("-");
  StringBuffer folderString = new StringBuffer();
  if(year){
   folderString.append(dateStringArray[0]);
   folderString.append("/");
  }
  if(month){
   folderString.append(dateStringArray[1]);
   folderString.append("/");
  }
  if(day){
   folderString.append(dateStringArray[2]);
   folderString.append("/");
  }
  return folderString.toString();
 }
}
