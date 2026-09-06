package com.pluxurydolo.codingagent.facade;

import com.pluxurydolo.codingagent.exception.SystemPromptException;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.GlobTool;
import org.springaicommunity.agent.tools.GrepTool;
import org.springaicommunity.agent.tools.ShellTools;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

public class CodingAgentChatFacade {
    private final ChatClient chatClient;

    public CodingAgentChatFacade(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultSystem(systemPrompt())
            .defaultTools(tools())
            .defaultAdvisors(advisors())
            .build();
    }

    public String chat(String prompt, String conversationId) {
        String projectDirectory = System.getProperty("user.dir");

        return chatClient.prompt()
            .user(prompt)
            .advisors(spec -> spec.param(CONVERSATION_ID, conversationId))
            .toolContext(Map.of("workingDir", projectDirectory))
            .call()
            .content();
    }

    private static String systemPrompt() {
        String projectDirectory = System.getProperty("user.dir");
        InputStreamSource resource = new ClassPathResource("prompt/SYSTEM.md");

        try {
            String prompt = StreamUtils.copyToString(resource.getInputStream(), UTF_8);
            return prompt.formatted(projectDirectory);
        } catch (IOException exception) {
            throw new SystemPromptException(exception);
        }
    }

    private static Object[] tools() {
        FileSystemTools fileSystemTools = FileSystemTools.builder()
            .build();

        GrepTool grepTool = GrepTool.builder()
            .build();

        GlobTool globTool = GlobTool.builder()
            .build();

        ShellTools shellTools = ShellTools.builder()
            .build();

        Path skillsDirectory = Path.of(".agent/skills");

        if (Files.exists(skillsDirectory)) {
            ToolCallback toolCallback = SkillsTool.builder()
                .addSkillsDirectory(".agent/skills")
                .build();

            return new Object[]{fileSystemTools, grepTool, globTool, shellTools, toolCallback};
        }

        return new Object[]{fileSystemTools, grepTool, globTool, shellTools};
    }

    private static Advisor[] advisors() {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(50)
            .build();

        ToolCallingAdvisor toolCallingAdvisor = ToolCallingAdvisor.builder()
            .conversationHistoryEnabled(true)
            .build();

        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
            .build();

        return new Advisor[]{toolCallingAdvisor, memoryAdvisor};
    }
}
