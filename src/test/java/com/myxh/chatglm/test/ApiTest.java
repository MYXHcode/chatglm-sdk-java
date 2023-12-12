package com.myxh.chatglm.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.myxh.chatglm.model.*;
import com.myxh.chatglm.session.Configuration;
import com.myxh.chatglm.session.OpenAiSession;
import com.myxh.chatglm.session.OpenAiSessionFactory;
import com.myxh.chatglm.session.defaults.DefaultOpenAiSessionFactory;
import com.myxh.chatglm.utils.BearerTokenUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 在官网申请 ApiSecretKey <a href="https://open.bigmodel.cn/usercenter/apikeys">ApiSecretKey</a>
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Slf4j
public class ApiTest
{
    private OpenAiSession openAiSession;

    @Before
    public void testOpenAiSessionFactory()
    {
        // 1. 配置文件
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMwuLVfL");
        configuration.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        // 3. 开启会话
        this.openAiSession = factory.openSession();
    }

    /**
     * 流式对话
     */
    @Test
    public void testCompletions() throws JsonProcessingException, InterruptedException
    {
        // 入参；模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();

        // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setModel(Model.CHATGLM_TURBO);
        request.setIncremental(false);

        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("1+2")
                        .build());

                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("Okay")
                        .build());

                /*
                system 和 user 为一组出现。如果有参数类型为 system 则 system + user 一组一起传递。
                 */
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.system.getCode())
                        .content("1+1=2")
                        .build());

                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("Okay")
                        .build());

                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("1+2")
                        .build());
            }
        });

        // 请求
        openAiSession.completions(request, new EventSourceListener()
        {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data)
            {
                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                log.info("测试结果 onEvent：{}", response.getData());

                // type 消息类型，add 增量，finish 结束，error 错误，interrupted 中断
                if (EventType.finish.getCode().equals(type))
                {
                    ChatCompletionResponse.Meta meta = JSON.parseObject(response.getMeta(), ChatCompletionResponse.Meta.class);
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(meta));
                }
            }

            @Override
            public void onClosed(EventSource eventSource)
            {
                log.info("对话完成");
            }
        });

        // 等待
        new CountDownLatch(1).await();
    }

    /**
     * 同步请求
     */
    @Test
    public void testCompletionsFuture() throws ExecutionException, InterruptedException
    {
        // 入参；模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();
        // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setModel(Model.CHATGLM_LITE);

        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("写个java冒泡排序")
                        .build());
            }
        });

        CompletableFuture<String> future = openAiSession.completions(request);
        String response = future.get();

        log.info("测试结果：{}", response);
    }


    @Test
    public void testCurl()
    {
        // 1. 配置文件
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMwuLVfL");

        // 2. 获取 Token
        String token = BearerTokenUtils.getToken(configuration.getApiKey(), configuration.getApiSecret());
        log.info("1. 在智谱 Ai 官网，申请 ApiSeretKey 配置到此测试类中，替换 setApiSecretKey 值。 https://open.bigmodel.cn/usercenter/apikeys");
        log.info("2. 运行 testCurl 获取 token：{}", token);
        log.info("3. 将获得的 token 值，复制到 curl.sh 中，填写到 Authorization: Bearer 后面");
        log.info("4. 执行完步骤 3 以后，可以复制直接运行 curl.sh 文件，或者复制 curl.sh 文件内容到控制台/终端/ApiPost中运行");
    }
}
