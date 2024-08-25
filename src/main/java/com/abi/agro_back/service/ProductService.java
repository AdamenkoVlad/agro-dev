package com.abi.agro_back.service;

import com.abi.agro_back.collection.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    Product createProduct(MultipartFile image, Product product) throws IOException;

    Product getProductById(String productId);

    List<Product> getAllProducts();

    Product updateProduct(String productId, Product updatedProduct);

    void deleteProduct(String  productId);

    List<Product> getProductsByImagePageId(String id);
}
