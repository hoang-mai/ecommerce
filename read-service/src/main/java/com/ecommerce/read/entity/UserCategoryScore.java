package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.UserCategoryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "user_category_scores")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryScore {

    @Id
    private String _id;

    @Field("categoryScores")
    @Builder.Default
    private Map<String, Double> categoryScores= new HashMap<>();

    public void addCategoryScore(String categoryId, Double score) {
        this.categoryScores.put(categoryId, this.categoryScores.getOrDefault(categoryId, 0.0) + score);
    }
}
