package com.my_api_server.service;


import com.my_api_server.entity.Product;
import com.my_api_server.repo.ProductRepo;
import com.my_api_server.service.dto.ProductCreateDto;
import com.my_api_server.service.dto.ProductResponseDto;
import com.my_api_server.service.dto.ProductUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductService {

    private final ProductRepo productRepo;

    public ProductResponseDto createProduct(ProductCreateDto dto) {
        Product product = Product.builder()
                .productName(dto.getProductName())
                .productType(dto.getProductType())
                .productNumber(dto.getProductNumber())
                .price(dto.getPrice())
                .stock(dto.getStock()).build();
        Product savedProduct = productRepo.save(product);

        ProductResponseDto resDto = ProductResponseDto.builder()
                .productName(savedProduct.getProductName())
                .stock(savedProduct.getStock())
                .price(savedProduct.getPrice())
                .build();
        return resDto;
    }

    public ProductResponseDto findProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow();
        ProductResponseDto resDto = ProductResponseDto.builder()
                .productName(product.getProductName())
                .stock(product.getStock())
                .price(product.getPrice())
                .build();
        return resDto;
    }

    @Transactional
    public ProductResponseDto updateProduct(ProductUpdateDto dto) {
        Product product = productRepo.findById(dto.productId()).orElseThrow();
        product.changeProductName(dto.changeProductName());
        product.increaseStock(dto.changeStock());

        ProductResponseDto resDto = ProductResponseDto.builder()
                .productName(product.getProductName())
                .stock(product.getStock())
                .price(product.getPrice())
                .build();
        return resDto;
    }


}
