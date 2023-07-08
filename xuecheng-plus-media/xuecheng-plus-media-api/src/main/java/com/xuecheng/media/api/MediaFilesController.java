package com.xuecheng.media.api;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }

    @ApiOperation("上传图片")
    @RequestMapping(value = "/upload/coursefile"/*此处额外添加*/, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    // MultipartFile是一个解决原生，上传二进制HttpServletRequest数据时，需要自己再进行转换为File类的工具类，可以简化上传操作
    public UploadFileResultDto upload(@RequestParam("filedata") MultipartFile upload) throws IOException {

        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileSize(upload.getSize());
        uploadFileParamsDto.setFileType("001001");
        uploadFileParamsDto.setTags("课程图片");
        uploadFileParamsDto.setRemark("");//备注
        uploadFileParamsDto.setFilename(upload.getOriginalFilename());
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(upload.getOriginalFilename());
        String mimeType = extensionMatch.getMimeType();
        uploadFileParamsDto.setContentType(mimeType);
        return mediaFileService.uploadFile(companyId, uploadFileParamsDto, upload.getBytes());
    }

//    @ApiOperation("上传文件")
//    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile upload) throws IOException {
//
//        return null;
//    }



}
