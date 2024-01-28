package com.myxh.chatglm.executor.result;

import okhttp3.sse.EventSourceListener;

/**
 * @author MYXH
 * @date 2024/1/28
 * @description 结果封装器
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public interface ResultHandler
{
    EventSourceListener eventSourceListener(EventSourceListener eventSourceListener);
}
