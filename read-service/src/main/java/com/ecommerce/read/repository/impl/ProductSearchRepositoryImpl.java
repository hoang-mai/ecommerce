package com.ecommerce.read.repository.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
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
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProductSearchRepositoryImpl {
    private final ElasticsearchOperations elasticsearchOperations;

    public Page<ProductSearch> getProductSearch(Map<String, Float> imageScoreMap, Long categoryId, ProductStatus status, ShopStatus shopStatus, String keyword, Integer star, BigDecimal startPrice, BigDecimal endPrice, Pageable pageable) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        List<String> productIds = imageScoreMap != null ? imageScoreMap.keySet().stream().toList() : null;
        final String[] scriptSource = {
            "def id = doc['id'].value;" +
                "return params.scores.containsKey(id) ? params.scores.get(id) : 0.0;"
        };
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
            scriptSource[0] = "def id = doc['id'].value;" +
                "def img = params.scores.containsKey(id) ? params.scores.get(id) : 0.0;" +
                "return (params.w_img * img) + (params.w_txt * _score);";
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
        Query finalQuery;
        if (imageScoreMap != null) {
            Map<String, JsonData> scoreParams = new java.util.HashMap<>();
            scoreParams.put("scores", JsonData.of(imageScoreMap));
            scoreParams.put("w_img", JsonData.of(100.0));
            scoreParams.put("w_txt", JsonData.of(1.0));

            finalQuery = Query.of(q -> q.scriptScore(ss -> ss
                .query(q2 -> q2.bool(boolQuery.build()))
                .script(sc -> sc.source(scriptSource[0]).params(scoreParams))
            ));
        } else {
            finalQuery = Query.of(q -> q.bool(boolQuery.build()));
        }

        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(finalQuery)
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
