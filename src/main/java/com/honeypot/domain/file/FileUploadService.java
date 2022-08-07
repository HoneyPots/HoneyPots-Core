package com.honeypot.domain.file;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileUploadService {

    private final FileMapper fileMapper;

    private final FileRepository fileRepository;

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.presigned-url.expiration-time-in-seconds}")
    private long presignedUrlExpirationTimeInSecond;

    @Transactional
    public FileDto upload(FileUploadDto file) {
        // TODO check file extension

        String newFileName = generateUniqueFileName(file.getFilename());

        FileDto fileDto = FileDto.builder()
                .filename(newFileName)
                .originalFilename(file.getFilename())
                .filePath(file.getFileType().fileDirectory + newFileName)
                .fileType(file.getFileType())
                .build();

        File uploaded = fileRepository.save(fileMapper.toEntity(fileDto));
        FileDto result = fileMapper.toDto(uploaded);

        // generate presigned url
        result.setPresignedUrl(generatePresignedUrl(result.getFilePath()));

        return result;
    }

    private String generateUniqueFileName(String filename) {
        // TODO change unique name generation strategy
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + "_" + filename;
    }


    public String generatePresignedUrl(String filePath) {
        GeneratePresignedUrlRequest preSignedUrlRequest
                = new GeneratePresignedUrlRequest(bucket, filePath)
                .withMethod(HttpMethod.PUT)
                .withExpiration(new Date(System.currentTimeMillis() +
                        presignedUrlExpirationTimeInSecond * 1000));

        preSignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return amazonS3Client.generatePresignedUrl(preSignedUrlRequest).toExternalForm();
    }

}
