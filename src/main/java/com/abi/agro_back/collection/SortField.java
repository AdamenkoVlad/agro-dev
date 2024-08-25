package com.abi.agro_back.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortField {
    ID("id"),
    TITLE("title"),
    START_DATE("startDate"),
    CREATED_AT("createdAt");

    private final String databaseFieldName;
}
