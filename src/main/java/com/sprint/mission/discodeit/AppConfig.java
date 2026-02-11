package com.sprint.mission.discodeit;  // ⚠️ 실제 AppConfig 패키지와 다르면 맞춰줘

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // =========================
    // Repository Beans
    // =========================

    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository();   // 파일 저장
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository(); // 파일 저장
    }

    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository(); // 파일 저장
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new BinaryContentRepository(); // 인터페이스 기본 구현 (현재 구조상 이거 맞음)
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return new ReadStatusRepository(); // JCF 기반
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        return new UserStatusRepository(); // JCF 기반
    }

    // =========================
    // Service Beans
    // =========================

    @Bean
    public UserService userService(
            UserRepository userRepository,
            UserStatusRepository userStatusRepository,
            BinaryContentRepository binaryContentRepository
    ) {
        return new BasicUserService(userRepository, userStatusRepository, binaryContentRepository);
    }

    @Bean
    public ChannelService channelService(
            ChannelRepository channelRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            ReadStatusRepository readStatusRepository
    ) {
        return new BasicChannelService(channelRepository, messageRepository, userRepository, readStatusRepository);
    }

    @Bean
    public MessageService messageService(
            MessageRepository messageRepository,
            ChannelRepository channelRepository,
            UserRepository userRepository,
            BinaryContentRepository binaryContentRepository
    ) {
        return new BasicMessageService(messageRepository, channelRepository, userRepository, binaryContentRepository);
    }
}