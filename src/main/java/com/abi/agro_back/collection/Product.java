package com.abi.agro_back.collection;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("products")
public class Product {

    @Id
    private String  id;

    @NotBlank(message = "title required")
    private String title;

    private String description;

    private String price;

    private Photo image;

    private String redirect;

    private String imagePageId;

}
