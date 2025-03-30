package com.sparksupport.productsales.model;

import com.sparksupport.productsales.dto.SalesDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SqlResultSetMapping(
        name = "salesmapping",
        classes = @ConstructorResult(
                targetClass = SalesDTO.class,
                columns = {
                        @ColumnResult(name = "id",type = String.class),
                        @ColumnResult(name = "quantity",type = Integer.class),
                        @ColumnResult(name = "salesDate",type = String.class),
                        @ColumnResult(name = "productId",type = String.class),
                }
        )
)

@NamedNativeQuery(
        name = "Sales.getAllSales",
        query = "SELECT id, quantity, sales_date AS salesDate, product_id AS productId FROM sales ",
        resultSetMapping = "salesmapping"
)
@NamedNativeQuery(
        name = "Sales.getSalesById",
        query = "SELECT id, quantity, sales_date AS salesDate, product_id AS productId FROM sales WHERE id = ?1 ",
        resultSetMapping = "salesmapping"
)



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
    private String salesDate;

    @ManyToOne
    private Product product;
}
