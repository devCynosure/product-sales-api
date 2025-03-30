package com.sparksupport.productsales.repository;

import com.sparksupport.productsales.dto.SalesDTO;
import com.sparksupport.productsales.model.Sales;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, String> {

    @Query(value = "SELECT SUM(s.quantity * p.price) FROM sales s JOIN product p ON s.product_id = p.id ", nativeQuery = true)
    String getTotalRevenue();

    @Query(value = "SELECT SUM(s.quantity * p.price) FROM sales s JOIN product p ON s.product_id = p.id WHERE p.id = ?1 ", nativeQuery = true)
    String getRevenueByProductId(String id);

    @Query(nativeQuery = true)
    List<SalesDTO> getAllSales();

    @Query(nativeQuery = true)
    SalesDTO getSalesById(String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sales SET quantity = ?1, sales_date = ?2 WHERE id = ?3 ", nativeQuery = true)
    void updateSalesById(Integer quantity, String saleDate, String id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO sales (id, quantity, sales_date, product_id) VALUES (?1, ?2, ?3, ?4) ", nativeQuery = true)
    void insertSales(String id, Integer quantity, String saleDate, String productId);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM sales WHERE id = ?1 ",nativeQuery = true)
    void deleteSalesById(String id);

    @Query(value = "SELECT id FROM sales WHERE product_id = ?1 ",nativeQuery = true)
    List<String> getSalesByProductId(String productId);
}
