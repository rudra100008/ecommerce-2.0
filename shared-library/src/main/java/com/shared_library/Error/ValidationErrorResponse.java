package com.shared_library.Error;

public record ValidationErrorResponse(
        String field,
        String message
) {

}
