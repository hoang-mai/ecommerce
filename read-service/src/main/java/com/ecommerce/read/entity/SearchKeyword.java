package com.ecommerce.read.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;

@Document(indexName = "search_keywords")
@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@Setting(settingPath = "/elasticsearch/icu-settings.json")
public class SearchKeyword {

    @Id
    private String id;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "vietnamese_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            })
    private String keyword;

    @Field(value = "search_count", type = FieldType.Long)
    private Long searchCount;

    @Field(
        value = "last_searched_at",
        type = FieldType.Date
    )
    private Instant lastSearchedAt;

}
