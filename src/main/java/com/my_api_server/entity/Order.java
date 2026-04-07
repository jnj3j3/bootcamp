package com.my_api_server.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Getter
@Builder
public class Order {
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderProduct> orderProducts = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members_id", nullable = false)
    private Member buyer;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Column(nullable = false)
    private LocalDateTime orderTime;

    public static Order createOrder(Member member, LocalDateTime orderTime) {
        Order order = Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.Pending)
                .orderTime(orderTime).build();
        return order;
    }

    public OrderProduct createOrderProduct(Long orderCount, Product product) {
        return OrderProduct.builder()
                .order(this)
                .number(orderCount)
                .product(product).build();
    }

    public void addOrderProducts(List<OrderProduct> orderProduct) {
        this.orderProducts = orderProduct;
    }
    
}

