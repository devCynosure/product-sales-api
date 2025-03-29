package com.sparksupport.productsales.service;

import com.sparksupport.productsales.repository.SalesRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SalesService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }


    public String getTotalRevenue() {

        try {
            return salesRepository.getTotalRevenue();
        } catch (Exception e) {
            log.error("Error occurred while get total revenue");
        }
        return "";
    }

    public String getRevenueByProductId(String id) {
        try {
            return salesRepository.getRevenueByProductId(id);
        } catch (Exception e) {
            log.error("Error occurred while getting revenue by product id");
        }
        return "";
    }
}
