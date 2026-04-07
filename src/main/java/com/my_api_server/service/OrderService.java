package com.my_api_server.service;

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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final MemberDBRepo memberRepo;
    private final ProductRepo productRepo;

// 원래 이렇게 만들고 싶었지만, thread 경재 때문에 포기
//    private Clock clock = Clock.systemDefaultZone();
//
//    public LocalDateTime getCurrentTime() {
//        return LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault());
//    }
//
//    public void changeClock(Clock newClock) {
//        clock = newClock;
//    }

    @Transactional
    public OrderResponseDto createOrder(OrderCreateDto dto) {
        Member memeber = memberRepo.findById(dto.memberId())
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        LocalDateTime orderTime = LocalDateTime.now();
//        if (orderTime.getHour() == 13) {
//            //로직 실행 (점심시간 이벤트 쿠폰 발행)
//            return null;
//        }
        Order order = Order.createOrder(memeber, orderTime);
        List<Product> products = productRepo.findAllById(dto.productId());
        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size()).mapToObj(idx -> {
            Product product = products.get(idx);
            Long orderCount = dto.count().get(idx);
            product.buyProductWithStock(orderCount);
            return order.createOrderProduct(orderCount, product);
        }).toList();

        order.addOrderProducts(orderProducts);
        Order savedOrder = orderRepo.save(order);
        return OrderResponseDto.of(
                savedOrder.getOrderTime(), OrderStatus.COMPLETED, true);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto findOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        OrderResponseDto orderResponseDto = OrderResponseDto.of(
                order.getOrderTime(), order.getOrderStatus(), true);
        return orderResponseDto;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(includes = ObjectOptimisticLockingFailureException.class, maxRetries = 3) //재시도 3번
    public OrderResponseDto createOrderOptLock(OrderCreateDto dto) {
        log.info("@Retryable 테스트");
        Member member = memberRepo.findById(dto.memberId()).orElseThrow();
        LocalDateTime orderTime = LocalDateTime.now();

        Order order = Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.Pending)
                .orderTime(orderTime)
                .build();

        List<Product> products = productRepo.findAllById(dto.productId()); //IN 쿼리
        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size())
                .mapToObj(idx -> {
                    //재고에대해서 차감을 해야한다.(음수 처리x)
                    Product product = products.get(idx);

                    //현재 재고에서 주문재고 감했을때 음수이면 <0 예외터트린다!(주문못하게 막는다!)
                    if (product.getStock() - dto.count().get(idx) < 0) {
                        throw new RuntimeException("재고가 음수이니 주문 할 수 없습니다!");
                    }

                    //재고 감소
                    //update product set stock = stock - 1 where pk =1;(더티체킹, 스냅샷 값을 비교한다!)
                    product.decreaseStock(dto.count().get(idx));

                    return OrderProduct.builder()
                            .order(order)
                            .number(dto.count().get(idx)) //product에 맞는 주문개수를 찾는다!
                            .product(products.get(idx))
                            .build();
                })
                .toList();

        order.addOrderProducts(orderProducts);

        Order savedOrder = orderRepo.save(order); //wt1- 영속성 컨텍스트가 1개 관리
        OrderResponseDto orderResponseDto = OrderResponseDto.of(
                savedOrder.getOrderTime(),
                OrderStatus.COMPLETED,
                true);

        return orderResponseDto;
    }

    @Transactional
    public OrderResponseDto createOrderPLock(OrderCreateDto dto) {
        Member memeber = memberRepo.findById(dto.memberId()).orElseThrow();
        LocalDateTime orderTime = LocalDateTime.now();
        Order order = Order.builder()
                .buyer(memeber)
                .orderStatus(OrderStatus.Pending)
                .orderTime(orderTime).build();
        List<Product> products = productRepo.findAllByIdsWithXLock(dto.productId());
        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size()).mapToObj(idx -> {
            Product product = products.get(idx);
            if (product.getStock() - dto.count().get(idx) < 0) throw new RuntimeException("재고가 음수이니 주문 할 수 없습니다.");
            product.decreaseStock(dto.count().get(idx));

            return OrderProduct.builder()
                    .order(order)
                    .number(dto.count().get(idx))
                    .product(products.get(idx)).build();
        }).toList();
        order.addOrderProducts(orderProducts);
        Order savedOrder = orderRepo.save(order);
        OrderResponseDto orderResponseDto = OrderResponseDto.of(
                savedOrder.getOrderTime(), OrderStatus.COMPLETED, true);
        return orderResponseDto;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean checkPhantomRead() {
        Long cnt1 = orderRepo.count();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Long cnt2 = orderRepo.count();
        return cnt1.equals(cnt2);
    }
    //최송 결정 = orderstatus completed
//    @Transactional
//    public String

}
