package com.ecommerce.read.repository;

import com.ecommerce.read.entity.UserView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserViewRepository extends MongoRepository<UserView, String> {

}
