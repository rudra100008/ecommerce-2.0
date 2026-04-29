package com.media_service.ServiceImpls;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.media_service.DTO.MediaUploadResponse;
import com.media_service.Exceptions.MediaUploadException;
import com.media_service.Services.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/jpg", "image/webp"
    );
    private static final long MAX_SIZE = 10L * 1024 * 1024;
    @Override
    public MediaUploadResponse uploadImage(MultipartFile imageFile, String folder) {
        validateFile(imageFile);

        try{
            log.info("Uploading file to folder:{}",folder);
            Map uploadResult = cloudinary.uploader().upload(
                    imageFile.getBytes(),
                    ObjectUtils.asMap(
                            "folder",folder,
                            "resource_type","image",
                            "use_filename",true,
                            "unique_filename",true
                    )
            );

            log.info("Image uploaded successfully: {}",uploadResult.get("public_id"));

            return new MediaUploadResponse(
                    (String) uploadResult.get("secure_url"),
                    (String) uploadResult.get("public_id"),
                    folder,
                    imageFile.getSize(),
                    (String) uploadResult.get("format")
            );
        }catch (IOException e){
            log.error("Failed to upload image:{}",e.getMessage());
            throw new MediaUploadException("Image upload failed: "+e.getMessage());
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try{
            log.info("Deleting image with publicId:{}",publicId);
            Map result = cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
            if(!"ok".equals(result.get("result"))){
                throw new MediaUploadException("Failed to delete image: "+ publicId);
            }
            log.info("Image deleted successfully: {}", publicId);

        } catch (IOException e) {
            log.error("Failed to delete image: {}", e.getMessage());
            throw new MediaUploadException("Image delete failed: " + e.getMessage());
        }
    }

    @Override
    public List<MediaUploadResponse> uploadMultipleImage(List<MultipartFile> imageFiles, String folder) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new MediaUploadException("No files provided");
        }

        log.info("Uploading {} images to folder: {}", imageFiles.size(), folder);

        return imageFiles.stream()
                .map(file -> uploadImage(file, folder))
                .toList();
    }


    private void validateFile(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw  new MediaUploadException("No files provided.");
        }

        if(!ALLOWED_TYPES.contains(file.getContentType())){
            throw new MediaUploadException(String.format(
                    "Invalid file type: %s. Allowed: %s"
            ,file.getContentType(),ALLOWED_TYPES));
        }

        if (file.getSize() > MAX_SIZE){
            throw new MediaUploadException("File to large.Max size is 10MB.");
        }
    }
}
