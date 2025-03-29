package com.sparksupport.productsales.service;

import com.fasterxml.uuid.Generators;
import com.sparksupport.productsales.dto.ProductDTO;
import com.sparksupport.productsales.dto.ResponseDTO;
import com.sparksupport.productsales.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Value("${custom.error-code}")
    private Integer errorCode;

    private ProductRepository productRepository;

    private SalesService salesService;

    @Autowired
    @Lazy
    public ProductService(ProductRepository productRepository, SalesService salesService) {
        this.productRepository = productRepository;
        this.salesService = salesService;
    }


    public ResponseDTO getAllProducts(Integer pageNo, Integer pageSize) {
        try {
            int offset= pageSize * (pageNo - 1);
            return new ResponseDTO(productRepository.getAllProducts(offset,pageSize),200,true,null);
        } catch (Exception e) {
            return new ResponseDTO(null,errorCode,false,e.getMessage());
        }
    }

    public ProductDTO getProductById(String id) {
        return productRepository.getProductById(id);
    }

    public ResponseEntity<String> insertProducts(List<ProductDTO> productDTOList) {
        for (ProductDTO productDTO : productDTOList) {
            productDTO.setId(Generators.timeBasedGenerator().generate().toString());
            productRepository.insertProducts(productDTO.getId(), productDTO.getName(), productDTO.getDescription(), productDTO.getPrice(), productDTO.getQuantity());
        }
        return ResponseEntity.ok("Products inserted successfully!");
    }

    public ResponseEntity<String> updateProduct(String id, ProductDTO productDTO) {
        productRepository.updateProduct(productDTO.getName(), productDTO.getDescription(), productDTO.getPrice(), productDTO.getQuantity(), id);
        return ResponseEntity.ok("Product updated successfully!");
    }

    public ResponseEntity<String> deleteProduct(String id) {
        productRepository.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }

    public String getTotalRevenue() {
        return salesService.getTotalRevenue();

    }

    public String getRevenueByProductId(String id) {
        return salesService.getRevenueByProductId(id);
    }
}
