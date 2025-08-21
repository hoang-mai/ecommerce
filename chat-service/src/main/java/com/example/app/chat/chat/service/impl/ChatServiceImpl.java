package com.example.app.chat.chat.service.impl;

import com.example.app.chat.chat.dto.ReqPrivateMessageDTO;
import com.example.app.chat.chat.dto.ResChatPreviewDTO;
import com.example.app.chat.chat.dto.ResMessageDTO;
import com.example.app.chat.chat.entity.Chat;
import com.example.app.chat.chat.entity.ChatMember;
import com.example.app.chat.chat.entity.Message;
import com.example.app.chat.chat.entity.User;
import com.example.app.chat.chat.repository.ChatMemberRepository;
import com.example.app.chat.chat.repository.ChatRepository;
import com.example.app.chat.chat.repository.MessageRepository;
import com.example.app.chat.chat.repository.UserRepository;
import com.example.app.chat.chat.service.ChatService;
import com.example.app.chat.library.component.UserHelper;
import com.example.app.chat.library.exception.HttpRequestException;
import com.example.app.chat.library.exception.NotFoundException;
import com.example.app.chat.library.utils.FnCommon;
import com.example.app.chat.library.utils.MessageError;
import com.example.app.chat.library.utils.PageResponse;
import com.example.app.chat.user.ReqGetUserChatDTO;
import com.example.app.chat.user.ResGetUserChatDTO;
import com.example.app.chat.user.UserServiceGrpc;
import com.example.app.chat.utils.base_response.BaseResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UserHelper userHelper;
    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

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
                    .nickName(sender.getFirstName()+" "+sender.getMiddleName()+" "+sender.getLastName())
                    .build();
            ChatMember chatReceiver = ChatMember.builder()
                    .chatId(chat.getChatId())
                    .userId(receiver.getUserId())
                    .nickName(receiver.getFirstName()+" "+receiver.getMiddleName()+" "+receiver.getLastName())
                    .build();
            chatMemberRepository.save(chatSender);
            chatMemberRepository.save(chatReceiver);
            Message message = Message.builder()
                    .messageType(reqPrivateMessageDTO.getMessageType())
                    .messageContent(reqPrivateMessageDTO.getMessageContent())
                    .chatMemberId(chatSender.getChatMemberId())
                    .chatId(chat.getChatId())
                    .build();
            messageRepository.save(message);
        }else{
            ChatMember chatSender = chatMemberRepository.findByChatIdAndUserId(reqPrivateMessageDTO.getChatId(), senderId)
                    .orElseThrow(() -> new NotFoundException(MessageError.CHAT_MEMBER_NOT_FOUND));
            Message message = Message.builder()
                    .chatId(reqPrivateMessageDTO.getChatId())
                    .messageType(reqPrivateMessageDTO.getMessageType())
                    .messageContent(reqPrivateMessageDTO.getMessageContent())
                    .chatMemberId(chatSender.getChatMemberId())
                    .build();
            messageRepository.save(message);
        }
    }

    @Override
    public PageResponse<ResChatPreviewDTO> getListChatPreview(Long userId, int pageNo, int pageSize) {
        Long currentUserId = userHelper.getCurrentUserId();
        if (!Objects.equals(currentUserId, userId)) {
            throw new NotFoundException(MessageError.USER_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ResChatPreviewDTO> resChatPreviewDTOS=chatRepository.findByUserId(userId, pageable);
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
     *
     * @param userId ID của người dùng cần lấy thông tin
     * @return User đối tượng chứa thông tin người dùng
     */
    private User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
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
        } else {
            return userOptional.get();
        }
    }
}
