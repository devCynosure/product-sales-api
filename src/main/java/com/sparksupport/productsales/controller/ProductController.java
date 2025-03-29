package com.sparksupport.productsales.controller;

import com.sparksupport.productsales.dto.ProductDTO;
import com.sparksupport.productsales.dto.ResponseDTO;
import com.sparksupport.productsales.service.ProductService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
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
    public ResponseDTO getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping()
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO insertProducts(@RequestBody List<ProductDTO> productDTOList) {
        return productService.insertProducts(productDTOList);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO updateProduct(@PathVariable String id, @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO deleteProduct(@PathVariable String id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/gettotalrevenue")
    public ResponseDTO getTotalRevenue() {
        return productService.getTotalRevenue();
    }

    @GetMapping("/getrevenuebyid/{id}")
    public ResponseDTO getRevenueByProductId(@PathVariable String id) {
        return productService.getRevenueByProductId(id);
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf() {
        return productService.downloadPdf();
    }

}
