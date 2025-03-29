package com.sparksupport.productsales.service;

import com.fasterxml.uuid.Generators;
import com.sparksupport.productsales.dto.ProductDTO;
import com.sparksupport.productsales.repository.ProductRepository;
import org.hibernate.generator.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;

    @Autowired
    @Lazy
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    public List<ProductDTO> getAllProducts() {
        return productRepository.getAllProducts();
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
}
