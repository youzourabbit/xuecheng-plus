package com.xuecheng.media;

import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class BigFileTest {

    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    //测试：对本地文件进行分块
    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("D:\\minio\\upload\\2.mp4");
        String chunkFilePah = "D:\\minio\\upload\\chunk\\";
        int chunkSize = 1024 * 1024 * 5;
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");

        //缓冲区
        byte[] bytes = new byte[1024 * 1024];
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePah + i);
            //分块文件的写入流
            RandomAccessFile raf_w = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_w.write(bytes, 0, len);
                if (chunkFile.length() >= chunkSize) {
                    break;
                }
            }
            raf_w.close();
        }
        raf_r.close();
    }


    //合并文件测试
    @Test
    public void testMerge() throws IOException {
        //块文件目录
        File chunkFolder = new File("D:\\minio\\upload\\chunk\\");
        //源文件(用于生成原始应有的md5值，最终进行校验）
        File sourceFile = new File("D:\\minio\\upload\\2.mp4");
        //合并后的文件
        File mergeFile = new File("D:\\minio\\upload\\合并后的文件.mp4");

        //取出刚才所有的分块文件,存进数组
        File[] files = chunkFolder.listFiles();//此时顺序不一定格式正确的

        //Java自带的工具类：将数组转为list
        List<File> filesForList = Arrays.asList(files);

        Collections.sort(filesForList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {//比较器
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });

        //向合并文件写的流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");

        //缓冲区
        byte[] bytes = new byte[1024 * 1024 * 5];

        //遍历分块文件，向合并的文件写
        for (File file : filesForList) {
            //读取数据
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            //
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
            }
            raf_r.close();

        }
        raf_rw.close();

        //合并文件完成，对比传输前后是否一致
        FileInputStream fis_merge = new FileInputStream(mergeFile);
        FileInputStream fis_source = new FileInputStream(sourceFile);
        String md5_merge = DigestUtils.md5Hex(fis_merge);

        String md5_source = DigestUtils.md5Hex(fis_source);

        if (md5_merge.equals(md5_source)) {
            System.out.println("文件合并结束，校验结果为：一致");
        } else {
            System.out.println("文件合并结束，校验结果为：不一致");
        }
    }

    //将分块上传至minio
    @Test
    public void uploadChunk() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        for (int i = 0; i < 199; i++) {
            UploadObjectArgs uoa = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .filename("D:\\minio\\upload\\chunk\\" + i)
                    .object("chunk/" + i)
                    .build();
            minioClient.uploadObject(uoa);
        }
        System.out.println("上传成功！");
    }

    @Test
    public void mergeForMinio() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<ComposeSource> sources = new ArrayList<>();
        //指定分块文件信息
        for (int i = 0; i < 199; i++) {
            ComposeSource source = ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build();
            sources.add(source);
        }
        ComposeObjectArgs testbucket =
                ComposeObjectArgs
                        .builder()
                        .bucket("testbucket")
                        .object("chunk合并后的文件.mp4")
                        .sources(sources)
                        .build();
        minioClient.composeObject(testbucket);
    }
}
