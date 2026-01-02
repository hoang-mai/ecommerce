package com.ecommerce.read.repository;

import com.ecommerce.read.entity.ProductSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearch, String> {
    List<ProductSearch> findByShopId(String shopId);

}
