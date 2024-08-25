package com.abi.agro_back.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("exhibitions")
public class Exhibition {

    @Id
    private String id;

    private String title;

    private String description;

    private String excerpt;

    private Photo image;

    private String phone;

    private String email;

    private String location;

    private List<Photo> galleryPhotos;

    private String address;

    private String website;

    private String contactTitle;

    private List<String> keyWords;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date endDate;

}
