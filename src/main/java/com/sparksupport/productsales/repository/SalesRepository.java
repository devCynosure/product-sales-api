package com.sparksupport.productsales.repository;

import com.sparksupport.productsales.model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<Sales, String> {

    @Query(value = "SELECT SUM(s.quantity * p.price) FROM sales s JOIN product p ON s.product_id = p.id ",nativeQuery = true)
    String getTotalRevenue();

    @Query(value = "SELECT SUM(s.quantity * p.price) FROM sales s JOIN product p ON s.product_id = p.id WHERE p.id = ?1",nativeQuery = true)
    String getRevenueByProductId(String id);
}
