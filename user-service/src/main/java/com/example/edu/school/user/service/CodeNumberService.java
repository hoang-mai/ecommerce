package com.example.edu.school.user.service;

import com.example.edu.school.user.model.CodeNumber;
import com.example.edu.school.user.model.Role;

public interface CodeNumberService {

    CodeNumber getCodeNumberByRole(Role role);

    void saveCodeNumber(CodeNumber codeNumber);

}
