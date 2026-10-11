package com.pranit.workspace.minio.service.impl;

import com.pranit.workspace.minio.api.MinioService;
import com.pranit.workspace.minio.properties.MinioProperties;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    @Override
    public void upload
            (final String objectName, final InputStream inputStream,
             final long size, final String contentType) throws Exception {
        this.minioClient.putObject(PutObjectArgs.builder()
                .bucket(this.properties.bucketName())
                .object(objectName)
                .stream(inputStream, size, (long) -1)
                .contentType(contentType)
                .build());
    }

    @Override
    public InputStream download(final String objectName) throws Exception {
        return this.minioClient.getObject(GetObjectArgs.builder()
                .bucket(this.properties.bucketName())
                .object(objectName)
                .build());
    }

    @Override
    public void delete(final String objectName) throws Exception {
        this.minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(this.properties.bucketName())
                .object(objectName)
                .build());
    }

    @Override
    public boolean exists(final String objectName) throws Exception {
        try {
            this.minioClient.statObject(StatObjectArgs.builder()
                    .bucket(this.properties.bucketName())
                    .object(objectName)
                    .build());
            return true;
        } catch (ErrorResponseException exception) {
            if ("NoSuchKey".equals(exception.errorResponse().code())
                    || "NoSuchObject".equals(exception.errorResponse().code())) {
                return false;
            }
            throw exception;
        }
    }

    @Override
    public String getPresignedUrl(final String objectName) throws Exception {
        return this.minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Http.Method.GET)
                .bucket(this.properties.bucketName())
                .object(objectName)
                .expiry(7, TimeUnit.DAYS)
                .build());
    }
}
