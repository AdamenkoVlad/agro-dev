package com.abi.agro_back.collection;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {

//    @NotNull
    @NotBlank(message = "required field")
//    @Size(min = 5)
    private String title;

    private String content;

    private Photo image;

    private LocalDateTime createdAt;

}
