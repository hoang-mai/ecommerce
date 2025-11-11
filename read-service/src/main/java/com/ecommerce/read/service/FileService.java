package com.ecommerce.read.service;


public interface FileService {

    /**
     * Lấy presigned URL từ object path
     *
     * @param objectPath đường dẫn object trong MinIO
     * @return presigned URL có hiệu lực 7 ngày
     */
    String getPresignedUrl(String objectPath);

}
