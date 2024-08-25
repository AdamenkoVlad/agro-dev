package com.abi.agro_back.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("agrarians")
public class Agrarian {
    @Id
    private String id;

    private String title;

    private String head;

    private List<String> phones;

    private Photo image;

    private List<String> emails;

    private String address;

    private String oblast;

    private String oldRegion;

    private boolean isPriority;

//    private boolean isForOblastOnly;

    private String redirect;

    private String area;

    private String website;

    private Set<SellType> sells;

    private String services;

    private String edrpou;

    private String villageCouncil;

}
