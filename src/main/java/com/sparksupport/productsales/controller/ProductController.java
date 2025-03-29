package com.sparksupport.productsales.controller;

import com.sparksupport.productsales.dto.ProductDTO;
import com.sparksupport.productsales.model.Product;
import com.sparksupport.productsales.service.ProductService;
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
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
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
    public ResponseEntity<String>updateProduct(@PathVariable String id, @RequestBody ProductDTO productDTO){
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>deleteProduct(@PathVariable String id){
        return productService.deleteProduct(id);
    }
}
