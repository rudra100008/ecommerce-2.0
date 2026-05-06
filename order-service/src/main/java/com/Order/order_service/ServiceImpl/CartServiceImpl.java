package com.Order.order_service.ServiceImpl;


import com.Order.order_service.DTOs.Cart.CartResponse;
import com.Order.order_service.DTOs.CartItem.CartItemRequest;
import com.Order.order_service.DTOs.CartItem.CartItemResponse;
import com.Order.order_service.DTOs.Product.ProductResponse;
import com.Order.order_service.DTOs.ReservationDTO.ReservationRequest;
import com.Order.order_service.DTOs.ReservationDTO.ReservationResponse;
import com.Order.order_service.Entities.Cart;
import com.Order.order_service.Entities.CartItem;

import com.Order.order_service.Mapper.CartItemMapper;
import com.Order.order_service.Mapper.CartMapper;
import com.Order.order_service.Repository.CartItemRepository;
import com.Order.order_service.Repository.CartRepository;
import com.Order.order_service.Services.CartService;
import com.Order.order_service.client.InventoryClient;
import com.Order.order_service.client.ProductClient;
import com.shared_library.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;


    @Override
    @Transactional
    public CartResponse createCart(Long userId) {
        if(cartRepository.existsByUserId(userId)){
            log.info("Cart already exists for userId: {}",userId);
            return getCartByUserId(userId);
        }

        Cart cart = Cart.builder()
                .userId(userId)
                .cartItems(new ArrayList<>())
                .build();

        Cart saved = this.cartRepository.save(cart);
        return this.cartMapper.toResponse(saved,List.of());
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, CartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for userId: " + userId));

        ProductResponse product = productClient.getById(request.productId());

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst();



        if (existingItem.isPresent()) {

            // if product is already in cart update the quantity not add same product in cart
            CartItem item = existingItem.get();

            ReservationResponse reservationResponse = inventoryClient.updateReservationQuantity(new ReservationRequest(
                    product.id(), userId, (long) request.quantity()));

            item.setQuantity(reservationResponse.reservedQuantity().intValue());
            cartItemRepository.save(item);
            log.info("Updated quantity for productId: {} userId: {}",
                    request.productId(), userId);

        } else {
            //  new product — so add to cart and create a reservation
            ReservationResponse reservation = inventoryClient.createReservation(
                    new ReservationRequest(
                            product.id(), userId, request.quantity().longValue()));

            CartItem newItem = CartItem.builder()
                    .productId(product.id())
                    .quantity(request.quantity())
                    .priceAtAddTime(product.price())
                    .discountAtAddTime(product.discount())
                    .reservationId(reservation.reservationId())
                    .build();

            cart.addCartItem(newItem);
            log.info("Added productId: {} to cart of userId: {}",
                    request.productId(), userId);
        }

        Cart saved = cartRepository.save(cart);
        List<CartItemResponse> cartItemResponses =
                cartItemMapper.toCartItemResponseList(saved.getCartItems());
        return cartMapper.toResponse(saved, cartItemResponses);
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for userId: " + userId));

        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CartItem not found: " + cartItemId));


        try {
            inventoryClient.deleteReservation(userId, item.getProductId());
        } catch (Exception e) {
            log.warn("Failed to delete reservation for productId: {}. " +
                    "Will expire automatically.", item.getProductId());
        }

        cart.removeCartItem(item);
        Cart saved = cartRepository.save(cart);

        List<CartItemResponse> cartItemResponses =
                cartItemMapper.toCartItemResponseList(saved.getCartItems());
        return cartMapper.toResponse(saved, cartItemResponses);
    }

    @Override
    @Transactional
    public void clearCartByUserId(Long userId) {
        Cart cart = this.cartRepository.findByUserId(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Cart not found"));
        cart.getCartItems().forEach(item -> {
            try {
                inventoryClient.deleteReservation(userId, item.getProductId());
            } catch (Exception e) {
                log.warn("Failed to release reservation for productId: {}",
                        item.getProductId());
            }
        });
        cart.getCartItems().clear();
        this.cartRepository.save(cart);

    }

    @Override
    @Transactional
    public void delete(Long userId) {
        Cart cart = this.cartRepository.findByUserId(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Cart not found"));
        this.cartRepository.delete(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = this.cartRepository.findByUserId(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Cart not found"));

        List<CartItemResponse> cartItemResponseList = this.cartItemMapper.toCartItemResponseList(cart.getCartItems());
        return this.cartMapper.toResponse(cart,cartItemResponseList);
    }
}
