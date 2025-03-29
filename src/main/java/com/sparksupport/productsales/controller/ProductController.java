package com.sparksupport.productsales.controller;

import com.sparksupport.productsales.dto.ProductDTO;
import com.sparksupport.productsales.dto.ResponseDTO;
import com.sparksupport.productsales.service.ProductService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private ProductService productService;

    @Autowired
    @Lazy
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public ResponseDTO getAllProducts(@RequestParam(defaultValue = "1") Integer pageNo,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        return productService.getAllProducts(pageNo, pageSize);
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping()
    public ResponseEntity<String> insertProducts(@RequestBody List<ProductDTO> productDTOList) {
        return productService.insertProducts(productDTOList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable String id, @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/gettotalrevenue")
    public String getTotalRevenue() {
        return productService.getTotalRevenue();
    }

    @GetMapping("/getrevenuebyid/{id}")
    public String getRevenueByProductId(@PathVariable String id) {
        return productService.getRevenueByProductId(id);
    }
}
