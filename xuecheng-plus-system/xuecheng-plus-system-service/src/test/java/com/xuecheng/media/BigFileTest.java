package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author = Ming
 * @decription 用于将大文件分块上传，再进行拼接，用于处理文件传输中途的意外状况
 * @date = 2023/5/23
 * @time = 23:15
 */
public class BigFileTest {
    @Test
    public void testChunk() throws IOException {//分块
        File sourceFile = new File("F:\\minio\\upload\\3.avi");
        //分块文件存储路径
        String chunkFilePath = "F:\\minio\\upload\\chunk\\";
        //分块文件大小
        int chunkSize = 1024 * 1024 * 4;//单位：字节
        //分块文件个数
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);//方法--向上取整，得到最终应该分块的数量
        //使用流从源文件读取数据，向分块文件中写数据
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");//具有读写两种功能，模式为只读

        //缓冲区
        byte[] bytes = new byte[1024 * 1024 * 4];//缓冲区过小可能会导致传输速率极大减慢

        for (int i = 0; i < chunkNum; i++) {
            //准备向哪个文件写--》创建新的临时文件
            File chunkFile = new File(chunkFilePath + i);
            //分块写入文件流
            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
                if (chunkFile.length() >= chunkSize) {
                    break;
                }
            }
            raf_rw.close();
        }
        raf_r.close();
    }

    @Test
    public void testMerge() throws IOException {//合并
        //块文件目录
        File chunkFolder = new File("F:\\minio\\upload\\chunk\\");
        //原始文件
        File originalFile = new File("F:\\minio\\upload\\3.avi");
        //合并文件
        File mergeFile = new File("F:\\minio\\upload\\4.avi");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024 * 1024 * 4];
        //分块列表,列出所有文件，变成一个数组对象
        File[] fileArray = chunkFolder.listFiles();
        // 数组转成集合，便于排序
        List<File> fileList = Arrays.asList(fileArray);
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {//对比（源码是static void sort(List<T> list,Comparator<? super T> c){list.sort(c);}）
                //创建chunk目录下的文件时默认是1.2.3....n，但是实际上有可能不按顺序读取
                //compare 的基本原理：
                //return 0:不交换位置，不排序
                //return 1:交换位置
                //return -1:不交换位置
                //return o1-o2:升序排列
                //return o2-o1:降序排列
                //o1 - o2，只要前一个数比后一个大，就必须换位，最终索引值最小的是最小值并以此类推
                //最终会得到一个升序的fileList
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //合并文件
        for (File chunkFile : fileList) {//对fileList遍历
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");//用于写入数据，模式rw
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {//这个b是一个数据缓冲区
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();

        //校验文件(也算是一种仅作为示范
        try (
                FileInputStream fileInputStream = new FileInputStream(originalFile);//源文件
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);//拼接后的文件
        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }
        }
    }
}


