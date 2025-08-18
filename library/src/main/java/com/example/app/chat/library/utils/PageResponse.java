package com.example.app.chat.library.utils;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PageResponse <T>{
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private List<T> data;

}