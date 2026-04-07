package com.my_api_server.repo;

import com.my_api_server.entity.Order;
import com.my_api_server.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepo extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findAllByOrder(Order order);
}
