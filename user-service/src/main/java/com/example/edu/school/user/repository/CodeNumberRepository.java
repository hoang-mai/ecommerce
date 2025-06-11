package com.example.edu.school.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.edu.school.user.model.CodeNumber;
import com.example.edu.school.user.model.Role;

@Repository
public interface CodeNumberRepository extends JpaRepository<CodeNumber, Role>{

}

