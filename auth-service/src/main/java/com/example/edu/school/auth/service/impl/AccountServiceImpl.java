package com.example.edu.school.auth.service.impl;

import com.example.edu.school.auth.client.UserClient;
import com.example.edu.school.auth.client.dto.ReqCreateUserDTO;
import com.example.edu.school.auth.dto.account.ReqCreateAccountDTO;

import com.example.edu.school.auth.message.command.CreateUserCommand;
import com.example.edu.school.auth.message.saga.CreateUserSaga;
import com.example.edu.school.auth.message.saga.state.CreateUserSagaState;
import com.example.edu.school.auth.service.AccountService;
import com.example.edu.school.auth.service.KeyCloakService;
import com.example.edu.school.library.utils.Constant;
import com.example.edu.school.library.utils.FnCommon;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final KeyCloakService keyCloakService;


    @Override
    public String createAccount(ReqCreateAccountDTO reqCreateAccountDTO) {
        String email = genEmail(reqCreateAccountDTO);
        return keyCloakService.register(reqCreateAccountDTO, email);
    }

    @Override
    public void deleteAccount(String accountId) {

    }

    /**
     * Tạo email từ thông tin tài khoản.
     * Quy tắc tạo email:
     * - Lấy chữ cái đầu tiên của tên (lastName) viết hoa.
     * - Lấy các chữ cái còn lại của tên.
     * - Lấy chữ cái đầu tiên của họ (firstName) viết hoa.
     * - Nếu có tên đệm (middleName), lấy chữ cái đầu tiên viết hoa.
     * - Kết thúc với "@school.edu.vn".
     * Ví dụ: Họ là "Nguyễn", Tên là "Văn", Tên đệm là "A" thì email sẽ là "Van.NA@school.edu.vn".
     *
     * @param reqCreateAccountDTO DTO chứa thông tin tài khoản mới
     * @return email được tạo
     */
    private String genEmail(ReqCreateAccountDTO reqCreateAccountDTO) {
        String lastNameWithoutAccents = FnCommon.removeVietnameseAccents(reqCreateAccountDTO.getLastName());
        return lastNameWithoutAccents.substring(0, 1).toUpperCase()
                + lastNameWithoutAccents.substring(1).toLowerCase() + "."
                + reqCreateAccountDTO.getFirstName().toUpperCase().charAt(0)
                + (FnCommon.isNullOrEmpty(reqCreateAccountDTO.getMiddleName()) ? reqCreateAccountDTO.getMiddleName().toUpperCase().charAt(0) : "")
                + Constant.EMAIL_DOMAIN;
    }
}
