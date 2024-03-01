package com.myxh.chatglm.model;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
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
     * 是否对返回结果数据做兼容，2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型，与之前的模型在返回结果上有差异，开启 true 可以做兼容
     */
    private Boolean isCompatible = true;

    /**
     * 模型
     */
    private Model model = Model.GLM_3_5_TURBO;

    /**
     * 请求参数 {"role": "user", "content": "你好"}
     * 2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    private List<Prompt> messages;

    /**
     * 请求 ID
     */
    @JsonProperty("request_id")
    private String requestId = String.format("myxh-%d", System.currentTimeMillis());

    /**
     * do_sample 为 true 时启用采样策略，do_sample 为 false 时采样策略 temperature、top_p 将不生效
     * 2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    @JsonProperty("do_sample")
    private Boolean doSample = true;

    /**
     * 使用同步调用时，此参数应当设置为 Fasle 或者省略。表示模型生成完所有内容后一次性返回所有内容。、
     * 如果设置为 True，模型将通过标准 Event Stream ，逐块返回模型生成内容。Event Stream 结束时会返回一条data: [DONE]消息
     * 2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    private Boolean stream = true;

    /**
     * 控制温度【随机性】
     */
    private float temperature = 0.9f;

    /**
     * 多样性控制
     */
    @JsonProperty("top_p")
    private float topP = 0.7f;

    /**
     * 模型输出最大 tokens
     * 2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens = 2048;

    /**
     * 模型在遇到 stop 所制定的字符时将停止生成，目前仅支持单个停止词，格式为["stop_word1"]
     * 2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    private List<String> stop;

    /**
     * 可供模型调用的工具列表，tools 字段会计算 tokens ，同样受到 tokens 长度的限制
     * 2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    private List<Tool> tools;

    /**
     * 用于控制模型是如何选择要调用的函数，仅当工具类型为 function 时补充。默认为 auto，当前仅支持 auto
     * 2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    @JsonProperty("tool_choice")
    private String toolChoice = "auto";

    /**
     * 输入给模型的会话信息
     * 用户输入的内容；role=user
     * 挟带历史的内容；role=assistant
     */
    private List<Prompt> prompt;

    /**
     * 智普 AI sse 固定参数 incremental = true 【增量返回】
     */
    private boolean incremental = true;

    /**
     * sseformat，用于兼容解决 sse 增量模式 okhttpsse 截取 data: 后面空格问题，[data: hello]。只在增量模式下使用 sseFormat
     */
    private String sseFormat = "data";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prompt
    {
        private String role;
        private String content;

        public static PromptBuilder builder()
        {
            return new PromptBuilder();
        }

        public static class PromptBuilder
        {
            private String role;
            private String content;

            PromptBuilder()
            {

            }

            public PromptBuilder role(String role)
            {
                this.role = role;

                return this;
            }

            public PromptBuilder content(String content)
            {
                this.content = content;

                return this;
            }

            public PromptBuilder content(Content content)
            {
                this.content = JSON.toJSONString(content);

                return this;
            }

            public Prompt build()
            {
                return new Prompt(this.role, this.content);
            }

            public String toString()
            {
                return "ChatCompletionRequest.Prompt.PromptBuilder(role=" + this.role + ", content=" + this.content + ")";
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Content
        {
            private String type = Type.text.code;
            private String text;

            @JsonProperty("image_url")
            private ImageUrl imageUrl;

            @Getter
            @AllArgsConstructor
            public enum Type
            {
                text("text", "文本"),
                image_url("image_url", "图"),
                ;

                private final String code;
                private final String info;
            }

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class ImageUrl
            {
                private String url;
            }
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tool
    {
        private Type type;
        private Function function;
        private Retrieval retrieval;

        @JsonProperty("web_search")
        private WebSearch webSearch;

        public String getType()
        {
            return type.code;
        }

        @Getter
        @AllArgsConstructor
        public enum Type
        {
            function("function", "函数功能"),
            retrieval("retrieval", "知识库"),
            web_search("web_search", "联网"),
            ;

            private final String code;
            private final String info;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Function
        {
            // 函数名称，只能包含 a-z，A-Z，0-9，下划线和中横线。最大长度限制为 64
            private String name;

            // 用于描述函数功能。模型会根据这段描述决定函数调用方式。
            private String description;

            // parameter 字段需要传入一个 Json Schema 对象，以准确地定义函数所接受的参数。https://open.bigmodel.cn/dev/api#glm-3-turbo
            private Object parameters;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Retrieval
        {
            // 当涉及到知识库 ID 时，请前往开放平台的知识库模块进行创建或获取
            @JsonProperty("knowledge_id")
            private String knowledgeId;

            // 请求模型时的知识库模板，默认模板
            @JsonProperty("prompt_template")
            private String promptTemplate = "\"\"\"\n" +
                    "{{ knowledge}}\n" +
                    "\"\"\"\n" +
                    "中找问题\n" +
                    "\"\"\"\n" +
                    "{{question}}\n" +
                    "\"\"\"";
        }

        // 仅当工具类型为 web_search 时补充，如果 tools 中存在类型 retrieval，此时 web_search 不生效
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class WebSearch
        {
            // 是否启用搜索，默认启用搜索 enable = true/false
            private Boolean enable = true;

            // 强制搜索自定义关键内容，此时模型会根据自定义搜索关键内容返回的结果作为背景知识来回答用户发起的对话
            @JsonProperty("search_query")
            private String searchQuery;
        }


    }

    @Override
    public String toString()
    {
        try
        {
            // 2024 年 1 月发布新模型后调整
            if (Model.GLM_3_5_TURBO.equals(this.model) || Model.GLM_4.equals(this.model) || Model.GLM_4V.equals(this.model))
            {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("model", this.model.getCode());

                if (null == this.messages && null == this.prompt)
                {
                    throw new RuntimeException("One of messages or prompt must not be empty！");
                }

                paramsMap.put("messages", this.messages != null ? this.messages : this.prompt);

                if (null != this.requestId)
                {
                    paramsMap.put("request_id", this.requestId);
                }

                if (null != this.doSample)
                {
                    paramsMap.put("do_sample", this.doSample);
                }

                paramsMap.put("stream", this.stream);
                paramsMap.put("temperature", this.temperature);
                paramsMap.put("top_p", this.topP);
                paramsMap.put("max_tokens", this.maxTokens);

                if (null != this.stop && !this.stop.isEmpty())
                {
                    paramsMap.put("stop", this.stop);
                }

                if (null != this.tools && !this.tools.isEmpty())
                {
                    paramsMap.put("tools", this.tools);
                    paramsMap.put("tool_choice", this.toolChoice);
                }

                return new ObjectMapper().writeValueAsString(paramsMap);
            }

            // 默认
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("request_id", requestId);
            paramsMap.put("prompt", prompt);
            paramsMap.put("incremental", incremental);
            paramsMap.put("temperature", temperature);
            paramsMap.put("top_p", topP);
            paramsMap.put("sseFormat", sseFormat);

            return new ObjectMapper().writeValueAsString(paramsMap);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
