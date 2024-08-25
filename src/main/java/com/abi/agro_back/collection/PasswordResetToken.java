package com.abi.agro_back.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("password_reset_token")
public class PasswordResetToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    private String id;

    private String token;

    @DBRef
    private User user;

    @CreatedDate
    private Date expiryDate;
}
