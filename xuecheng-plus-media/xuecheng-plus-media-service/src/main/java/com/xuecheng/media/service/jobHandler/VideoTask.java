package com.xuecheng.media.service.jobHandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;


/**
 * 视频处理任务类
 */
@Component
@Slf4j
public class VideoTask {

    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    String ffmpeg_path;

    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {
        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        //确定CPU的核心数量
        Runtime.getRuntime().availableProcessors();

        //查询待处理的任务(正在执行的机器总数、目前机器的编号、本次查询的数量)
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardTotal, shardIndex, 5);
        //任务数量
        int size = mediaProcessList.size();
        if (size <= 0) {
            log.debug("无可执行任务，得到的任务数：" + size);
            return;
        } else {
            log.debug("获取任务数量：" + size);
        }
        //创建一个线程值
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //使用计数器（进行线程阻塞
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            executorService.execute(() -> {

                try {
                    //抢任务（开启任务：获取任务ID
                    Long id = mediaProcess.getId();
                    /*开启任务*/
                    boolean b = mediaFileProcessService.startTask(id);
                    if (!b) {
                        log.debug("抢占任务失败，任务id:{}", id);
                        return;
                    }

                    //执行视频的转码
                    String bucket = mediaProcess.getBucket();
                    String objectName = mediaProcess.getFilePath();
                    //下载视频
                    File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);
                    if (file == null) {
                        log.debug("下载视频出错，任务id:{},bucket:{},objectName:{}", id, bucket, objectName);
                        return;
                    }
                    //文件id就是md5值
                    String fileId = mediaProcess.getFileId();
                    //视频avi源路径（名称并不叫xxx.avi，这是一个临时创建的Temp目录下的文件，后缀名是.merge）
                    String video_path = file.getAbsolutePath();
                    //视频转换后的名称
                    String mp4_name = fileId + ".mp4";
                    //转换后的mp4文件路径
                    //创建一个临时路径
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件异常,{}", e.getMessage());
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, "创建临时文件异常");
                        return;
                    }

                    String mp4_path = mp4File.getAbsolutePath();

                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4_path);
//                    Mp4VideoUtil tmp_videoUtil = new Mp4VideoUtil(tmp_ffmpeg_path, tmp_video_path, tmp_mp4_name, tmp_mp4_path);
                    /*开始视频转换成功返回success*/
                    String result = videoUtil.generateMp4();
                    if (!result.equals("success")) {
                        log.debug("视频转换失败失败原因:{}bucket:{},objectName:{}", result, bucket, objectName);
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, result);
                        return;
                    }

                    log.debug("视频转换成功");

                    String mp4ObjectName = objectName.substring(0, objectName.lastIndexOf(".")) + ".mp4";

                    //上传到Minio
                    boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", bucket, mp4ObjectName);
                    if (!b1) {
                        log.debug("上传mp4文件到Minio失败任务ID:{}bucket:{},objectName:{}", id, bucket, objectName);
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, "上传mp4到Minio失败");
                        return;
                    }
                    log.debug("视频上传成功");

                    /*mp4文件的url*/
                    String mp4Url = getFilePath(fileId, ".mp4");

                    //任务成功，保存任务处理结果
                    mediaFileProcessService.saveProcessFinishStatus(id, "2", fileId, mp4Url, "创建文件成功");

                    log.debug("处理结果保存成功");

                    //计数器减去1
                    countDownLatch.countDown();

                } finally {
                    //最终都要释放一次，每个线程都会减一
                    countDownLatch.countDown();
                }
            });


        });

        countDownLatch.await(30, TimeUnit.MINUTES);//执行阻塞,给出充裕的时间（30分钟

    }

    private String getFilePath(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }


}
