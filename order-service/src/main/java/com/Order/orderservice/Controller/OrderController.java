package com.Order.orderservice.Controller;

import com.Order.orderservice.DTOs.Error.ValidationErrors;
import com.Order.orderservice.DTOs.Order.OrderRequest;
import com.Order.orderservice.DTOs.Order.OrderResponse;
import com.Order.orderservice.DTOs.Error.ValidationErrorResponse;
import com.Order.orderservice.Services.OrderService;
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrors(errorResponses));
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

    @PutMapping("/confirm/{id}/user/{userId}")
    public ResponseEntity<?> confirm(
            @PathVariable Long id,
            @PathVariable Long userId
    ){
        OrderResponse orderResponse = this.orderService.confirmOrder(id,userId);

        return  ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }


    @PutMapping("/ship_order/{id}")
    public ResponseEntity<?> shipOrder(
            @PathVariable Long id
    ){
        OrderResponse orderResponse = this.orderService.shipOrder(id);
        return  ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }

    @PutMapping("/delivered/{id}")
    public ResponseEntity<?> delivered(
            @PathVariable Long id
    ){
        OrderResponse orderResponse = this.orderService.deliverOrder(id);
        return  ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }
}
