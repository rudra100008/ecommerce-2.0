package com.shared_library.Error;

import java.util.List;

public record ValidationErrors(
        List<ValidationErrorResponse> errors
) {
}
