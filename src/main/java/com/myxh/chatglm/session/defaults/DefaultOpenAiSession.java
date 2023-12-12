package com.myxh.chatglm.session.defaults;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.myxh.chatglm.IOpenAiApi;
import com.myxh.chatglm.model.ChatCompletionRequest;
import com.myxh.chatglm.model.ChatCompletionResponse;
import com.myxh.chatglm.model.EventType;
import com.myxh.chatglm.session.Configuration;
import com.myxh.chatglm.session.OpenAiSession;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 会话服务
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Slf4j
public class DefaultOpenAiSession implements OpenAiSession
{
    /**
     * OpenAi 接口
     */
    private final Configuration configuration;

    /**
     * 工厂事件
     */
    private final EventSource.Factory factory;

    public DefaultOpenAiSession(Configuration configuration)
    {
        this.configuration = configuration;
        this.factory = configuration.createRequestFactory();
    }

    @Override
    public EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException
    {
        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAiApi.v3_completions).replace("{model}", chatCompletionRequest.getModel().getCode()))
                .post(RequestBody.create(MediaType.parse("application/json"), chatCompletionRequest.toString()))
                .build();

        // 返回事件结果
        return factory.newEventSource(request, eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws InterruptedException
    {
        // 用于执行异步任务并获取结果
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuffer dataBuffer = new StringBuffer();

        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAiApi.v3_completions).replace("{model}", chatCompletionRequest.getModel().getCode()))
                .post(RequestBody.create(MediaType.parse("application/json"), chatCompletionRequest.toString()))
                .build();

        // 异步响应请求
        factory.newEventSource(request, new EventSourceListener()
        {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data)
            {
                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);

                // type 消息类型，add 增量，finish 结束，error 错误，interrupted 中断
                if (EventType.add.getCode().equals(type))
                {
                    dataBuffer.append(response.getData());
                }
                else if (EventType.finish.getCode().equals(type))
                {
                    future.complete(dataBuffer.toString());
                }
            }

            @Override
            public void onClosed(EventSource eventSource)
            {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response)
            {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });

        return future;
    }
}
