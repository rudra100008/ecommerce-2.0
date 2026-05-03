package com.Order.order_service.Entities;

import com.Order.order_service.Enum.AddressType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ShippingAddress {
    private String  province;
    private String district;
    private String municipality;
    private Integer wardNumber;
    private String landmark;
    private String area;
    private String houseNumber;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;
}
