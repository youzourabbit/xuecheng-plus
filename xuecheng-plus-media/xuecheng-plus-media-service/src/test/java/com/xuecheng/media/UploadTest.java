package com.xuecheng.media;

import com.xuecheng.media.model.dto.UploadFileResultDto;
import io.swagger.annotations.ApiOperation;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author = Ming
 * @date = 2023/5/21
 * @time = 21:36
 */
public class UploadTest {

    @Test
    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile upload)throws IOException {
        return null;
    }

}
