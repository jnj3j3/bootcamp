package com.my_api_server.common;

import com.my_api_server.entity.Product;
import com.my_api_server.entity.ProductType;

import java.util.List;

public class ProductFixture {
    //product type is fixed
    public static Product.ProductBuilder defaultProduct() {
        return Product.builder()
                .productType(ProductType.CLOTHES);
    }

    public static List<Product> defaultProducts() {
        Product product1 = Product.builder()
                .productNumber("TEST1")
                .productName("티셔츠 1")
                .productType(ProductType.CLOTHES)
                .price(1000L)
                .stock(10000000000L)
                .build();

        Product product2 = Product.builder()
                .productNumber("TEST2")
                .productName("티셔츠 2")
                .productType(ProductType.CLOTHES)
                .price(2000L)
                .stock(200000000000L)
                .build();
        return List.of(product1, product2);
    }
}
