package com.ecommerce.read.repository;

import com.ecommerce.read.entity.ProductSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearch, String> {
}
