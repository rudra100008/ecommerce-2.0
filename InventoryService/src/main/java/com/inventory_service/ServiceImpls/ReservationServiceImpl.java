package com.inventory_service.ServiceImpls;

import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.DTOs.ReservationDTO.ReservationResponse;
import com.inventory_service.Entities.Inventory;
import com.inventory_service.Entities.Reservation;
import com.inventory_service.Enums.ReservationStatus;
import com.inventory_service.Exceptions.ResourceNotFoundException;
import com.inventory_service.Helpers.ReservationHelper;
import com.inventory_service.Mapper.ReservationMapper;
import com.inventory_service.Repository.ReservationRepository;
import com.inventory_service.Services.InventoryService;
import com.inventory_service.Services.ReservationService;
import com.inventory_service.Validators.ReservationValidators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;
    private final ReservationValidators reservationValidators;
    private final ReservationHelper reservationHelper;
    private final ReservationMapper reservationMapper;


    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        reservationValidators.validateNoDuplicateReservation(request.userId(),request.inventoryId());

        Inventory inventory = this.inventoryService.findByIdWithLock(request.inventoryId());

        reservationValidators.validateStockAvailability(inventory.getId(),request.reservedQuantity());

        Reservation reservation = reservationHelper.buildReservation(request,inventory);
        Reservation saved = this.reservationRepository.save(reservation);

        return this.reservationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservationQuantity(ReservationRequest request) {
        Reservation reservation = reservationRepository.findByUserIdAndInventoryIdAndStatus(
                request.userId(),
                request.inventoryId(),
                ReservationStatus.ACTIVE
        ).orElseThrow(()-> new ResourceNotFoundException("Reservation not found"));
        Inventory inventory = this.inventoryService.findByIdWithLock(request.inventoryId());
        reservationValidators.validateStockAvailability(inventory.getId(),request.reservedQuantity());

        reservation.setReservedQuantity(request.reservedQuantity());
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusWeeks(1));
        reservation.setStatus(ReservationStatus.ACTIVE);

        Reservation updated = this.reservationRepository.save(reservation);
        return this.reservationMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReservation(Long userId, Long inventoryId) {
        Reservation reservation = this.reservationRepository.findByUserIdAndInventoryIdAndStatus(
                userId,
                inventoryId,
                ReservationStatus.ACTIVE
        ).orElseThrow(()-> new ResourceNotFoundException("Reservation not found."));

        this.reservationRepository.delete(reservation);

    }


    @Override
    @Transactional
    public long getTotalReservationByInventoryId(Long inventoryId) {
        Long result = this.reservationRepository.getTotalReservedQuantityByInventoryId(inventoryId,LocalDateTime.now());
        return result != null ? result : 0L;
    }
}
