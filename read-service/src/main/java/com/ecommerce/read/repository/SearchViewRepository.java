package com.ecommerce.read.repository;

import com.ecommerce.read.entity.SearchView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchViewRepository extends MongoRepository<SearchView, String> {
}
