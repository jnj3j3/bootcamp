package com.my_api_server.service.homework;

import com.my_api_server.entity.*;
import com.my_api_server.repo.MemberDBRepo;
import com.my_api_server.repo.OrderRepo;
import com.my_api_server.repo.ProductRepo;
import com.my_api_server.service.dto.OrderCreateDto;
import com.my_api_server.service.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceHomework {
    private final Clock clock;
    private final MemberDBRepo memberRepo;
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now(clock);
    }

    @Transactional
    @Retryable(includes = ObjectOptimisticLockingFailureException.class, maxRetries = 3)//재시도 3번
    public OrderResponseDto createOrderOptLockHw(OrderCreateDto dto) {
        Member member = memberRepo.findById(dto.memberId()).orElseThrow();
        LocalDateTime orderTime = getCurrentTime();
        Order order = Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.Pending)
                .orderTime(orderTime)
                .build();

        List<Product> products = productRepo.findAllByIdsWithXLock(dto.productId()); //IN 쿼리
        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size())
                .mapToObj(idx -> {
                    Product product = products.get(idx);
                    product.decreaseStock(dto.count().get(idx));

                    return OrderProduct.builder()
                            .order(order)
                            .number(dto.count().get(idx))
                            .product(products.get(idx))
                            .build();
                })
                .toList();

        order.addOrderProducts(orderProducts);
        Order savedOrder = orderRepo.save(order);
        return OrderResponseDto.of(
                savedOrder.getOrderTime(),
                OrderStatus.COMPLETED,
                true);
    }
}
