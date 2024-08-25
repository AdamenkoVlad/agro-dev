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
@Document("service_requests")
public class ServiceRequest {
    @Id
    private String id;

    private List<String> services;

    private String organization;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}
