package com.abi.agro_back.collection;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("banners")
public class Banner {
    @Id
    private String id;

    @NotBlank(message = "required field")
    private String title;

    private String info;

    private Photo img;

    private List<String> locations;

    private String address;

    private String phone;

    private String email;

    private String url;

    private boolean bannerAgro;

    private List<String> keyWords;

}
