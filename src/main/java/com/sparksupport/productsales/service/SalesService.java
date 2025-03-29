package com.sparksupport.productsales.service;

import com.sparksupport.productsales.repository.SalesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SalesService {

    private SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository){
        this.salesRepository=salesRepository;
    }


    public String getTotalRevenue() {
        return salesRepository.getTotalRevenue();
    }

    public String getRevenueByProductId(String id) {
        return salesRepository.getRevenueByProductId(id);
    }
}
