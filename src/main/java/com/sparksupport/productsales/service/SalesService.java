package com.sparksupport.productsales.service;

import com.fasterxml.uuid.Generators;
import com.sparksupport.productsales.dto.ResponseDTO;
import com.sparksupport.productsales.dto.SalesDTO;
import com.sparksupport.productsales.repository.SalesRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SalesService {

    @Value("${custom.error-code}")
    private Integer errorCode;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private SalesRepository salesRepository;
    private ProductService productService;

    public SalesService(SalesRepository salesRepository, ProductService productService) {
        this.salesRepository = salesRepository;
        this.productService = productService;
    }


    public String getTotalRevenue() {

        try {
            log.info("Trying to get total revenue ");
            return salesRepository.getTotalRevenue();
        } catch (Exception e) {
            log.error("Error occurred while get total revenue");
        }
        return "";
    }

    public String getRevenueByProductId(String id) {
        try {
            log.info("Trying to get revenue by product id : {}", id);
            return salesRepository.getRevenueByProductId(id);
        } catch (Exception e) {
            log.error("Error occurred while getting revenue by product id : {}", id);
        }
        return "";
    }

    public ResponseDTO getAllSales() {
        try {
            log.info("Trying to get all sales");
            return new ResponseDTO(salesRepository.getAllSales(), 200, true, null);
        } catch (Exception e) {
            log.error("Error occurred while getting all sales");
            return new ResponseDTO(null, errorCode, true, e.getMessage());
        }
    }

    public ResponseDTO getSalesById(String id) {
        try {
            log.info("Trying to get sales by id : {}", id);
            return new ResponseDTO(salesRepository.getSalesById(id), 200, true, null);
        } catch (Exception e) {
            log.error("Error occurred while getting sales by id : {}", id);
            return new ResponseDTO(null, errorCode, true, e.getMessage());
        }
    }

    public ResponseDTO updateSalesById(String salesId, SalesDTO salesDTO) {
        try {
            log.info("Trying to update sales by id : {}", salesId);
            String productId = salesRepository.getSalesById(salesId).getProductId();
            log.info("Product id : {}", productId);

            int productStock = productService.getAvailableProductQuantity(productId);
            log.info("Product stock available : {}", productStock);

            if (productStock >= salesDTO.getQuantity()) {
                salesRepository.updateSalesById(salesDTO.getQuantity(), salesDTO.getSalesDate(), salesId);
                productService.updateProductQuantity(productId, salesDTO.getQuantity());
            } else {
                log.error("Error occurred while updating sales the stock quantity : {} and trying to sell : {}", productStock, salesDTO.getQuantity());
                return new ResponseDTO(null, errorCode, false, "Sales quantity is more than the stock quantity.");
            }
        } catch (Exception e) {
            log.error("Error occurred while updating sales by id:{}", salesId);
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
        return new ResponseDTO("Sales updated successfully!", 200, true, null);

    }

    public ResponseDTO insertSalesByProductId(String productId, List<SalesDTO> salesDTOList) {
        try {
            int productStock = productService.getAvailableProductQuantity(productId);
            for (SalesDTO salesDTO : salesDTOList) {
                try {
                    log.info("Trying to insert sales : {}", salesDTO.toString());
                    salesDTO.setId(Generators.timeBasedGenerator().generate().toString());
                    if (productStock > salesDTO.getQuantity()) {
                        salesRepository.insertSales(salesDTO.getId(), salesDTO.getQuantity(), salesDTO.getSalesDate(), productId);
                        productService.updateProductQuantity(productId, salesDTO.getQuantity());
                        productStock -= salesDTO.getQuantity();
                    } else {
                        log.error("Error occurred while inserting sales the stock quantity : {} and trying to sell : {}", productStock, salesDTO.getQuantity());
                    }
                } catch (Exception e) {
                    log.error("Error occurred while inserting sales: {}", salesDTO.toString());
                }
            }
            return new ResponseDTO("Sales inserted successfully!", 200, true, null);

        } catch (Exception e) {
            log.error("Error occurred while inserting sales...");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseDTO deleteSalesById(String id) {
        try {
            log.info("Trying to delete sales by id : {}", id);
            salesRepository.deleteSalesById(id);
        } catch (Exception e) {
            log.error("Error occurred while deleting sales by id:{}", id);
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
        return new ResponseDTO("Sales deleted successfully!", 200, true, null);

    }

    public List<String> getSalesByProductId(String productId) {
        return salesRepository.getSalesByProductId(productId);
    }
}
