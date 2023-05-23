package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * @author Mr.M
 * @version 1.0
 * @description 测试minio的sdk
 * @date 2023/2/17 11:55
 */
public class MinioTest {
    /*写一个测试类，测试minio的上传与下载*/
    /*minioClient是minio提供的接口，用于对接minio系统的上传与下载*/
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.153.135:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void test_upload() throws Exception {

        // 在下方UploadObjectArgs的创建探讨了一下文件类型的处理方式，再相应的，还有一种更加直接的文件类型归类方法：通过拓展名直接获取媒体类型：mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch("mp3");//通过输入拓展名直接获取mimeType
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimetype,字节流（意义是未知类型）
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();//假如成功获取到了这种拓展名的mimetype类型，就将字符赋值到mimeType,否则mimeType将保持未知类型
        }

        //尝试添加新文件
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs
                .builder()
                .bucket("mybucket")//确定bucket的名称
                .filename("F:\\音乐\\Ayasa绚沙 - 告白の夜 (告白之夜)(Live)(1).mp3")//找到需要上传的文件
                .object("test/music/告白之夜.mp3")//确定文件进入数据仓库后的名称
                //.contentType("music/mp3")//设置媒体文件类型--具体有没有MP3属于未知，临时写的没试过
                //.contentType(MediaType.IMAGE_JPEG_VALUE)//相应的，可以使用mediaType对数据类型进行确认
                .contentType(mimeType)//使用处理过的mimeType赋值，要么未知，要么获取成功
                .build();//参数确定，创建成功，然后再按照其返回的对象进行接收
        minioClient.uploadObject(uploadObjectArgs);//真正执行上传操作——得知真正需要的对象再进行创建（UploadObjectArgs，
        // 它可能抛出异常，使用官方提供的方法接收


        //通过扩展名得到媒体资源类型 mimeType
        //根据扩展名取出mimeType
       /* ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }*/

        //上传文件的参数信息
     /*   UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("mybucket")//桶
                .filename("F:/音乐/Ayasa绚沙 - 告白の夜 (告白之夜)(Live)(1).mp3") //指定本地文件路径
//                .object("1.mp4")//对象名 在桶下存储该文件
                .object("test/01/1.mp4")//对象名 放在子目录下
                .contentType(mimeType)//设置媒体文件类型
                .build();

        //上传文件
        minioClient.uploadObject(uploadObjectArgs);*/


    }

    @Test
    public void baseTest(){
        System.out.println("dabcdefg".lastIndexOf("d"));
    }

    //删除文件
    @Test
    public void test_delete() throws Exception {

        //RemoveObjectArgs
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs
                .builder()
                .bucket("mybucket")
                .object("Ayasa绚沙 - 告白の夜 (告白之夜)(Live)(1).mp3")
                .build();

        //删除文件
        minioClient.removeObject(removeObjectArgs);
    }

    //查询文件 从minio中下载
    @Test
    public void test_getFile() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("mybucket").object("test/music/告白之夜.mp3").build();
        try (
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);//这一步就已经下载完成，后续全是校验步骤
                FileOutputStream outputStream = new FileOutputStream(new File("F:\\minio\\download\\1.mp3"));//将文件输出流定位到下载
        ) {
            IOUtils.copy(inputStream, outputStream);//使用IOUtils将数据转移到指定文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }

        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        FileOutputStream outputStream1 = new FileOutputStream("F:\\minio\\download\\1a.mp3");//怎么看都是在应付啊，拿下载好的自我对比......
        IOUtils.copy(inputStream,outputStream1);
        //文件是从网络路径下载的，转换为md5后总会与本地文件不一致，这里只是对md5的用法做一个示范，以后md5校验码要从服务器直接发送

        //校验文件的完整性对文件的内容进行md5
        FileInputStream fileInputStream1 = new FileInputStream(new File("F:\\minio\\download\\1.mp3"));
        //错误：系统找不到指定文件
        String source_md5 = DigestUtils.md5Hex(fileInputStream1);
        FileInputStream fileInputStream = new FileInputStream(new File("F:\\minio\\download\\1a.mp3"));
        String local_md5 = DigestUtils.md5Hex(fileInputStream);
        if (source_md5.equals(local_md5)) {
            System.out.println("下载成功");
        } else {
            System.out.println("下载失败");
        }



        /*GetObjectArgs getObjectArgs = GetObjectArgs
                .builder()
                .bucket("mybucket")
                .object("test/music/告白之夜.mp3")//云端的文件名
                .build();
        //查询远程服务获取到一个流对象
        FilterInputStream inputStream =
                minioClient
                .getObject(getObjectArgs);

        FilterInputStream inputStream2 =
                minioClient.getObject(getObjectArgs);
*/        //指定输出流
       /* FileOutputStream outputStream =
                new FileOutputStream(new File("F:\\minio\\download\\new_file.mp3"));//下载到本地文件名更改
        IOUtils.copy(inputStream, outputStream);*/

        // 执行校验
       /* String source_md5 = DigestUtils.md5Hex(inputStream);
        String local_md5 = DigestUtils.md5Hex(new FileInputStream(new File("F:\\minio\\download\\告白之夜.mp3")));
        if (source_md5.equals(local_md5)){
            System.out.println("数据校验成功，传输的文件没有损坏");
        }else {
            System.out.println("数据校验失败，数据可能破损或不完整");
        }*/
        //经测试，对该数据源进行直接检测，前后数据的md5码并不一致，若是直接从路径提取无法进行md5校验，必须进行改造

        // 指定输出流
        /*FileOutputStream fileOutputStream = new FileOutputStream(new File("F:\\minio\\download\\告白之夜.mp3"));
        //大概是使用outputStream在本地虚拟一个与服务器端文件名相同，与本地文件路径名相同的文件
        IOUtils.copy(inputStream,fileOutputStream);
        FileInputStream fileInputStream1 = new FileInputStream(new File("F:\\minio\\download\\new_download"));//本地文件名1*/
        /*String source = DigestUtils.md5Hex(inputStream);
        String local = DigestUtils.md5Hex(inputStream2);//虽然不是很懂老师这么做的理由，不过还是照做好了——用两个下载到本地的文件进行md5校验

        if (source.equals(local)){
            System.out.println("下载成功");
        }else {
            System.out.println("数据校验失败，数据可能破损或不完整");*/
    }

    //校验文件的完整性对文件的内容进行md5
        /*FileInputStream fileInputStream1 =
                new FileInputStream(new File("F:\\minio\\download\\new_download.mp3"));
        String source_md5 = DigestUtils
                .md5Hex(fileInputStream1);//第一个md5校验码——来自本地下载文件
        FileInputStream fileInputStream =
                new FileInputStream(new File("F:\\minio\\download\\new_download.mp3"));
        String local_md5 = DigestUtils
                .md5Hex(inputStream);//第二个md5校验码——来自源数据
        if (source_md5.equals(local_md5)) {
            System.out.println("下载成功");//远程流，不稳定，且不匹配
        }*/

}

