package com.pluxurydolo.codingagent.exception;

public class SystemPromptException extends RuntimeException{
    public SystemPromptException(Throwable cause) {
        super("Произошла ошибка при извлечении системного промпта %s".formatted(cause.getMessage()));
    }
}
