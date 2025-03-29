package com.sparksupport.productsales.model;

import com.sparksupport.productsales.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@SqlResultSetMapping(
        name = "productsMapping",
        classes = @ConstructorResult(
                targetClass = ProductDTO.class,
                columns = {
                        @ColumnResult(name = "id", type = String.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "description", type = String.class),
                        @ColumnResult(name = "price", type = Double.class),
                        @ColumnResult(name = "quantity", type = Integer.class)
                }
        )
)

@NamedNativeQuery(
        name = "Product.getAllProducts",
        query = "SELECT id, name, description, price, quantity FROM product",
        resultSetMapping = "productsMapping"
)
@NamedNativeQuery(
        name = "Product.getAllProductsPaginated",
        query = "SELECT id, name, description, price, quantity FROM product LIMIT ?2 OFFSET ?1",
        resultSetMapping = "productsMapping"
)
@NamedNativeQuery(
        name = "Product.getProductById",
        query = "SELECT id, name, description, price, quantity FROM product WHERE id = ?1",
        resultSetMapping = "productsMapping"
)





@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Double price;

    @Column
    private Integer quantity;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private List<Sales> sales;
}
