package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.MinioException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @description 测试MinIO
 * @author Mr.M
 * @date 2022/9/11 21:24
 * @version 1.0
 */
public class MinIOTest {

 static MinioClient minioClient =
         MinioClient.builder()
                 .endpoint("http://192.168.101.65:9000")
                 .credentials("minioadmin", "minioadmin")
                 .build();


 //上传文件
 public static void upload()throws IOException, NoSuchAlgorithmException, InvalidKeyException {
  try {
   boolean found =
           minioClient.bucketExists(BucketExistsArgs.builder().bucket("testbucket").build());
   //检查testbucket桶是否创建，没有创建自动创建
   if (!found) {
    minioClient.makeBucket(MakeBucketArgs.builder().bucket("testbucket").build());
   } else {
    System.out.println("Bucket 'testbucket' already exists.");
   }
   //上传1.mp4
//   minioClient.uploadObject(
//           UploadObjectArgs.builder()
//                   .bucket("testbucket")
//                   .object("1.mp4")
//                   .filename("D:\\develop\\upload\\1.mp4")
//                   .build());

   //上传到avi子目录
//   minioClient.uploadObject(
//           UploadObjectArgs.builder()
//                   .bucket("testbucket")
//                   .object("avi/1.avi")
//                   .filename("D:\\develop\\upload\\1.avi")
//                   .build());

   //用流的方式上传1.avi,上传到avi子目录
   File file = new File("D:\\develop\\upload\\1.jpg");
   FileInputStream fileInputStream = new FileInputStream(file);
   byte[] bytes = IOUtils.toByteArray(fileInputStream);
   ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
   String contentType = Files.probeContentType(file.toPath());
   minioClient.putObject(
           PutObjectArgs.builder().bucket("testbucket").object("jpg/1.jpg").stream(
                           byteArrayInputStream, byteArrayInputStream.available(), -1)
                   .contentType(contentType)
                   .build());

   System.out.println("上传成功");
  } catch (MinioException e) {
   System.out.println("Error occurred: " + e);
   System.out.println("HTTP trace: " + e.httpTrace());
  }

 }


 //删除文件
 public static void delete(String bucket,String filepath)throws IOException, NoSuchAlgorithmException, InvalidKeyException {
  try {

   minioClient.removeObject(
           RemoveObjectArgs.builder().bucket(bucket).object(filepath).build());
   System.out.println("删除成功");
  } catch (MinioException e) {
   System.out.println("Error occurred: " + e);
   System.out.println("HTTP trace: " + e.httpTrace());
  }

 }

 //下载文件
 public static void getFile(String bucket,String filepath,String outPath)throws IOException, NoSuchAlgorithmException, InvalidKeyException {
  try {

   String path = outPath+filepath;
   path = path.substring(0,path.lastIndexOf(".")-1);
   File folder = new File(path);
   if(!folder.exists()){
    folder.mkdirs();
   }
   try (InputStream stream = minioClient.getObject(
           GetObjectArgs.builder()
                   .bucket(bucket)
                   .object(filepath)
                   .build());
        FileOutputStream fileOutputStream = new FileOutputStream(new File(outPath + filepath));
   ) {

    // Read data from stream
    IOUtils.copy(stream,fileOutputStream);
    System.out.println("下载成功");
   }

  } catch (MinioException e) {
   System.out.println("Error occurred: " + e);
   System.out.println("HTTP trace: " + e.httpTrace());
  }

 }


 public static void uploadByInterface(){
  UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();

  File file = new File("D:\\minio\\upload\\3.mp4");
  FileInputStream fis = null;
  byte[] bytes = null;
  try {
   fis = new FileInputStream(file);
   bytes = new byte[(int) file.length()];
   fis.read(bytes);
   fis.close();
  } catch (IOException e) {
   e.printStackTrace();
  }


  uploadFileParamsDto.setFileSize(file.length());
  uploadFileParamsDto.setFileType("001001");
  uploadFileParamsDto.setTags("课程视频");
  uploadFileParamsDto.setRemark("");//备注
  uploadFileParamsDto.setFilename(file.getName());
  String fileName = file.getName();
  String fileExtension = fileName.substring(fileName.lastIndexOf("."));
  ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(fileExtension);
  String mimeType = extensionMatch.getMimeType();
  uploadFileParamsDto.setContentType(mimeType);
//  mediaFileService.uploadFile(1L,uploadFileParamsDto,bytes);
 }

 public static void main(String[] args)throws IOException, NoSuchAlgorithmException, InvalidKeyException {
  upload();
//  delete("testbucket","1.mp4");
//  delete("testbucket","avi/1.avi");

//  getFile("testbucket","avi/1.avi","D:\\develop\\minio_data\\");
//  uploadByInterface();
 }


}
