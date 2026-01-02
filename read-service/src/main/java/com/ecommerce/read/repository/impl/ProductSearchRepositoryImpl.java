package com.ecommerce.read.repository.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.read.entity.ProductSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductSearchRepositoryImpl {
    private final ElasticsearchOperations elasticsearchOperations;

    public Page<ProductSearch> getProductSearch(List<String> productIds, Long categoryId, ProductStatus status, ShopStatus shopStatus, String keyword, Integer star, BigDecimal startPrice, BigDecimal endPrice, Pageable pageable) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        if (FnCommon.isNotNullOrEmptyList(productIds)) {
            boolQuery.filter(f -> f.terms(t -> t
                .field("id")
                .terms(terms -> terms.value(
                    productIds.stream()
                        .map(FieldValue::of)
                        .toList()
                ))
            ));
        }

        if (categoryId != null) {
            boolQuery.filter(f -> f.term(t -> t
                .field("categoryId")
                .value(categoryId)
            ));
        }

        if (FnCommon.isNotNull(status)) {
            boolQuery.filter(f -> f.term(t -> t
                .field("productStatus")
                .value(status.name())
            ));
        }

        if (FnCommon.isNotNull(shopStatus)) {
            boolQuery.filter(f -> f.term(t -> t
                .field("shopStatus")
                .value(shopStatus.name())
            ));
        }


        if (FnCommon.isNotNullOrEmpty(keyword)) {
            boolQuery.must(m -> m.multiMatch(mm -> mm
                .query(keyword)
                .fields("name^3", "categoryName^2")
                .fuzziness("AUTO")
            ));
        }

        if (star != null) {
            boolQuery.filter(f -> f.range(r -> r
                .number(n -> n
                    .field("rating")
                    .gte((double) star)
                )
            ));
        }

        if (startPrice != null || endPrice != null) {
            boolQuery.filter(f -> f.range(r -> r
                .number(n -> {
                    n.field("basePrice");
                    if (startPrice != null) {
                        n.gte(startPrice.doubleValue());
                    }
                    if (endPrice != null) {
                        n.lte(endPrice.doubleValue());
                    }
                    return n;
                })
            ));
        }

        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(q -> q.bool(boolQuery.build()))
            .withPageable(pageable)
            .build();

        SearchHits<ProductSearch> searchHits = elasticsearchOperations.search(
            searchQuery,
            ProductSearch.class,
            IndexCoordinates.of("product_search")
        );

        return SearchHitSupport.searchPageFor(searchHits, pageable)
            .map(SearchHit::getContent);
    }
}
