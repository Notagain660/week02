package org.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class Ollama_Config {
    @Bean
    public RestClient ollamRestClient() {
        return RestClient.builder()
                         .baseUrl("http://localhost:11435")
                         .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                         //CONTENT_TYPE 头表示请求体是 JSON，通常也是需要的。
                         .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                         //HttpHeaders.ACCEPT 是 HTTP 标准头 Accept，表示客户端能处理的响应内容类型。
                         //显式设置：明确告诉服务器你期望的格式，避免某些 API 默认返回其他格式（如纯文本）时出现问题。
                         .build();

    }
}
