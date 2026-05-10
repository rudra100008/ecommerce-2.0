package com.user.user_service.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long addressId;

    @Column(nullable = false)
    private String district;
    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String municipality;

    @Column(nullable = false)
    private Integer wardNumber;
    @Column(length = 500)
    private String landmark;// famous place eg "Near Bhat Bhateni Supermarket", "Opposite of Everest Bank", "Behind Boudha Stupa"


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
}
