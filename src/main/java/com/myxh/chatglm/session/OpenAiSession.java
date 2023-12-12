package com.myxh.chatglm.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myxh.chatglm.model.ChatCompletionRequest;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.concurrent.CompletableFuture;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 会话服务接口
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public interface OpenAiSession
{
    EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException;

    CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws InterruptedException;
}
