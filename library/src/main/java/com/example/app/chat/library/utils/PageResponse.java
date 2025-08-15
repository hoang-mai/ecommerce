package com.example.app.chat.library.utils;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PageResponse <T>{
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private T data;

}