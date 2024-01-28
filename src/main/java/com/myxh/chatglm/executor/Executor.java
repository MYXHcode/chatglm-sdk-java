package com.myxh.chatglm.executor;

import com.myxh.chatglm.model.ChatCompletionRequest;
import com.myxh.chatglm.model.ChatCompletionSyncResponse;
import com.myxh.chatglm.model.ImageCompletionRequest;
import com.myxh.chatglm.model.ImageCompletionResponse;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author MYXH
 * @date 2024/1/28
 * @description 智谱 AI 通用大模型执行器
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public interface Executor
{
    /**
     * 问答模式，流式反馈
     *
     * @param chatCompletionRequest 请求信息
     * @param eventSourceListener   实现监听：通过监听的 onEvent 方法接收数据
     * @return 应答结果
     * @throws Exception 异常
     */
    EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws Exception;

    /**
     * 问答模式，同步反馈 —— 用流式转化 Future
     *
     * @param chatCompletionRequest 请求信息
     * @return 应答结果
     */
    CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws InterruptedException;

    /**
     * 同步应答接口
     *
     * @param chatCompletionRequest 请求信息
     * @return ChatCompletionSyncResponse
     * @throws IOException 异常
     */
    ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception;

    /**
     * 图片生成接口
     *
     * @param request 请求信息
     * @return 应答结果
     */
    ImageCompletionResponse genImages(ImageCompletionRequest request) throws Exception;
}
