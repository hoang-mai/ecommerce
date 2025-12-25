package com.ecommerce.read.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "search_views")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchView extends BaseEntity {
    @Id
    private String _id;

    @Field("productIds")
    @Builder.Default
    private List<String> productIds = new ArrayList<>();
}
