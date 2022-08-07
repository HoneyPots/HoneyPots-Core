package com.honeypot.domain.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/files")
@RestController
public class FileApi {

    private final FileUploadService fileUploadService;

    @GetMapping("/presigned-url")
    public ResponseEntity<?> getPresignedUrl(@Valid FileUploadDto fileUploadRequest) {
        FileDto fileDto = fileUploadService.upload(fileUploadRequest);

        return ResponseEntity.ok(fileDto);
    }

}
