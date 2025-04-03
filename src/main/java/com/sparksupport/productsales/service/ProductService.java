package com.sparksupport.productsales.service;

import com.fasterxml.uuid.Generators;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.sparksupport.productsales.dto.ProductDTO;
import com.sparksupport.productsales.dto.ResponseDTO;
import com.sparksupport.productsales.exception.ResourceNotFoundException;
import com.sparksupport.productsales.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductService {

    @Value("${custom.error-code}")
    private Integer errorCode;

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private ProductRepository productRepository;

    private SalesService salesService;

    @Autowired
    @Lazy
    public ProductService(ProductRepository productRepository, SalesService salesService) {
        this.productRepository = productRepository;
        this.salesService = salesService;
    }


    public ResponseDTO getAllProducts(Integer pageNo, Integer pageSize) {
        try {
            log.info("Trying to fetch all products ");
            int offset = pageSize * (pageNo - 1);
            List<ProductDTO> productDTOList = productRepository.getAllProductsPaginated(offset, pageSize);
            if (productDTOList == null) {
                throw new ResourceNotFoundException("No Products found.");
            }
            return new ResponseDTO(productDTOList, 200, true, null);
        } catch (Exception e) {
            log.error("Error occurred while get all products");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseDTO getProductById(String id) {

        try {
            log.info("Trying to get product by id : {}", id);
            ProductDTO productDTO = productRepository.getProductById(id);
            if (productDTO == null) {
                throw new ResourceNotFoundException("No matching record found for the id : " + id);
            }
            return new ResponseDTO(productDTO, HttpStatus.OK.value(), true, null);
        } catch (Exception e) {
            log.error("Error occurred while get product by id: {}", id);
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseDTO insertProducts(List<ProductDTO> productDTOList) {
        try {
            List<ProductDTO> insertedRecords = new ArrayList<>();
            for (ProductDTO productDTO : productDTOList) {
                try {
                    log.info("Trying to insert product : {}", productDTO.toString());
                    productDTO.setId(Generators.timeBasedGenerator().generate().toString());
                    productRepository.insertProducts(productDTO.getId(), productDTO.getName(), productDTO.getDescription(), productDTO.getPrice(), productDTO.getQuantity());
                    insertedRecords.add(productDTO);
                } catch (Exception e) {
                    log.error("Error occurred while inserting: {}", productDTO.toString());
                }
            }
            return new ResponseDTO(insertedRecords, 200, true, null);

        } catch (Exception e) {
            log.error("Error occurred while inserting products...");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseDTO updateProduct(String id, ProductDTO productDTO) {

        try {
            log.info("Trying to update product by id : {}", id);
            ProductDTO productDTO1 = (ProductDTO) getProductById(id).getData();
            if (productDTO1 == null) {
                throw new ResourceNotFoundException("Matching record of product for the id " + id + " not found");
            }
            productRepository.updateProduct(productDTO.getName(), productDTO.getDescription(), productDTO.getPrice(), productDTO.getQuantity(), id);
        } catch (Exception e) {
            log.error("Error occurred while updating product");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
        return new ResponseDTO(productDTO, 200, true, null);

    }

    public ResponseDTO deleteProduct(String productId) {
        try {
            log.info("Delete product by id : {} ", productId);
            ProductDTO productDTO1 = (ProductDTO) getProductById(productId).getData();
            if (productDTO1 == null) {
                throw new ResourceNotFoundException("Matching record of product for the id " + productId + " not found");
            }
            List<String> salesIdList = salesService.getSalesByProductId(productId);
            for (String salesId : salesIdList) {
                try {
                    salesService.deleteSalesById(salesId);
                } catch (Exception e) {
                    log.error("Error occurred while deleting the sales record id : {} ", salesId);
                }
            }
            productRepository.deleteProduct(productId);
        } catch (Exception e) {
            log.error("Error occurred while deleting product");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
        return new ResponseDTO("Product : " + productId + "is deleted successfully!", 200, true, null);
    }

    public ResponseDTO getTotalRevenue() {
        try {
            log.info("Trying to get total revenue");
            String totalRevenue = salesService.getTotalRevenue();
            if (totalRevenue == null || totalRevenue.isBlank()) {
                throw new ResourceNotFoundException("There is no revenue to display ");
            }
            return new ResponseDTO(totalRevenue, HttpStatus.OK.value(), true, null);

        } catch (Exception e) {
            log.error("Error occurred while getting total revenue");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }

    }

    public ResponseDTO getRevenueByProductId(String id) {
        try {
            log.info("Trying to get revenue by id : {}", id);
            ProductDTO productDTO1 = (ProductDTO) getProductById(id).getData();
            log.info("Product : {}", productDTO1);
            if (productDTO1 == null) {
                throw new ResourceNotFoundException("Matching record of product for the id " + id + " not found");
            }
            String revenue = salesService.getRevenueByProductId(id);
            log.info("Revenue : {}", revenue);

            if (revenue == null || revenue.isBlank()) {
                throw new ResourceNotFoundException("There is no revenue for the product id : " + id);
            }
            return new ResponseDTO(revenue, HttpStatus.OK.value(), true, null);
        } catch (Exception e) {
            log.error("Error occurred while getting revenue by id : {}", id);
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseEntity<byte[]> downloadPdf() {
        try {
            byte[] pdfBytes = generateProductPdf();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error occurred in download pdf : {}", e.getMessage());
        }
        return null;
    }

    public byte[] generateProductPdf() {

        try {
            List<ProductDTO> products = productRepository.getAllProducts();
            if (products == null) {
                throw new Exception("No products found.");
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Product List").simulateBold().setFontSize(18));

            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 4, 6, 3, 3, 3}))
                    .useAllAvailableWidth();

            table.addHeaderCell("ID");
            table.addHeaderCell("Name");
            table.addHeaderCell("Description");
            table.addHeaderCell("Price");
            table.addHeaderCell("Quantity");
            table.addHeaderCell("Revenue");

            for (ProductDTO productDTO : products) {
                try {
                    log.info("Adding row : {}", productDTO);
                    String revenue = salesService.getRevenueByProductId(productDTO.getId());
                    if(revenue == null || revenue.isBlank()){
                        revenue = "0";
                    }
                    table.addCell(productDTO.getId());
                    table.addCell(productDTO.getName());
                    table.addCell(productDTO.getDescription());
                    table.addCell(String.valueOf(productDTO.getPrice()));
                    table.addCell(String.valueOf(productDTO.getQuantity()));
                    table.addCell(revenue);
                } catch (Exception e) {
                    log.error("Error occurred while inserting the row : {}", productDTO);
                }
            }

            document.add(table);
            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error while generating PDF", e);
        }
        return new byte[0];
    }

    public void updateProductQuantity(String productId, Integer quantity) {
        try {
            log.info("Trying to update product quantity by id : {}", productId);
            ProductDTO productDTO = (ProductDTO) getProductById(productId).getData();
            if (productDTO == null) {
                throw new ResourceNotFoundException("Matching record of product for the id " + productId + " not found");
            }
            productRepository.updateProductQuantity(productId, quantity);
        } catch (Exception e) {
            log.error("Error occurred while updating product quantity : {}", e.getMessage());
        }
    }

    public int getAvailableProductQuantity(String productId) {
        try {
            log.info("Trying to fetch product quantity by id : {}", productId);
            ProductDTO productDTO1 = (ProductDTO) getProductById(productId).getData();
            if (productDTO1 == null) {
                throw new ResourceNotFoundException("Matching record of product for the id " + productId + " not found");
            }
            Integer quantity = productRepository.getAvailableProductQuantity(productId);
            if (quantity == null) {
                throw new Exception("Quantity is null!");
            }
            return quantity;
        } catch (Exception e) {
            log.error("Error occurred while fetch product quantity by id : {}", e.getMessage());
        }
        return 0;
    }
}
