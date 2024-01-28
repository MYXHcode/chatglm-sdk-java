package com.myxh.chatglm.session;

import com.myxh.chatglm.model.ChatCompletionRequest;
import com.myxh.chatglm.model.ChatCompletionSyncResponse;
import com.myxh.chatglm.model.ImageCompletionRequest;
import com.myxh.chatglm.model.ImageCompletionResponse;
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
    EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws Exception;

    CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ImageCompletionResponse genImages(ImageCompletionRequest imageCompletionRequest) throws Exception;

    Configuration configuration();
}
