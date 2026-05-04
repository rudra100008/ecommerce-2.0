package com.Order.order_service.Controller;

import com.Order.order_service.DTOs.Error.ValidationErrors;
import com.Order.order_service.DTOs.Order.OrderRequest;
import com.Order.order_service.DTOs.Order.OrderResponse;
import com.Order.order_service.DTOs.Error.ValidationErrorResponse;
import com.Order.order_service.Services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    @PostMapping("/create")
    public ResponseEntity<?> create(
            @Valid @RequestBody OrderRequest orderRequest,
            BindingResult result
    ){
        if(result.hasErrors()){
            List<ValidationErrorResponse> errorResponses = new ArrayList<>();
            result.getFieldErrors().forEach(err ->{
                errorResponses.add(new ValidationErrorResponse(err.getField(),err.getDefaultMessage()));
            });
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationErrors(errorResponses));
        }
        OrderResponse orderResponse = this.orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @GetMapping("/fetch/{id}/user/{userId}")
    public ResponseEntity<?> fetchByIdAndUserId(
            @PathVariable Long id,
            @PathVariable Long userId
    ){
        OrderResponse response = this.orderService.getOrderById(id,userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/cancel/{id}/user/{userId}")
    public ResponseEntity<?> cancel(
            @PathVariable Long id,
            @PathVariable Long userId
    ){
        this.orderService.cancelOrder(id,userId);
        return ResponseEntity.status(HttpStatus.OK).body("Order cancelled successfully.");
    }
}
