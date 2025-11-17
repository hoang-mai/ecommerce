package com.ecommerce.read.repository;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.read.entity.UserView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserViewRepository extends MongoRepository<UserView, Long> {


    @Query("""
            {
                $and: [
                    { $or: [ { ?0: { $eq: null } }, { account_status: ?0 } ] },
                    { $or: [ { ?1: { $eq: null } }, { role: ?1 } ] },
                    {
                        $or: [
                            { ?2: { $eq: null } },
                            { username: { $regex: ?2, $options: 'i' } },
                            { email: { $regex: ?2, $options: 'i' } },
                            { first_name: { $regex: ?2, $options: 'i' } },
                            { middle_name: { $regex: ?2, $options: 'i' } },
                            { last_name: { $regex: ?2, $options: 'i' } }
                        ]
                    }
                ]
            }
            """)
    Page<UserView> getUserView(AccountStatus accountStatus, Role role, String keyword, Pageable pageable);

}
