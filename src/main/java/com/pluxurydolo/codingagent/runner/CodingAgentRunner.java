package com.pluxurydolo.codingagent.runner;

import com.pluxurydolo.codingagent.facade.CodingAgentChatFacade;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.CommandLineRunner;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.jline.reader.LineReader.Option.AUTO_FRESH_LINE;

public class CodingAgentRunner implements CommandLineRunner {
    private final CodingAgentChatFacade codingAgentChatFacade;

    public CodingAgentRunner(CodingAgentChatFacade codingAgentChatFacade) {
        this.codingAgentChatFacade = codingAgentChatFacade;
    }

    @Override
    public void run(String... args) throws Exception {
        try (
            Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .encoding(UTF_8)
                .build()
        ) {
            String conversationId = UUID.randomUUID().toString();

            LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .option(AUTO_FRESH_LINE, true)
                .build();

            System.out.println("🚀 Coding Agent готов к работе");

            while (true) {
                String prompt = "\u001B[32m🤖 agent>\u001B[0m ";

                String input = reader.readLine(prompt)
                    .trim();

                if (input.isEmpty()) {
                    System.out.println("🚀 Coding Agent завершил работу");
                    break;
                }

                long startTime = System.currentTimeMillis();
                String response = codingAgentChatFacade.chat(input, conversationId);
                long duration = System.currentTimeMillis() - startTime;

                System.out.println("\u001B[36m" + response + "\u001B[0m");
                System.out.println("\u001B[90m⏱️ " + duration + "ms\u001B[0m\n");
            }
        }
    }
}
