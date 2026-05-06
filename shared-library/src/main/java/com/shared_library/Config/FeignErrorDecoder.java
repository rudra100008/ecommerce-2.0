package com.shared_library.Config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared_library.Exceptions.AlreadyExistException;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.InsufficientStockException;
import com.shared_library.Exceptions.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;

// shared-library - FeignErrorDecoder.java
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = extractMessage(response);

        return switch (response.status()) {
            case 400 -> new BusinessInvalidException(message);
            case 404 -> new ResourceNotFoundException(message);
            case 409 -> new AlreadyExistException(message);
            case 422 -> new InsufficientStockException(message);
            default  -> defaultDecoder.decode(methodKey, response);
        };
    }

    private String extractMessage(Response response) {
        try {
            if (response.body() != null) {
                String body = new String(
                        response.body().asInputStream().readAllBytes());
                JsonNode node = objectMapper.readTree(body);
                if (node.has("message")) {
                    return node.get("message").asText();
                }
                return body;
            }
        } catch (IOException e) {
            return "Failed to read error response";
        }
        return "Error from: " + response.request().url();
    }
}
