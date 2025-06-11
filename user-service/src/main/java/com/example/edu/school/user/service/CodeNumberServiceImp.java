package com.example.edu.school.user.service;

import org.springframework.stereotype.Service;

import com.example.edu.school.user.model.CodeNumber;
import com.example.edu.school.user.model.Role;
import com.example.edu.school.user.repository.CodeNumberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeNumberServiceImp implements CodeNumberService {
    private final CodeNumberRepository codeNumberRepository;

    @Override
    public CodeNumber getCodeNumberByRole(Role role) {
        return codeNumberRepository.findById(role).orElse(CodeNumber.builder()
                .role(role)
                .currentNumber(-1)
                .build());
    }

    @Override
    public void saveCodeNumber(CodeNumber codeNumber) {
        codeNumberRepository.save(codeNumber);
    }

}
