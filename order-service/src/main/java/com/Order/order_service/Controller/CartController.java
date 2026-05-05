package com.Order.order_service.Controller;

import com.Order.order_service.DTOs.Cart.CartResponse;
import com.Order.order_service.DTOs.CartItem.CartItemRequest;
import com.Order.order_service.DTOs.Error.ValidationErrorResponse;
import com.Order.order_service.DTOs.Error.ValidationErrors;
import com.Order.order_service.Services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;


    @PostMapping("/create/user/{userId}")
    public ResponseEntity<?> createCart(
            @PathVariable Long userId
    ){
        CartResponse cartResponse = this.cartService.createCart(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @PutMapping("/addToCart/user/{userId}")
    public ResponseEntity<?> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequest cartItemRequest,
            BindingResult result
    ){
        if(result.hasErrors()){
            List<ValidationErrorResponse> errorResponses = new ArrayList<>();
            result.getFieldErrors().forEach(err ->{
                errorResponses.add(new ValidationErrorResponse(err.getField(),err.getDefaultMessage()));
            });
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationErrors(errorResponses));
        }
        CartResponse cartResponse = this.cartService.addToCart(userId,cartItemRequest);
        return ResponseEntity.status(HttpStatus.OK).body(cartResponse);
    }

    @DeleteMapping("/removeFromCart/user/{userId}/cartItem/{cartItemId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long cartItemId
    ){
        CartResponse cartResponse = this.cartService.removeFromCart(userId,cartItemId);

        return ResponseEntity.status(HttpStatus.OK).body(cartResponse);
    }
}
