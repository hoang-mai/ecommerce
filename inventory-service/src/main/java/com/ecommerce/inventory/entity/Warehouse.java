package com.ecommerce.inventory.entity;

import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "province")
    private String province;

    @Column(name = "ward")
    private String ward;

    @Column(name = "detail")
    private String detail;
}