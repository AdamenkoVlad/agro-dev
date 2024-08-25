package com.abi.agro_back.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("image_pages")
public class ImagePage {
    @Id
    private String id;

    private String title;

    private String description;

    private String excerpt;

    private String advantages;

    private String address;

    private String phone;

    private Photo image;

    private List<Photo> galleryPhotos;

    private String video;

    private String email;

    private String website;

    private String person;

    private boolean visible;

    private List<String> keyWords;

    private List<Socials> socials;

}
