package com.ecommerce.read.service.impl;

import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;


    /**
     * Lấy presigned URL từ object path
     * URL có hiệu lực trong 7 ngày
     *
     * @param objectPath đường dẫn object trong MinIO
     * @return presigned URL
     */
    public String getPresignedUrl(String objectPath) {
        if (objectPath == null || objectPath.isEmpty()) {
            return null;
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectPath)
                            .expiry(7 * 24 * 60 * 60)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(MessageError.FILE_UPLOAD_FAILED);
        }
    }


}
