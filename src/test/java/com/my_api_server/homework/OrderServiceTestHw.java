package com.my_api_server.homework;

import com.my_api_server.common.MemberFixture;
import com.my_api_server.common.ProductFixture;
import com.my_api_server.config.TestContainerConfig;
import com.my_api_server.entity.Member;
import com.my_api_server.entity.Product;
import com.my_api_server.homework.common.OrderCreateFixture;
import com.my_api_server.repo.MemberDBRepo;
import com.my_api_server.repo.OrderProductRepo;
import com.my_api_server.repo.OrderRepo;
import com.my_api_server.repo.ProductRepo;
import com.my_api_server.service.dto.OrderCreateDto;
import com.my_api_server.service.homework.OrderServiceHomework;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest //spring DI를 통해 모든 BEAN 주입 해주는 어노테이션
@Import(TestContainerConfig.class)
@ActiveProfiles("test") //application-{}.yml 값을 읽는다
public class OrderServiceTestHw {
    @Autowired
    private OrderServiceHomework orderService;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private MemberDBRepo memberDBRepo;
    @Autowired
    private OrderProductRepo orderProductRepo;
    @MockitoBean
    private Clock clock;

    @BeforeEach
    public void setUp() {
        Instant fixedInstant = Instant.parse("2026-04-06T00:00:00Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        orderProductRepo.deleteAllInBatch();
        productRepo.deleteAllInBatch();
        orderRepo.deleteAllInBatch();
        memberDBRepo.deleteAllInBatch();

    }

    private Member getSavedMember(String password) {
        return memberDBRepo.save(MemberFixture.defaultMember()
                .password(password)
                .build());
    }

    private List<Product> getProducts() {
        return productRepo.saveAll(ProductFixture.defaultProducts());
    }


    @Nested()
    @DisplayName("주문 생성 TC")
    class OrderCreateTest {
        //주문 생성시 상품 개수 조회
        @Test
        @DisplayName("주문 생성시 상품 개수 조회")
        public void checkOrderPhantomReadAndProductCnt() throws Exception {
            AtomicInteger successCnt = new AtomicInteger();
            int threadQty = 50;
            Long productQty = 1L;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(threadQty);
            final Member savedMember = getSavedMember("1234");
            final List<Product> checkProducts = getProducts();
            Map<Long, Long> expectedStocks = checkProducts.stream().collect(Collectors.toMap(
                    Product::getId, product -> product.getStock() - productQty * threadQty
            ));
            final OrderCreateDto orderCreateDto = OrderCreateFixture.builder()
                    .id(savedMember.getId())
                    .productIds(expectedStocks.keySet().stream().toList())
                    .counts(
                            IntStream.range(0, checkProducts.size())
                                    .mapToObj(i -> productQty)
                                    .collect(Collectors.toList())
                    ).build();
            ExecutorService executorService = Executors.newFixedThreadPool(threadQty);
            IntStream.range(0, threadQty).forEach(i ->
                    executorService.submit(() -> {
                        try {
                            startLatch.await();
                            orderService.createOrderOptLockHw(orderCreateDto);
                            successCnt.incrementAndGet();
                        } catch (Exception _) {
                        } finally {
                            endLatch.countDown();
                        }
                    })
            );
            startLatch.countDown();
            endLatch.await();
            executorService.shutdown();
            assertThat(successCnt.get()).isEqualTo(threadQty);
            orderRepo.findAllByBuyer(savedMember).forEach(order ->
                    orderProductRepo.findAllByOrder(order).forEach(orderProduct -> {
                        Long productId = orderProduct.getProduct().getId();
                        Product latestProduct = productRepo.findById(productId).orElseThrow();
                        assertThat(latestProduct.getStock())
                                .isEqualTo(expectedStocks.get(productId));
                    })
            );
        }

        //존재하지 않는 상품
        @Test
        @DisplayName("존재하지 않는 상품 구매 오류")
        public void buyNullProduct() {
            final Member savedMember = getSavedMember("1234");
            final List<Long> counts = List.of(1L);
            // 존재하지 않는 productIds
            final List<Long> productIds = List.of(1L);
            OrderCreateDto orderCreateDto = OrderCreateFixture
                    .builder()
                    .id(savedMember.getId())
                    .productIds(productIds)
                    .counts(counts)
                    .build();
            assertThatThrownBy(() -> orderService.createOrderOptLockHw(orderCreateDto))
                    .isInstanceOf(RuntimeException.class);
        }

    }

}
