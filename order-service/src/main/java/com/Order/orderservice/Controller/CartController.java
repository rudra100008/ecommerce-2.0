package com.Order.orderservice.Controller;

import com.Order.orderservice.DTOs.Cart.CartResponse;
import com.Order.orderservice.DTOs.CartItem.CartItemRequest;
import com.Order.orderservice.DTOs.Error.ValidationErrorResponse;
import com.Order.orderservice.DTOs.Error.ValidationErrors;
import com.Order.orderservice.Services.CartService;
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

    @PostMapping("/addToCart/user/{userId}")
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrors(errorResponses));
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


    @DeleteMapping("/clear_cart/user/{userId}")
    public ResponseEntity<?> clearCart(
            @PathVariable Long userId
    ){
        this.cartService.clearCartByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body("Cart cleared");
    }

    @GetMapping("/fetch/user/{userId}")
    public ResponseEntity<?> fetchCart(
            @PathVariable Long userId
    ){
        CartResponse cartResponse = this.cartService.getCartByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(cartResponse);
    }
}
