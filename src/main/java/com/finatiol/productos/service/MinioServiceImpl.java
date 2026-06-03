package com.finatiol.productos.service;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioServiceImpl implements MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioServiceImpl.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.public-url}")
    private String minioPublicUrl;

    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void init() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucket).build());
                String policy = """
                        {"Version":"2012-10-17","Statement":[{"Effect":"Allow",\
                        "Principal":{"AWS":["*"]},"Action":["s3:GetObject"],\
                        "Resource":["arn:aws:s3:::%s/*"]}]}""".formatted(bucket);
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
                log.info("[MinIO] Bucket '{}' creado con política de lectura pública", bucket);
            }
        } catch (Exception e) {
            log.error("[MinIO] Error inicializando bucket: {}", e.getMessage());
        }
    }

    @Override
    public String subirImagen(MultipartFile file, String objectName) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return minioPublicUrl + "/" + bucket + "/" + objectName;
        } catch (Exception e) {
            throw new RuntimeException("Error al subir imagen a MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarImagen(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(objectName).build());
        } catch (Exception e) {
            log.warn("[MinIO] Error al eliminar imagen '{}': {}", objectName, e.getMessage());
        }
    }
}
