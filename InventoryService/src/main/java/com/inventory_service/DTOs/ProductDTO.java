package com.inventory_service.DTOs;


import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private Long id;

    private String name;
    private String description;
    private Double price;
    private Double discount;
}
