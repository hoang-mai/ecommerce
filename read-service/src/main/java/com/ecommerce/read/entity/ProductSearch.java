package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ShopStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;

@Document(indexName = "product_search")
@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@Setting(settingPath = "/elasticsearch/icu-settings.json")
public class ProductSearch {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "vietnamese_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            })
    private String name;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    @Field(type = FieldType.Keyword)
    private String shopId;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "vietnamese_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            })
    private String categoryName;

    @Field(type = FieldType.Double)
    private Double rating;

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal basePrice;

    @Field(type = FieldType.Keyword)
    private ProductStatus productStatus;

    @Field(type = FieldType.Keyword)
    private ShopStatus shopStatus;

    @Field(type= FieldType.Integer)
    private Integer totalSold;
}
