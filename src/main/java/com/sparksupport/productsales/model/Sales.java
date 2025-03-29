package com.sparksupport.productsales.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sales {
    @Id
    private String id;

    @Column
    private Integer quantity;

    @Column
    private String saleDate;

    @ManyToOne
    private Product product;
}
