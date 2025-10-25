package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.dto.*;
import com.example.app.chat.chat.dto.*;
import com.ecommerce.chat.entity.Chat;
import com.ecommerce.chat.entity.ChatMember;
import com.ecommerce.chat.entity.Message;
import com.ecommerce.chat.entity.User;
import com.ecommerce.chat.repository.ChatMemberRepository;
import com.ecommerce.chat.repository.ChatRepository;
import com.ecommerce.chat.repository.MessageRepository;
import com.ecommerce.chat.repository.UserRepository;
import com.ecommerce.chat.service.ChatService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.example.app.chat.user.ReqGetUserChatDTO;
import com.example.app.chat.user.ResGetUserChatDTO;
import com.example.app.chat.user.UserServiceGrpc;
import com.example.app.chat.utils.base_response.BaseResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UserHelper userHelper;
    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    @Override
    public void createMessagePrivate(ReqPrivateMessageDTO reqPrivateMessageDTO) {
        Long senderId = userHelper.getCurrentUserId();
        Long receiverId = reqPrivateMessageDTO.getReceiverId();

        if (FnCommon.isNotNullOrEmpty(reqPrivateMessageDTO.getChatId())) {
            User sender = getUserById(senderId);
            User receiver = getUserById(receiverId);

            Chat chat = Chat.builder()
                    .isGroupChat(false)
                    .build();
            chatRepository.save(chat);
            ChatMember chatSender = ChatMember.builder()
                    .chatId(chat.getChatId())
                    .userId(sender.getUserId())
                    .nickName(generateNickName(sender))
                    .isDeleted(false)
                    .build();
            ChatMember chatReceiver = ChatMember.builder()
                    .chatId(chat.getChatId())
                    .userId(receiver.getUserId())
                    .nickName(generateNickName(receiver))
                    .isDeleted(false)
                    .build();
            chatMemberRepository.save(chatSender);
            chatMemberRepository.save(chatReceiver);
            Message message = Message.builder()
                    .messageType(reqPrivateMessageDTO.getMessageType())
                    .messageContent(reqPrivateMessageDTO.getMessageContent())
                    .chatMemberId(chatSender.getChatMemberId())
                    .chatId(chat.getChatId())
                    .isDeleted(false)
                    .isUpdated(false)
                    .build();
            messageRepository.save(message);
        } else {
            ChatMember chatSender = chatMemberRepository.findByChatIdAndUserIdAndIsDeleted(reqPrivateMessageDTO.getChatId(), senderId, false)
                    .orElseThrow(() -> new NotFoundException(MessageError.CHAT_MEMBER_NOT_FOUND));
            if (chatRepository.existsByChatId(reqPrivateMessageDTO.getChatId())) {
                throw new NotFoundException(MessageError.CHAT_NOT_FOUND);
            }
            Message message = Message.builder()
                    .chatId(reqPrivateMessageDTO.getChatId())
                    .messageType(reqPrivateMessageDTO.getMessageType())
                    .messageContent(reqPrivateMessageDTO.getMessageContent())
                    .chatMemberId(chatSender.getChatMemberId())
                    .isDeleted(false)
                    .isUpdated(false)
                    .build();
            messageRepository.save(message);
        }
    }


    @Override
    public void createMessageGroup(ReqPrivateMessageDTO reqPrivateMessageDTO) {
        if (!chatRepository.existsByChatId(reqPrivateMessageDTO.getChatId())) {
            throw new NotFoundException(MessageError.CHAT_NOT_FOUND);
        }
        Long senderId = userHelper.getCurrentUserId();
        ChatMember chatSender = chatMemberRepository.findByChatIdAndUserIdAndIsDeleted(reqPrivateMessageDTO.getChatId(), senderId, false)
                .orElseThrow(() -> new NotFoundException(MessageError.CHAT_MEMBER_NOT_FOUND));
        Message message = Message.builder()
                .chatId(reqPrivateMessageDTO.getChatId())
                .messageType(reqPrivateMessageDTO.getMessageType())
                .messageContent(reqPrivateMessageDTO.getMessageContent())
                .chatMemberId(chatSender.getChatMemberId())
                .isDeleted(false)
                .isUpdated(false)
                .build();
        messageRepository.save(message);

        Set<Long> userIds = chatRepository.getUserIdsByChatId(reqPrivateMessageDTO.getChatId());
        Set<String> userIdsInGroup = stringRedisTemplate.opsForSet().members("group:" + reqPrivateMessageDTO.getChatId());
        if (FnCommon.isNotNull(userIdsInGroup)) {
            userIdsInGroup.stream().map(Long::valueOf).toList().forEach(userIds::remove);
        }
    }

    @Override
    @Transactional
    public void createGroupChat(ReqCreateGroupChatDTO reqCreateGroupChatDTO) {
        Chat chat = Chat.builder()
                .isGroupChat(true)
                .build();
        chatRepository.save(chat);
        for (Long userId : reqCreateGroupChatDTO.getUserIds()) {
            // User có thể không tồn tại trong database local nên cần gọi gRPC để lấy thông tin
            User user = getUserById(userId);
            ChatMember chatMember = ChatMember.builder()
                    .chatId(chat.getChatId())
                    .userId(user.getUserId())
                    .nickName(generateNickName(user))
                    .isDeleted(false)
                    .isAdmin(false)
                    .build();
            chatMemberRepository.save(chatMember);
        }
        User adminUser = getUserById(userHelper.getCurrentUserId());
        ChatMember adminChatMember = ChatMember.builder()
                .chatId(chat.getChatId())
                .userId(adminUser.getUserId())
                .nickName(generateNickName(adminUser))
                .isDeleted(false)
                .isAdmin(true)
                .build();
        chatMemberRepository.save(adminChatMember);
        reqCreateGroupChatDTO.getUserIds().add(adminUser.getUserId());
        String chatName = generateChatName(reqCreateGroupChatDTO);
        chat.setChatName(chatName);
        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void addMemberToGroupChat(String chatId, ReqCreateGroupChatDTO reqCreateGroupChatDTO) {
        for (Long userId : reqCreateGroupChatDTO.getUserIds()) {
            Optional<ChatMember> optionalChatMember = chatMemberRepository.findByChatIdAndUserId(chatId, userId);
            if (optionalChatMember.isPresent()) {
                ChatMember chatMember = optionalChatMember.get();
                chatMember.setIsDeleted(false);
                chatMemberRepository.save(chatMember);
                continue;
            }
            // User có thể không tồn tại trong database local nên cần gọi gRPC để lấy thông tin
            User user = getUserById(userId);
            ChatMember chatMember = ChatMember.builder()
                    .chatId(chatId)
                    .userId(user.getUserId())
                    .nickName(generateNickName(user))
                    .isDeleted(false)
                    .isAdmin(false)
                    .build();
            chatMemberRepository.save(chatMember);
        }
    }

    @Override
    public void updateChat(String chatId, ReqUpdateChatDTO reqUpdateChatDTO) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException(MessageError.CHAT_NOT_FOUND));
        if (FnCommon.isNotNullOrEmpty(reqUpdateChatDTO.getChatName())) {
            chat.setChatName(reqUpdateChatDTO.getChatName());
        }
        if (FnCommon.isNotNullOrEmpty(reqUpdateChatDTO.getChatImageUrl())) {
            chat.setChatImageUrl(reqUpdateChatDTO.getChatImageUrl());
        }
        chatRepository.save(chat);
    }


    @Override
    public PageResponse<ResChatPreviewDTO> getListChatPreview(Long userId, int pageNo, int pageSize) {
        Long currentUserId = userHelper.getCurrentUserId();
        if (!Objects.equals(currentUserId, userId)) {
            throw new NotFoundException(MessageError.USER_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ResChatPreviewDTO> resChatPreviewDTOS = chatRepository.findByUserId(userId, pageable);
        return PageResponse.<ResChatPreviewDTO>builder()
                .pageNo(resChatPreviewDTOS.getNumber())
                .pageSize(resChatPreviewDTOS.getSize())
                .hasNextPage(resChatPreviewDTOS.hasNext())
                .hasPreviousPage(resChatPreviewDTOS.hasPrevious())
                .data(resChatPreviewDTOS.getContent())
                .build();
    }

    @Override
    public PageResponse<ResMessageDTO> getChatById(String chatId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ResMessageDTO> resMessageDTOS = messageRepository.findByChatId(chatId, pageable);
        return PageResponse.<ResMessageDTO>builder()
                .pageNo(resMessageDTOS.getNumber())
                .pageSize(resMessageDTOS.getSize())
                .hasNextPage(resMessageDTOS.hasNext())
                .hasPreviousPage(resMessageDTOS.hasPrevious())
                .data(resMessageDTOS.getContent())
                .build();
    }


    /**
     * Lấy thông tin người dùng theo ID
     * B1: Kiểm tra trong cơ sở dữ liệu cục bộ (userRepository)
     * B2: Nếu không tìm thấy, gọi dịch vụ gRPC để lấy thông tin
     * B3: Lưu thông tin người dùng vào cơ sở dữ liệu cục bộ để sử dụng lần sau
     * B4: Trả về đối tượng User chứa thông tin người dùng
     *
     * @param userId ID của người dùng cần lấy thông tin
     * @return User đối tượng chứa thông tin người dùng
     */
    private User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        try {
            UserServiceGrpc.UserServiceBlockingStub stubWithToken = userServiceBlockingStub.withInterceptors(new BearerTokenAuthenticationInterceptor(userHelper.getToken()));
            BaseResponse baseResponse = stubWithToken.getUserChatById(ReqGetUserChatDTO.newBuilder()
                    .setUserId(userHelper.getCurrentUserId())
                    .build());
            if (baseResponse.hasData()) {
                try {
                    ResGetUserChatDTO res = baseResponse.getData().unpack(ResGetUserChatDTO.class);
                    User user = User.builder()
                            .userId(res.getUserId())
                            .avatarUrl(res.getAvatarUrl())
                            .firstName(res.getFirstName())
                            .middleName(res.getMiddleName())
                            .lastName(res.getLastName())
                            .build();
                    userRepository.save(user);
                    return user;
                } catch (InvalidProtocolBufferException e) {
                    throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
                }
            } else {
                throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            }
        } catch (StatusRuntimeException e) {
            Metadata metadata = e.getTrailers();
            LocalDateTime timestamp = LocalDateTime.now();
            if (metadata != null && metadata.containsKey(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER))) {
                String timeStamp = metadata.get(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER));
                if (timeStamp != null && !timeStamp.isEmpty()) {
                    timestamp = LocalDateTime.parse(timeStamp);
                }
            }
            throw new HttpRequestException(e.getStatus().getDescription(), FnCommon.convertGrpcCodeToHttpStatus(e.getStatus().getCode()), timestamp);
        }
    }

    /**
     * Tạo biệt danh cho thành viên trong cuộc trò chuyện
     *
     * @param user Đối tượng User chứa thông tin người dùng
     * @return Biệt danh được tạo dựa trên họ và tên của người dùng
     */
    private String generateNickName(User user) {
        return user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName();
    }

    /**
     * Tạo tên cuộc trò chuyện nhóm dựa trên danh sách ID người dùng
     *
     * @param reqCreateGroupChatDTO Đối tượng chứa danh sách ID người dùng
     * @return Tên cuộc trò chuyện nhóm được tạo
     */
    private String generateChatName(ReqCreateGroupChatDTO reqCreateGroupChatDTO) {
        List<User> users = userRepository.findAllById(reqCreateGroupChatDTO.getUserIds());
        StringBuilder chatName = new StringBuilder();
        for (User user : users) {
            if (!chatName.isEmpty()) {
                chatName.append(", ");
            }
            chatName.append(user.getFirstName()).append(" ").append(user.getMiddleName()).append(" ").append(user.getLastName());
        }
        if (chatName.length() > 30) {
            return chatName.substring(0, 27) + "...";
        }
        return chatName.toString();
    }
}
