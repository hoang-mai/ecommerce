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
import io.eventuate.tram.sagas.orchestration.SagaManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final KeyCloakService keyCloakService;
    private final UserClient userClient;
    private final SagaManager<CreateUserSagaState> createUserSagaStateSagaManager;


    @Override
    public void createAccount(ReqCreateAccountDTO reqCreateAccountDTO) {
        String email = genEmail(reqCreateAccountDTO);
        String accountId = keyCloakService.register(reqCreateAccountDTO, email);
        CreateUserSagaState createUserSagaState = CreateUserSagaState.builder()
                .accountId(accountId)
                .email(email)
                .firstName(reqCreateAccountDTO.getFirstName())
                .middleName(reqCreateAccountDTO.getMiddleName())
                .lastName(reqCreateAccountDTO.getLastName())
                .phoneNumber(reqCreateAccountDTO.getPhoneNumber())
                .address(reqCreateAccountDTO.getAddress())
                .role(reqCreateAccountDTO.getRole())
                .gender(reqCreateAccountDTO.getGender())
                .dateOfBirth(reqCreateAccountDTO.getDateOfBirth())
                .build();
        createUserSagaStateSagaManager.create(createUserSagaState, CreateUserSaga.class, accountId);
    }

    @Override
    public void deleteAccount(String accountId) {

    }

    /**
     * Chuyển đổi từ ReqCreateAccountDTO sang ReqCreateUserDTO.
     *
     * @param reqCreateAccountDTO DTO chứa thông tin tài khoản mới
     * @param accountId           ID của tài khoản đã được tạo
     * @return DTO chứa thông tin người dùng cần tạo
     */
    private ReqCreateUserDTO mapperToReqCreateUserDTO(ReqCreateAccountDTO reqCreateAccountDTO, String accountId, String email) {
        return ReqCreateUserDTO.builder()
                .accountId(accountId)
                .email(email)
                .firstName(reqCreateAccountDTO.getFirstName())
                .lastName(reqCreateAccountDTO.getLastName())
                .phoneNumber(reqCreateAccountDTO.getPhoneNumber())
                .address(reqCreateAccountDTO.getAddress())
                .role(reqCreateAccountDTO.getRole())
                .dateOfBirth(reqCreateAccountDTO.getDateOfBirth())
                .middleName(reqCreateAccountDTO.getMiddleName())
                .gender(reqCreateAccountDTO.getGender())
                .phoneNumber(reqCreateAccountDTO.getPhoneNumber())
                .build();
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
