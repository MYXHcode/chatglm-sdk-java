package com.myxh.chatglm.interceptor;

import com.myxh.chatglm.session.Configuration;
import com.myxh.chatglm.utils.BearerTokenUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 接口拦截器
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public class OpenAiHTTPInterceptor implements Interceptor
{
    /**
     * 智普 Ai，Jwt 加密 Token
     */
    private final Configuration configuration;

    public OpenAiHTTPInterceptor(Configuration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public @NotNull Response intercept(Chain chain) throws IOException
    {
        // 1. 获取原始 Request
        Request original = chain.request();

        // 2. 构建请求
        Request request = original.newBuilder()
                .url(original.url())
                .header("Authorization", "Bearer " + BearerTokenUtils.getToken(configuration.getApiKey(), configuration.getApiSecret()))
                .header("Content-Type", Configuration.JSON_CONTENT_TYPE)
                .header("User-Agent", Configuration.DEFAULT_USER_AGENT)
                .header("Accept", null != original.header("Accept") ? original.header("Accept") : Configuration.SSE_CONTENT_TYPE)
                .method(original.method(), original.body())
                .build();

        // 3. 返回执行结果
        return chain.proceed(request);
    }
}
