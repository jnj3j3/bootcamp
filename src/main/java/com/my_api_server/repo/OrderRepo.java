package com.my_api_server.repo;

import com.my_api_server.entity.Member;
import com.my_api_server.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findAllByBuyer(Member buyer);
}
