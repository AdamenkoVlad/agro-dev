package com.abi.agro_back.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
@AllArgsConstructor
public class Photo {

    String key;
    URL url;

}
