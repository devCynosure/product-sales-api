package com.sparksupport.productsales.service;

import com.fasterxml.uuid.Generators;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.sparksupport.productsales.dto.ProductDTO;
import com.sparksupport.productsales.dto.ResponseDTO;
import com.sparksupport.productsales.model.Product;
import com.sparksupport.productsales.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
            return new ResponseDTO(productRepository.getAllProductsPaginated(offset, pageSize), 200, true, null);
        } catch (Exception e) {
            log.error("Error occurred while get all products");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseDTO getProductById(String id) {

        try {
            log.info("Trying to get product by id : {}", id);
            return new ResponseDTO(productRepository.getProductById(id), 200, true, null);
        } catch (Exception e) {
            log.error("Error occurred while get product by id: {}",id);
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseDTO insertProducts(List<ProductDTO> productDTOList) {
        try {
            for (ProductDTO productDTO : productDTOList) {
                try {
                    log.info("Trying to insert product : {}", productDTO.toString());
                    productDTO.setId(Generators.timeBasedGenerator().generate().toString());
                    productRepository.insertProducts(productDTO.getId(), productDTO.getName(), productDTO.getDescription(), productDTO.getPrice(), productDTO.getQuantity());
                } catch (Exception e) {
                    log.error("Error occurred while inserting: {}",productDTO.toString());
                }
            }
            return new ResponseDTO("Products inserted successfully!", 200, true, null);

        } catch (Exception e) {
            log.error("Error occurred while inserting products...");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseDTO updateProduct(String id, ProductDTO productDTO) {

        try {
            log.info("Trying to update product by id : {}", id);
            productRepository.updateProduct(productDTO.getName(), productDTO.getDescription(), productDTO.getPrice(), productDTO.getQuantity(), id);
        } catch (Exception e) {
            log.error("Error occurred while updating product");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
        return new ResponseDTO("Product updated successfully!", 200, true, null);

    }

    public ResponseDTO deleteProduct(String id) {
        try {
            log.info("Trying to delete product by id : {}", id);
            productRepository.deleteProduct(id);
        } catch (Exception e) {
            log.error("Error occurred while deleting product");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
        return new ResponseDTO("Product deleted successfully!", 200, true, null);
    }

    public ResponseDTO getTotalRevenue() {
        try {
            log.info("Trying to get total revenue");
            return new ResponseDTO( salesService.getTotalRevenue(), 200, true, null);

        } catch (Exception e) {
            log.error("Error occurred while getting total revenue");
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }

    }

    public ResponseDTO getRevenueByProductId(String id) {
        try {
            log.info("Trying to get revenue by id : {}",id);
            return new ResponseDTO(salesService.getRevenueByProductId(id), 200, true, null);
        } catch (Exception e) {
            log.error("Error occurred while getting revenue by id : {}",id);
            return new ResponseDTO(null, errorCode, false, e.getMessage());
        }
    }

    public ResponseEntity<byte[]> downloadPdf(){
        try {
            byte[] pdfBytes = generateProductPdf();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error occurred in download pdf : {}",e.getMessage());
        }
        return null;
    }
    public byte[] generateProductPdf() {
        List<ProductDTO> products = productRepository.getAllProducts();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
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
                    table.addCell(productDTO.getId());
                    table.addCell(productDTO.getName());
                    table.addCell(productDTO.getDescription());
                    table.addCell(String.valueOf(productDTO.getPrice()));
                    table.addCell(String.valueOf(productDTO.getQuantity()));
                    table.addCell(String.valueOf(revenue));
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
}
