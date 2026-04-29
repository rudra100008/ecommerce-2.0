package com.media_service.DTO;

import jakarta.validation.constraints.NotNull;

public record MediaDeleteRequest (
        @NotNull(message = "publicId is required")
        String publicId
){
}
