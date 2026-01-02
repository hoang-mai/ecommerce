package com.ecommerce.read.repository;

import com.ecommerce.read.entity.SearchKeyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchKeywordRepository extends ElasticsearchRepository<SearchKeyword, String> {
    @Query("""
        {
          "term": {
            "keyword.keyword": "?0"
          }
        }
        """)
    Optional<SearchKeyword> findByKeyword(String keyword);

    @Query("""
        {
             "bool": {
                 "should": [
                     {
                         "match": {
                             "keyword": {
                                 "query": "?0",
                                 "fuzziness": "AUTO"
                             }
                         }
                     }
                 ]
             }
         }
    """)
    Page<SearchKeyword> getPopularSearchKeywords(String keyword, Pageable pageable);

}
