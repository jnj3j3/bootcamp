package com.my_api_server.service;

import com.my_api_server.entity.*;
import com.my_api_server.repo.MemberDBRepo;
import com.my_api_server.repo.OrderRepo;
import com.my_api_server.repo.ProductRepo;
import com.my_api_server.service.dto.OrderCreateDto;
import com.my_api_server.service.dto.OrderResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) //mockitoextension 활성화
class OrderServiceUnitTest {
    @Mock // 가짜 객체 생성
    ProductRepo productRepo;
    @Mock // 가짜 객체 생성
    MemberDBRepo memberDBRepo;
    @Mock // 가짜 객체 생성
    OrderRepo orderRepo;


    @InjectMocks //실제 테스트할 대상 클래스(Mock 개체를 자동으로 주입받는다)
    OrderService orderService;

    // 초기 데이터용 클래스 객체
    InitData initData;
    OrderCreateDto orderCreateDto;

    @BeforeEach
    public void init() {

        initData = new InitData();
        initData.memberId = 1L;
        initData.productIds = List.of(1L, 2L);
        initData.counts = List.of(1L, 2L);
        initData.product1 = Product.builder()
                .productNumber("TEST1")
                .price(1000L)
                .stock(3L)
                .productType(ProductType.CLOTHES)
                .productName("티셔츠 1")
                .build();
        initData.product2 = Product.builder()
                .productNumber("TEST2")
                .price(2000L)
                .stock(6L)
                .productType(ProductType.CLOTHES)
                .productName("티셔츠 2")
                .build();
        initData.member = Member.builder()
                .email("test1@gmail.com")
                .password("1234")
                .build();
        orderCreateDto = new OrderCreateDto(initData.memberId, initData.productIds, initData.counts);
    }

    @Test
    @DisplayName("test1")
    public void test1() {
        //given(when절에 필요한 데이터 생성)
        int a = 10;

        //when(실제 수행할 메서드)
        a++;

        //then(테스트 결과를 확인)
        assertThat(a).isEqualTo(11);

    }

    @Test
    @DisplayName("[Happy]주문 요청이 정상적으로 잘 등록된다")
    public void createOrderSuccess() {
        //given

        when(productRepo.findAllById(initData.productIds)).thenReturn(List.of(initData.product1, initData.product2));
        when(memberDBRepo.findById(initData.memberId)).thenReturn(Optional.of(initData.member));
        when(orderRepo.save(any())).thenAnswer(invocationOnMock ->
                invocationOnMock.getArgument(0));
        //when
        OrderResponseDto dto = orderService.createOrder(orderCreateDto);

        //then
        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepo).save(orderArgumentCaptor.capture()); //orderRepo save()가 호출되는지 확인

        assertThat(dto.isSuccess()).isTrue(); //성공 여부 검증
        assertThat(dto.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);

    }

    @Test
    @DisplayName("[Exception]주문 요청시 재고 부족하면 예외 처리가 정상 동작한다")
    public void ProductStockValid() {
        //given
        Long memberId = 1L;
        List<Long> productIds = List.of(1L, 2L);
        List<Long> counts = List.of(2L, 4L);

        Product product1 = Product.builder()
                .productNumber("TEST1")
                .productName("티셔츠 1")
                .productType(ProductType.CLOTHES)
                .price(1000L)
                .stock(1L)
                .build();

        Product product2 = Product.builder()
                .productNumber("TEST2")
                .productName("티셔츠 2")
                .productType(ProductType.CLOTHES)
                .price(2000L)
                .stock(2L)
                .build();

        Member member = Member.builder()
                .email("test1@gmail.com")
                .password("1234")
                .build();

        OrderCreateDto createDto = new OrderCreateDto(memberId, productIds, counts);

        when(productRepo.findAllById(productIds)).thenReturn(List.of(product1, product2));
        when(memberDBRepo.findById(memberId)).thenReturn(Optional.of(member));

        //when


        //then
        assertThatThrownBy(() -> orderService.createOrder(createDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("재고가 음수이니 주문 할 수 없습니다.");
    }

    //    @Test
    @DisplayName("[Exception]주문 시간 날짜 오류 테스트")
    //given
    public void orderTimeException() {


        when(productRepo.findAllById(initData.productIds)).thenReturn(List.of(initData.product1, initData.product2));
        when(memberDBRepo.findById(initData.memberId)).thenReturn(Optional.of(initData.member));

        //when
        OrderResponseDto dto = orderService.createOrder(orderCreateDto);

        //then
        assertThat(dto).isNotNull();
    }

    //테스트용 초기 데이터
    public class InitData {
        public Long memberId;
        public List<Long> productIds;
        public List<Long> counts;

        public Product product1;
        public Product product2;

        public Member member;
    }


}