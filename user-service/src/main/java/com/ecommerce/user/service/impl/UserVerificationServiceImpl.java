package com.ecommerce.user.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.DuplicateException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.user.dto.ReqRegisterOwnerDTO;
import com.ecommerce.user.dto.ReqRejectOwnerDTO;
import com.ecommerce.user.dto.ResUserVerificationDTO;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.entity.UserVerification;
import com.ecommerce.library.enumeration.UserVerificationStatus;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.repository.UserVerificationRepository;
import com.ecommerce.user.service.FileService;
import com.ecommerce.user.service.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserVerificationServiceImpl implements UserVerificationService {
    private final UserRepository userRepository;
    private final UserHelper userHelper;
    private final UserVerificationRepository userVerificationRepository;
    private final FileService fileService;
    @Override
    @Transactional
    public void registerOwner(MultipartFile frontImage, MultipartFile backImage, MultipartFile avatar, ReqRegisterOwnerDTO reqRegisterOwnerDTO) {
        if(userVerificationRepository.existsByVerificationCode(reqRegisterOwnerDTO.getVerificationCode())) {
            throw new DuplicateException(MessageError.VERIFICATION_CODE_ALREADY_USED);
        }
        User user = userRepository.findById(userHelper.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));

        String frontImageUrl = fileService.uploadFile(frontImage, "verifications/" + user.getUserId() + "/front");
        String backImageUrl = fileService.uploadFile(backImage, "verifications/" + user.getUserId() + "/back");
        String avatarUrl = fileService.uploadFile(avatar, "verifications/" + user.getUserId() + "/avatar");

        UserVerification userVerification = UserVerification.builder()
                .user(user)
                .verificationCode(reqRegisterOwnerDTO.getVerificationCode())
                .frontImageUrl(frontImageUrl)
                .backImageUrl(backImageUrl)
                .accountNumber(reqRegisterOwnerDTO.getAccountNumber())
                .bankName(reqRegisterOwnerDTO.getBankName())
                .avatarUrl(avatarUrl)
                .userVerificationStatus(UserVerificationStatus.PENDING)
                .build();
        userVerificationRepository.save(userVerification);
    }



    @Override
    public void rejectOwnerRequest(Long userVerificationId, ReqRejectOwnerDTO reason) {
        UserVerification userVerification = userVerificationRepository.findById(userVerificationId)
                .orElseThrow(() -> new NotFoundException(MessageError.USER_VERIFICATION_NOT_FOUND));
        userVerification.setUserVerificationStatus(UserVerificationStatus.REJECTED);
        userVerification.setRejectReason(reason.getReason());
        userVerificationRepository.save(userVerification);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ResUserVerificationDTO> searchUserVerifications(
            UserVerificationStatus status,
            String keyword,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<UserVerification> verificationsPage = userVerificationRepository
                .searchUserVerifications(status, keyword, pageable);

        return buildPageResponse(verificationsPage);
    }

    private PageResponse<ResUserVerificationDTO> buildPageResponse(Page<UserVerification> verificationsPage) {
        List<ResUserVerificationDTO> verificationResponses = verificationsPage.getContent().stream()
                .map(this::convertToUserVerificationDTO)
                .collect(Collectors.toList());

        return PageResponse.<ResUserVerificationDTO>builder()
                .pageNo(verificationsPage.getNumber())
                .pageSize(verificationsPage.getSize())
                .totalElements(verificationsPage.getTotalElements())
                .totalPages(verificationsPage.getTotalPages())
                .hasNextPage(verificationsPage.hasNext())
                .hasPreviousPage(verificationsPage.hasPrevious())
                .data(verificationResponses)
                .build();
    }

    private ResUserVerificationDTO convertToUserVerificationDTO(UserVerification verification) {
        User user = verification.getUser();
        return ResUserVerificationDTO.builder()
                .userVerificationId(verification.getUserVerificationId())
                .verificationCode(verification.getVerificationCode())
                .avatarUrl(fileService.getPresignedUrl(verification.getAvatarUrl()))
                .accountNumber(maskAccountNumber(verification.getAccountNumber()))
                .bankName(verification.getBankName())
                .frontImageUrl(fileService.getPresignedUrl(verification.getFrontImageUrl()))
                .backImageUrl(fileService.getPresignedUrl(verification.getBackImageUrl()))
                .rejectReason(verification.getRejectReason())
                .userVerificationStatus(verification.getUserVerificationStatus())
                .userId(user != null ? user.getUserId() : null)
                .userName(user != null ? buildFullName(user) : null)
                .userEmail(user != null ? user.getEmail() : null)
                .createdAt(verification.getCreatedAt())
                .updatedAt(verification.getUpdatedAt())
                .build();
    }

    private String buildFullName(User user) {
        StringBuilder fullName = new StringBuilder();
        if (FnCommon.isNotNullOrEmpty(user.getFirstName())) {
            fullName.append(user.getFirstName());
        }
        if (FnCommon.isNotNullOrEmpty(user.getLastName())) {
            if (!fullName.isEmpty()) fullName.append(" ");
            fullName.append(user.getMiddleName());
        }
        if (FnCommon.isNotNullOrEmpty(user.getLastName())) {
            if (!fullName.isEmpty()) fullName.append(" ");
            fullName.append(user.getLastName());
        }
        return !fullName.isEmpty() ? fullName.toString() : null;
    }
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null) return null;
        int len = accountNumber.length();
        if (len <= 3) return accountNumber;
        return "*".repeat(len - 3) +
                accountNumber.substring(len - 3);
    }

    @Override
    public PageResponse<ResUserVerificationDTO> getCurrentUserVerification(
            UserVerificationStatus status,
            String keyword,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir) {
        Long currentUserId = userHelper.getCurrentUserId();

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<UserVerification> verificationsPage = userVerificationRepository.findByUserId(
                currentUserId, status, keyword, pageable);

        return buildPageResponse(verificationsPage);
    }
}
