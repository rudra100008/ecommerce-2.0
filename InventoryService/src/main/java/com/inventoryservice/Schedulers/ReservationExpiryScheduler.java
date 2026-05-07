package com.inventoryservice.Schedulers;

import com.inventoryservice.Repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiryScheduler {
    private final ReservationRepository reservationRepository;

    @Scheduled(fixedRate = 5 * 60 * 1000) //300000
    @Transactional
    public void expireReservations(){
        log.info("Running reservation expiry check.....");

        int count = reservationRepository.expiryReservation(LocalDateTime.now());
        log.info("Expired {} reservations",count);
    }
}

