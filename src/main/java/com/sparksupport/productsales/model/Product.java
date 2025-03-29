package com.sparksupport.productsales.model;

import com.sparksupport.productsales.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
}
