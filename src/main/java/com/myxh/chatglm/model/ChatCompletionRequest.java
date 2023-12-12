package com.myxh.chatglm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 请求参数
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Data
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest
{
    /**
     * 模型
     */
    private Model model = Model.CHATGLM_6B_SSE;

    /**
     * 请求 ID
     */
    @JsonProperty("request_id")
    private String requestId = String.format("myxh-%d", System.currentTimeMillis());

    /**
     * 控制温度【随机性】
     */
    private float temperature = 0.9f;

    /**
     * 多样性控制；
     */
    @JsonProperty("top_p")
    private float topP = 0.7f;

    /**
     * 输入给模型的会话信息
     * 用户输入的内容：role=user
     * 挟带历史的内容：role=assistant
     */
    private List<Prompt> prompt;

    /**
     * 智普 AI sse 固定参数 incremental = true 【增量返回】
     */
    private boolean incremental = true;

    /**
     * sseformat, 用于兼容解决 sse 增量模式 okhttpsse 截取 data:后面空格问题, [data: hello]。只在增量模式下使用 sseFormat。
     */
    private String sseFormat = "data";

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prompt
    {
        private String role;
        private String content;
    }

    @Override
    public String toString()
    {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("request_id", requestId);
        paramsMap.put("prompt", prompt);
        paramsMap.put("incremental", incremental);
        paramsMap.put("temperature", temperature);
        paramsMap.put("top_p", topP);
        paramsMap.put("sseFormat", sseFormat);

        try
        {
            return new ObjectMapper().writeValueAsString(paramsMap);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
