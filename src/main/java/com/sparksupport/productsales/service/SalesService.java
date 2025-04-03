package com.sparksupport.productsales.service;

import com.fasterxml.uuid.Generators;
import com.sparksupport.productsales.dto.ProductDTO;
import com.sparksupport.productsales.dto.ResponseDTO;
import com.sparksupport.productsales.dto.SalesDTO;
import com.sparksupport.productsales.exception.ResourceNotFoundException;
import com.sparksupport.productsales.repository.SalesRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            List<SalesDTO> salesDTOList = salesRepository.getAllSales();
            if (salesDTOList == null) {
                throw new Exception("Sales is empty");
            }
            return new ResponseDTO(salesDTOList, HttpStatus.OK.value(), true, null);
        } catch (Exception e) {
            log.error("Error occurred while getting all sales");
            return new ResponseDTO(null, errorCode, true, e.getMessage());
        }
    }

    public ResponseDTO getSalesById(String id) {
        try {
            log.info("Trying to get sales by id : {}", id);
            SalesDTO salesDTO = salesRepository.getSalesById(id);
            if (salesDTO == null) {
                throw new ResourceNotFoundException("No matching record found for the id " + id);
            }
            return new ResponseDTO(salesDTO, HttpStatus.OK.value(), true, null);
        } catch (Exception e) {
            log.error("Error occurred while getting sales by id : {}", id);
            return new ResponseDTO(null, errorCode, true, e.getMessage());
        }
    }

    public ResponseDTO updateSalesById(String salesId, SalesDTO updatedSalesDTO) {
        try {


            log.info("Trying to update sales by id : {}", salesId);
            SalesDTO oldSalesDTO = salesRepository.getSalesById(salesId);
            if (oldSalesDTO == null) {
                throw new ResourceNotFoundException("No matching record found for the id " + salesId);
            }
            log.info("Product id : {}", oldSalesDTO.getProductId());

            //making changes for returning the dto once updated.
            updatedSalesDTO.setId(salesId);
            updatedSalesDTO.setProductId(oldSalesDTO.getProductId());

            int productInStock = productService.getAvailableProductQuantity(oldSalesDTO.getProductId());
            log.info("Product stock available : {}", productInStock);

            int productInStockUpdated = productInStock + oldSalesDTO.getQuantity();
            if (productInStockUpdated >= updatedSalesDTO.getQuantity()) {
                salesRepository.updateSalesById(updatedSalesDTO.getQuantity(), updatedSalesDTO.getSalesDate(), salesId);
                productInStockUpdated -= updatedSalesDTO.getQuantity();
                productService.updateProductQuantity(oldSalesDTO.getProductId(), productInStockUpdated);
            } else {
                log.error("Error occurred while updating sales the stock quantity : {} and trying to sell : {}", productInStockUpdated, updatedSalesDTO.getQuantity());
                throw new Exception("Sales quantity is more than the stock quantity.");
            }
        } catch (Exception e) {
            log.error("Error occurred while updating sales by id:{}", salesId);
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
        return new ResponseDTO(updatedSalesDTO, 200, true, null);

    }

    public ResponseDTO insertSalesByProductId(String productId, List<SalesDTO> salesDTOList) {
        try {
            List<SalesDTO> insertedSalesDTO = new ArrayList<>();
            ProductDTO productDTO = (ProductDTO) productService.getProductById(productId).getData();
            if (productDTO == null) {
                throw new ResourceNotFoundException("No matching record found for the product id " + productId);
            }
            int productInStock = productDTO.getQuantity();
            for (SalesDTO salesDTO : salesDTOList) {
                try {
                    log.info("Trying to insert sales : {}", salesDTO.toString());
                    salesDTO.setId(Generators.timeBasedGenerator().generate().toString());
                    if (productInStock > salesDTO.getQuantity()) {
                        salesRepository.insertSales(salesDTO.getId(), salesDTO.getQuantity(), salesDTO.getSalesDate(), productId);
                        productInStock -= salesDTO.getQuantity();
                        productService.updateProductQuantity(productId, productInStock);
                        insertedSalesDTO.add(salesDTO);
                    } else {
                        log.error("Error occurred while inserting sales the stock quantity : {} and trying to sell : {}", productInStock, salesDTO.getQuantity());
                    }
                } catch (Exception e) {
                    log.error("Error occurred while inserting sales: {}", salesDTO.toString());
                }
            }
            if (insertedSalesDTO == null) {
                throw new Exception("No record has been inserted.");
            }
            return new ResponseDTO(insertedSalesDTO, 200, true, null);

        } catch (Exception e) {
            log.error("Error occurred while inserting sales...");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseDTO deleteSalesById(String id) {
        try {
            log.info("Trying to delete sales by id : {}", id);
            SalesDTO SalesDTO = salesRepository.getSalesById(id);
            if (SalesDTO == null) {
                throw new ResourceNotFoundException("No matching record found for the id " + id);
            }
            salesRepository.deleteSalesById(id);
        } catch (Exception e) {
            log.error("Error occurred while deleting sales by id:{}", id);
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
        return new ResponseDTO("Sales deleted successfully!", 200, true, null);

    }

    public List<String> getSalesByProductId(String productId) {
        ProductDTO productDTO = (ProductDTO) productService.getProductById(productId).getData();
        if(productDTO == null){
            throw new ResourceNotFoundException("No matching product record found for the id : "+productId);
        }
        List<String> salesId = salesRepository.getSalesByProductId(productId);
        if(salesId == null){
            throw new ResourceNotFoundException("No matching sales record found for the id : "+salesId);
        }

        return salesId;
    }
}
