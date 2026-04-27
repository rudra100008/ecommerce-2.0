package com.product.product_service.Constants;


public class ApiConstants {
    private ApiConstants(){

    }

    public static final String API_INVENTORY = "/api/inventory";
    public static final String API_INVENTORY_BY_PRODUCT = API_INVENTORY + "/product/{productId}";

}
