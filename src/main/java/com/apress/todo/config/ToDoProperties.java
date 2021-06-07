package com.apress.todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "todo.authentication")
public class ToDoProperties {
    private String findByEmailUri;
    private String username;
    private String password;
}
