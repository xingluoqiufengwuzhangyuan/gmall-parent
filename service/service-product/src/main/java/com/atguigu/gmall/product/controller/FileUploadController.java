package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/2 21:20
 * @Version 1.8
 */
@RestController
@RequestMapping("admin/product")
@Api("文件上传管理器")
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileUrl; // fileUrl = http://192.168.200.128:8080/



    @PostMapping("fileUpload")
    @ApiOperation(value = "文件上传接口")
    public Result fileUpload(MultipartFile file) throws Exception {
        String configFile = this.getClass().getResource("/tracker.conf").getFile();
        String path = "";
        if (configFile != null) {
            ClientGlobal.init(configFile);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();
            StorageClient1 storageClient1 = new StorageClient1(connection, null);
            String extname = FilenameUtils.getExtension(file.getOriginalFilename());
            path = storageClient1.upload_appender_file1(file.getBytes(), extname, null);
            System.out.println("文件路径：\t"+path);

        }
        return Result.ok(fileUrl + path);
    }
}
