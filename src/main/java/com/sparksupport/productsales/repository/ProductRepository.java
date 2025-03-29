package com.sparksupport.productsales.repository;

import com.sparksupport.productsales.dto.ProductDTO;
import com.sparksupport.productsales.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query(nativeQuery = true)
    List<ProductDTO> getAllProductsPaginated(Integer offset, Integer pageSize);

    @Query(nativeQuery = true)
    ProductDTO getProductById(String id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO product (id, name, description, price, quantity) VALUES (?1, ?2, ?3, ?4, ?5) ", nativeQuery = true)
    void insertProducts(String id, String name, String description, Double price, Integer quantity);


    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET name = ?1, description = ?2, price = ?3, quantity = ?4 WHERE id = ?5 ", nativeQuery = true)
    void updateProduct(String name, String description, Double price, Integer quantity, String id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product WHERE id = ?1 ", nativeQuery = true)
    void deleteProduct(String id);

    @Query(nativeQuery = true)
    List<ProductDTO> getAllProducts();
}
