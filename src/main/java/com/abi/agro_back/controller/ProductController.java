package com.abi.agro_back.controller;

import com.abi.agro_back.collection.Product;
import com.abi.agro_back.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "the Product Endpoint")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Product> createProduct(@RequestPart ("image") MultipartFile image,
                                                 @Validated @RequestPart Product product) throws IOException {
        return new ResponseEntity<>(productService.createProduct(image, product), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") String  productId,
                                              @RequestBody Product updatedProduct) {
        return ResponseEntity.ok(productService.updateProduct(productId, updatedProduct));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable("id") String  id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }

    @GetMapping("/image-page/{id}")
    public ResponseEntity<List<Product>> getProductsByImagePageId(@PathVariable("id") String id) {
        return ResponseEntity.ok(productService.getProductsByImagePageId(id));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
