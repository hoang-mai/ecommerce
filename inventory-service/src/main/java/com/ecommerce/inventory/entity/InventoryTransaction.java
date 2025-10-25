package com.ecommerce.inventory.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.InventoryTransactionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InventoryTransactionStatus inventoryTransactionStatus;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reference_id")
    private Long referenceId;

}


