package com.ecommerce.read.repository.impl;

import com.ecommerce.read.entity.SearchView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchViewRepositoryImpl {
    private final MongoTemplate mongoTemplate;

    public SearchView getById(String id){
        return mongoTemplate.findById(id, SearchView.class);
    }
}
