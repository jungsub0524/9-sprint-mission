package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {



    // 1) Repository Bean 등록 (인터페이스 타입으로 노출)
    // 아래는 @Bean 메서드
    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository();
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository();
    }

    // 2) Service Bean 등록 (인터페이스 타입으로 노출)
    @Bean
    public UserService userService(UserRepository userRepository) {
        return new BasicUserService(userRepository);
    }

    @Bean
    public ChannelService channelService(ChannelRepository channelRepository) {
        return new BasicChannelService(channelRepository);
    }

    @Bean
    public MessageService messageService(
            MessageRepository messageRepository,
            ChannelRepository channelRepository,
            UserRepository userRepository
    ) {
        return new BasicMessageService(messageRepository, channelRepository, userRepository);
    }
}
