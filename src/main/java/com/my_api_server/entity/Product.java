package com.my_api_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@Getter
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productNumber;

    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    private Long price;

    private Long stock;

    @Version
    private Long version;

    public void changeProductName(String changeProductName) {
        this.productName = changeProductName;
    }

    public void increaseStock(Long addStock) {
        this.stock += addStock;
    }

    public void decreaseStock(Long subStock) {
        this.stock -= subStock;
    }

    public void buyProductWithStock(Long orderCount) {
        if (this.getStock() - orderCount < 0) throw new RuntimeException("재고가 음수이니 주문 할 수 없습니다.");
        this.decreaseStock(orderCount);
    }

}
