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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("pages")
public class Page {
    @Id
    private String id;

//    @NotNull
    @NotBlank(message = "required field")
//    @Size(min = 5)
    private String title;

    private String content;

    private Photo image;

    @CreatedDate
    private LocalDateTime createdAt;

    private boolean published;

}
