package com.media_service.Controllers;

import com.media_service.DTO.MediaDeleteRequest;
import com.media_service.DTO.MediaUploadResponse;
import com.media_service.Services.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {
    private final MediaService mediaService;


    @PostMapping(value = "/upload",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadImage(
            @RequestPart("file")MultipartFile file,
            @RequestParam("folder")String folder
    ){
        MediaUploadResponse response = this.mediaService.uploadImage(file,folder);
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/upload/multiple",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadMultipleImage(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("folder")String folder
    ){
        List<MediaUploadResponse> responses = this.mediaService.uploadMultipleImage(files,folder);
        return  ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(
            @Valid @RequestBody MediaDeleteRequest request) {

        mediaService.deleteImage(request.publicId());
        return ResponseEntity.noContent().build();
    }
}
