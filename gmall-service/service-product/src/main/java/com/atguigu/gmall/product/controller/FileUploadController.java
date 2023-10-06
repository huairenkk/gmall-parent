package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/admin/product")
public class FileUploadController {

    @Value("${minio.endpointUrl}")
    private String endpointUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String bucketName;


    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    //admin/product/fileUpload
    @PostMapping("/fileUpload")
    public Result fileUpload(MultipartFile file) {
        String url = null;
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(endpointUrl)
                            .credentials(accessKey, secretKey)
                            .build();


            //查询指定桶是否存在
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                //如果桶不存在就创建桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                System.out.println("Bucket 'gmall' already exists.");
            }

            //上传文件名
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String dateString = dateFormat.format(date);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String fileName = file.getName();
            fileName = dateString + "/" + uuid + fileName;
            //实现文件内容的上传
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).
                            object(fileName).
                            stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            //组合访问的路径
            url = endpointUrl + "/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(url);
    }
}
