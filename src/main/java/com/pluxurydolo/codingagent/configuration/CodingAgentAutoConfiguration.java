package com.pluxurydolo.codingagent.configuration;

import com.pluxurydolo.codingagent.facade.CodingAgentChatFacade;
import com.pluxurydolo.codingagent.runner.CodingAgentRunner;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CodingAgentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CodingAgentRunner codingAgentRunner(CodingAgentChatFacade codingAgentChatFacade) {
        return new CodingAgentRunner(codingAgentChatFacade);
    }

    @Bean
    @ConditionalOnMissingBean
    public CodingAgentChatFacade codingAgentChatFacade(ChatClient.Builder chatClientBuilder) {
        return new CodingAgentChatFacade(chatClientBuilder);
    }
}
