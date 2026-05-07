package com.inventoryservice.Constants;


public class ApiConstants {
    private ApiConstants(){

    }

    public static final String API_INVENTORY = "/api/inventory";
    public static final String API_INVENTORY_BY_PRODUCT = API_INVENTORY + "/product/{productId}";


    //product-service urls;
    public static final String API_PRODUCT = "/api/product";
    public static final String API_PRODUCT_BY_PRODUCT_ID = API_PRODUCT + "/{productId}";

}
