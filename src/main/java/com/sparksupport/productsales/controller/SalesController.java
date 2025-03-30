package com.sparksupport.productsales.controller;

import com.sparksupport.productsales.dto.ResponseDTO;
import com.sparksupport.productsales.dto.SalesDTO;
import com.sparksupport.productsales.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private SalesService salesService;

    @Autowired
    @Lazy
    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping()
    public ResponseDTO getAllSales() {
        return salesService.getAllSales();
    }

    @GetMapping("/{id}")
    public ResponseDTO getSalesById(@PathVariable String id) {
        return salesService.getSalesById(id);
    }

    @PutMapping("/{id}")
    public ResponseDTO updateSalesById(@PathVariable String id, @RequestBody SalesDTO salesDTO) {
        return salesService.updateSalesById(id, salesDTO);
    }

    @PostMapping("/product/{productId}")
    public ResponseDTO insertSalesByProductId(@PathVariable String productId, @RequestBody List<SalesDTO> salesDTO) {
        return salesService.insertSalesByProductId(productId, salesDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseDTO deleteSalesById(@PathVariable String id) {
        return salesService.deleteSalesById(id);
    }

}
