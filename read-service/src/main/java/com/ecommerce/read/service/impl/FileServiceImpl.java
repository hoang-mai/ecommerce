package com.ecommerce.read.service.impl;

import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url-public}")
    private String urlPublic;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.region:us-east-1}")
    private String region;

    private static final String AWS4_SIGNING_ALGORITHM = "AWS4-HMAC-SHA256";
    private static final String SERVICE = "s3";
    private static final int EXPIRY_SECONDS = 7 * 24 * 60 * 60; // 7 days

    /**
     * Lấy presigned URL từ object path
     * URL có hiệu lực trong 7 ngày
     * Tự ký URL locally mà không cần kết nối đến MinIO server
     *
     * @param objectPath đường dẫn object trong MinIO
     * @return presigned URL
     */
    public String getPresignedUrl(String objectPath) {
        if (objectPath == null || objectPath.isEmpty()) {
            return null;
        }
        try {
            return generatePresignedUrl(objectPath);
        } catch (Exception e) {
            System.out.println("Error generating presigned URL: " + e.getMessage());
            throw new RuntimeException(MessageError.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Tạo presigned URL sử dụng AWS Signature V4
     * Ký locally mà không cần kết nối đến MinIO server
     */
    private String generatePresignedUrl(String objectPath) throws Exception {
        // Parse URL public để lấy host
        String baseUrl = urlPublic.endsWith("/") ? urlPublic.substring(0, urlPublic.length() - 1) : urlPublic;
        String host = baseUrl.replaceAll("https?://", "");

        // Encode object path
        String encodedObjectPath = encodeObjectPath(objectPath);

        // Thời gian hiện tại theo UTC
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String amzDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
        String dateStamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Credential scope
        String credentialScope = dateStamp + "/" + region + "/" + SERVICE + "/aws4_request";

        // Canonical query string
        String canonicalQueryString = "X-Amz-Algorithm=" + AWS4_SIGNING_ALGORITHM
                + "&X-Amz-Credential=" + URLEncoder.encode(accessKey + "/" + credentialScope, StandardCharsets.UTF_8)
                + "&X-Amz-Date=" + amzDate
                + "&X-Amz-Expires=" + EXPIRY_SECONDS
                + "&X-Amz-SignedHeaders=host";

        // Canonical request
        String canonicalRequest = "GET\n"
                + "/" + bucketName + "/" + encodedObjectPath + "\n"
                + canonicalQueryString + "\n"
                + "host:" + host + "\n"
                + "\n"
                + "host\n"
                + "UNSIGNED-PAYLOAD";

        // String to sign
        String stringToSign = AWS4_SIGNING_ALGORITHM + "\n"
                + amzDate + "\n"
                + credentialScope + "\n"
                + sha256Hex(canonicalRequest);

        // Signing key
        byte[] signingKey = getSignatureKey(secretKey, dateStamp, region, SERVICE);

        // Signature
        String signature = bytesToHex(hmacSha256(signingKey, stringToSign));

        // Final URL
        return baseUrl + "/" + bucketName + "/" + encodedObjectPath
                + "?" + canonicalQueryString
                + "&X-Amz-Signature=" + signature;
    }

    private String encodeObjectPath(String objectPath) {
        // Encode từng phần của path
        String[] parts = objectPath.split("/");
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) encoded.append("/");
            encoded.append(URLEncoder.encode(parts[i], StandardCharsets.UTF_8).replace("+", "%20"));
        }
        return encoded.toString();
    }

    private byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSha256(kSecret, dateStamp);
        byte[] kRegion = hmacSha256(kDate, regionName);
        byte[] kService = hmacSha256(kRegion, serviceName);
        return hmacSha256(kService, "aws4_request");
    }

    private byte[] hmacSha256(byte[] key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private String sha256Hex(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
