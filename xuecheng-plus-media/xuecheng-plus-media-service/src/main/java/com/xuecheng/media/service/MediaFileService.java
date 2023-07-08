package com.xuecheng.media.service;

import com.xuecheng.base.execption.RestResponse;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

 /**
  * @description 上传文件
  * @param uploadFileParamsDto  上传文件信息
  * @return com.xuecheng.media.model.dto.UploadFileResultDto 上传文件结果
  * @author Mr.M
  * @date 2022/9/12 19:31
 */
 public UploadFileResultDto uploadFile(Long companyId,UploadFileParamsDto uploadFileParamsDto,byte[] bytes);



 /**
  * @description 检查文件是否存在
  * @param fileMd5 文件的md5
  * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
  * @author Mr.M
  * @date 2022/9/13 15:38
  */
 public RestResponse<Boolean> checkFile(String fileMd5);

 /**
  * @description 检查分块是否存在
  * @param fileMd5  文件的md5
  * @param chunkIndex  分块序号
  * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
  * @author Mr.M
  * @date 2022/9/13 15:39
  */
 public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

 /**
  * @description 上传分块
  * @param fileMd5  文件md5
  * @param chunk  分块序号
  * @param bytes  文件字节
  * @return com.xuecheng.base.model.RestResponse
  * @author Mr.M
  * @date 2022/9/13 15:50
  */
 public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath);

RestResponse mergeChunk(Long companyId,String fileMd5, String fileName, int chunkTotal,UploadFileParamsDto uploadFileParamsDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

MediaFiles addMediaFileToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

}
