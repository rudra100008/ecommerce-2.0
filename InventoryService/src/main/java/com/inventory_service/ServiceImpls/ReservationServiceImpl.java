package com.inventory_service.ServiceImpls;

import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.DTOs.ReservationDTO.ReservationResponse;
import com.inventory_service.Entities.Inventory;
import com.inventory_service.Entities.Reservation;
import com.inventory_service.Enums.ReservationStatus;
import com.inventory_service.Helpers.ReservationHelper;
import com.inventory_service.Mapper.ReservationMapper;
import com.inventory_service.Repository.InventoryRepository;
import com.inventory_service.Repository.ReservationRepository;
import com.inventory_service.Services.ReservationService;
import com.inventory_service.Validators.ReservationValidators;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.InsufficientStockException;
import com.shared_library.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;
    private final ReservationValidators reservationValidators;
    private final ReservationHelper reservationHelper;
    private final ReservationMapper reservationMapper;


    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request,Long userId) {

        Inventory inventory = this.inventoryRepository.findByProductIdWithLock(request.productId())
                .orElseThrow(()-> new ResourceNotFoundException("Inventory not found"));

        long totalReserved = reservationRepository
                .getTotalReservedQuantityByProductId(request.productId(), LocalDateTime.now());

        long available = inventory.getStockQuantity() - totalReserved;

        if (available < request.reservedQuantity()) {
            throw new InsufficientStockException(String.format(
                    "Insufficient stock. Requested: %d,Available: %d",
                    request.reservedQuantity(),available
            ));
        }

        boolean exists = reservationRepository
                .existsByUserIdAndProductIdAndStatus(
                        userId, request.productId(), ReservationStatus.ACTIVE);
        if (exists) {
            throw new BusinessInvalidException(String.format("Active reservation for product %d already exists.",request.productId()));
        }

        Reservation reservation = reservationHelper.buildReservation(request,inventory,userId);
        Reservation saved = this.reservationRepository.save(reservation);

        return this.reservationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservationQuantity(ReservationRequest request,Long userId) {
        Reservation reservation = reservationRepository.findByUserIdAndProductIdAndStatus(
                userId,
                request.productId(),
                ReservationStatus.ACTIVE
        ).orElseThrow(()-> new ResourceNotFoundException("Reservation not found"));

        this.inventoryRepository.findByProductIdWithLock(request.productId())
                .orElseThrow(()-> new ResourceNotFoundException("Inventory not found"));

        reservationValidators.validateStockAvailability(request.productId(),request.reservedQuantity());



        reservation.setReservedQuantity(request.reservedQuantity());
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusWeeks(1));
        reservation.setStatus(ReservationStatus.ACTIVE);

        Reservation updated = this.reservationRepository.save(reservation);
        return this.reservationMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReservation(Long userId, Long productId) {
        Reservation reservation = this.reservationRepository.findByUserIdAndProductIdAndStatus(
                userId,
                productId,
                ReservationStatus.ACTIVE
        ).orElseThrow(()-> new ResourceNotFoundException("Reservation not found.It might have expired"));

        this.reservationRepository.delete(reservation);

    }

    @Override
    @Transactional
    public void releaseAllReservation(Long userId, List<Long> productIds) {
        List<Reservation> reservations = this.reservationRepository.findActiveByUserIdAndProductIds(
                userId,
                productIds,
                LocalDateTime.now()
        );

        this.reservationRepository.deleteAll(reservations);
    }


    @Override
    @Transactional(readOnly = true)
    public Long getTotalReservationByProductId(Long productId) {
        Long result = this.reservationRepository.getTotalReservedQuantityByProductId(productId,LocalDateTime.now());
        return result != null ? result : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> validateReservation(
            Long userId,
            List<Long> productIds
    ) {
        List<Reservation> reservations = this.reservationRepository
                .findActiveByUserIdAndProductIds(userId, productIds, LocalDateTime.now());

        // Check all products have a reservation
        if(reservations.size() != productIds.size()) {
            // find which productIds are missing
            List<Long> foundProductIds = reservations.stream()
                    .map(r -> r.getInventory().getProductId())
                    .toList();

            List<Long> missingProductIds = productIds.stream()
                    .filter(id -> !foundProductIds.contains(id))
                    .toList();

            throw new BusinessInvalidException(
                    "Reservations expired or not found for products: " + missingProductIds
                            + ". Please add to cart again."
            );
        }

        return this.reservationMapper.toReservationList(reservations);
    }

    @Override
    @Transactional
    public void convertReservation(Long userId, List<Long> productIds) {
        if(productIds == null || productIds.isEmpty()){
            throw new BusinessInvalidException("Product Ids cannot be empty");
        }
        List<Inventory> inventories = inventoryRepository
                .findByProductIdsWithLock(productIds);

        List<Reservation> reservations = this.reservationRepository.findActiveByUserIdAndProductIds(
                userId,
                productIds,
                LocalDateTime.now()
        );

        if (reservations.size() != productIds.size()) {
            List<Long> foundIds = reservations.stream()
                    .map(r -> r.getInventory().getProductId())
                    .toList();
            List<Long> missingIds = productIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new BusinessInvalidException(
                    "Reservations expired for products: " + missingIds
            );
        }

        boolean alreadyConverting = reservations.stream()
                .anyMatch(r -> r.getStatus() == ReservationStatus.CONVERTING);
        if(alreadyConverting){
            log.warn("Conversion already in progress for userId:{}, products:{}",userId,productIds);
            return;
        }
        Map<Long,Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(
                        i -> i.getProductId(),i ->i
                ));

        reservations.forEach(r -> r.setStatus(ReservationStatus.CONVERTING));
        reservationRepository.saveAll(reservations);
        reservations.forEach(reservation -> {
            Long productId = reservation.getInventory().getProductId();
            Inventory inventory = inventoryMap.get(productId);

            if(inventory == null){
                throw new ResourceNotFoundException("Inventory not found for product: " + productId);
            }
            Long stockQuantity = inventory.getStockQuantity();
            Long reservedQuantity = reservation.getReservedQuantity();


            if (stockQuantity < reservedQuantity) {
                throw new BusinessInvalidException(
                        String.format("Stock inconsistent for product: %d. Stock: %d, Reserved: %d",
                                productId, stockQuantity, reservedQuantity));
            }

            inventory.setStockQuantity(stockQuantity - reservedQuantity);
        });

        inventoryRepository.saveAll(inventories);
        this.reservationRepository.deleteAll(reservations);

        log.info("Converted {} reservations for userId: {}", reservations.size(), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse fetchActiveByUserIdAndProductId(Long userId, Long productId) {
        Reservation reservation = this.reservationRepository.findByUserIdAndProductIdAndStatus(
                userId,
                productId,
                ReservationStatus.ACTIVE
        ).orElseThrow(()-> new ResourceNotFoundException("Reservation not found.It might have expired."));

        return this.reservationMapper.toResponse(reservation);
    }
}
