package com.myxh.chatglm.test;

import com.alibaba.fastjson.JSON;
import com.myxh.chatglm.model.*;
import com.myxh.chatglm.session.Configuration;
import com.myxh.chatglm.session.OpenAiSession;
import com.myxh.chatglm.session.OpenAiSessionFactory;
import com.myxh.chatglm.session.defaults.DefaultOpenAiSessionFactory;
import com.myxh.chatglm.utils.BearerTokenUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

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
     * 1. 默认 isCompatible = true 会兼容新旧版数据格式
     * 2. GLM_3_5_TURBO、GLM_4 支持联网等插件
     */
    @Test
    public void testCompletions() throws Exception
    {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 入参：模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();

        // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setModel(Model.GLM_3_5_TURBO);
        request.setIncremental(false);

        // 是否对返回结果数据做兼容，2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型，与之前的模型在返回结果上有差异。开启 true 可以做兼容
        request.setIsCompatible(true);

        // 2024 年 1 月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
        request.setTools(new ArrayList<ChatCompletionRequest.Tool>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Tool.builder()
                        .type(ChatCompletionRequest.Tool.Type.web_search)
                        .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("末影小黑xh").build())
                        .build());
            }
        });

        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("末影小黑xh是谁？")
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
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response)
            {
                log.info("对话异常");
                countDownLatch.countDown();
            }
        });

        // 等待
        countDownLatch.await();
    }

    /**
     * 流式对话
     * 1. 与 test_completions 测试类相比，只是设置 isCompatible = false 这样就是使用了新的数据结构。onEvent 处理接收数据有差异
     * 2. 不兼容旧版格式的话，仅支持 GLM_3_5_TURBO、GLM_4 其他模型会有解析错误
     */
    @Test
    public void testCompletionsNew() throws Exception
    {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 入参：模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();

        // GLM_3_5_TURBO、GLM_4
        request.setModel(Model.GLM_3_5_TURBO);
        request.setIsCompatible(false);

        // 2024 年 1 月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
        request.setTools(new ArrayList<ChatCompletionRequest.Tool>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Tool.builder()
                        .type(ChatCompletionRequest.Tool.Type.web_search)
                        .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("末影小黑xh").build())
                        .build());
            }
        });

        request.setMessages(new ArrayList<ChatCompletionRequest.Prompt>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("末影小黑xh是谁？")
                        .build());
            }
        });

        // 请求
        openAiSession.completions(request, new EventSourceListener()
        {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data)
            {
                if ("[DONE]".equals(data))
                {
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(data));

                    return;
                }

                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                log.info("测试结果：{}", JSON.toJSONString(response));
            }

            @Override
            public void onClosed(EventSource eventSource)
            {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response)
            {
                log.error("对话失败", t);
                countDownLatch.countDown();
            }
        });

        // 等待
        countDownLatch.await();
    }

    /**
     * 模型编码：glm-4v
     * 根据输入的自然语言指令和图像信息完成任务，推荐使用 SSE 或同步调用方式请求接口
     * <a href="https://open.bigmodel.cn/dev/api#glm-4v">https://open.bigmodel.cn/dev/api#glm-4v</a>
     */
    @Test
    public void testCompletions4V() throws Exception
    {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 入参：模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();

        // GLM_3_5_TURBO、GLM_4
        request.setModel(Model.GLM_4V);
        request.setStream(true);

        request.setMessages(new ArrayList<ChatCompletionRequest.Prompt>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                // content 字符串格式
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("这个图片写了什么？")
                        .build());

                // content 对象格式
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content(ChatCompletionRequest.Prompt.Content.builder()
                                .type(ChatCompletionRequest.Prompt.Content.Type.text.getCode())
                                .text("这是什么图片？")
                                .build())
                        .build());

                // content 对象格式，上传图片：图片支持url、basde64
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content(ChatCompletionRequest.Prompt.Content.builder()
                                .type(ChatCompletionRequest.Prompt.Content.Type.image_url.getCode())
                                .imageUrl(ChatCompletionRequest.Prompt.Content.ImageUrl.builder().url("https://bugstack.cn/images/article/project/chatgpt/chatgpt-extra-231011-01.png").build())
                                .build())
                        .build());
            }
        });

        openAiSession.completions(request, new EventSourceListener()
        {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data)
            {
                if ("[DONE]".equals(data))
                {
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(data));
                    return;
                }

                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                log.info("测试结果：{}", JSON.toJSONString(response));
            }

            @Override
            public void onClosed(EventSource eventSource)
            {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response)
            {
                log.error("对话失败", t);
                countDownLatch.countDown();
            }
        });

        // 等待
        countDownLatch.await();
    }

    /**
     * 同步请求
     */
    @Test
    public void testCompletionsFuture() throws Exception
    {
        // 入参：模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();

        // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setModel(Model.CHATGLM_TURBO);

        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("1+1")
                        .build());
            }
        });

        CompletableFuture<String> future = openAiSession.completions(request);
        String response = future.get();

        log.info("测试结果：{}", response);
    }

    /**
     * 同步请求
     */
    @Test
    public void testCompletionsSync() throws Exception
    {
        // 入参：模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();

        // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setModel(Model.GLM_3_5_TURBO);

        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("末影小黑xh是谁？")
                        .build());
            }
        });

        // 2024 年 1 月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
        request.setTools(new ArrayList<ChatCompletionRequest.Tool>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Tool.builder()
                        .type(ChatCompletionRequest.Tool.Type.web_search)
                        .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("末影小黑xh").build())
                        .build());
            }
        });

        ChatCompletionSyncResponse response = openAiSession.completionsSync(request);

        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void testGenImages() throws Exception
    {
        ImageCompletionRequest request = new ImageCompletionRequest();
        request.setModel(Model.COGVIEW_3);
        request.setPrompt("画一条小狗");
        ImageCompletionResponse response = openAiSession.genImages(request);
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void testCurl()
    {
        // 1. 配置文件
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMwuLVfL");

        // 2. 获取Token
        String token = BearerTokenUtils.getToken(configuration.getApiKey(), configuration.getApiSecret());
        log.info("1. 在智谱Ai官网，申请 ApiSeretKey 配置到此测试类中，替换 setApiSecretKey 值。 https://open.bigmodel.cn/usercenter/apikeys");
        log.info("2. 运行 testCurl 获取 token：{}", token);
        log.info("3. 将获得的 token 值，复制到 curl.sh 中，填写到 Authorization: Bearer 后面");
        log.info("4. 执行完步骤3以后，可以复制直接运行 curl.sh 文件，或者复制 curl.sh 文件内容到控制台/终端/ApiPost中运行");
    }
}
