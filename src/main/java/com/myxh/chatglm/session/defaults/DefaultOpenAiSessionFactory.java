package com.myxh.chatglm.session.defaults;

import com.myxh.chatglm.IOpenAiApi;
import com.myxh.chatglm.executor.Executor;
import com.myxh.chatglm.interceptor.OpenAiHTTPInterceptor;
import com.myxh.chatglm.model.Model;
import com.myxh.chatglm.session.Configuration;
import com.myxh.chatglm.session.OpenAiSession;
import com.myxh.chatglm.session.OpenAiSessionFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 会话工厂
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public class DefaultOpenAiSessionFactory implements OpenAiSessionFactory
{
    private final Configuration configuration;

    public DefaultOpenAiSessionFactory(Configuration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public OpenAiSession openSession()
    {
        // 1. 日志配置
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(configuration.getLevel());

        // 2. 开启 Http 客户端
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new OpenAiHTTPInterceptor(configuration))
                .connectTimeout(configuration.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(configuration.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(configuration.getReadTimeout(), TimeUnit.SECONDS)
                .build();

        configuration.setOkHttpClient(okHttpClient);

        // 3. 创建 API 服务
        IOpenAiApi openAiApi = new Retrofit.Builder()
                .baseUrl(configuration.getApiHost())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(IOpenAiApi.class);

        configuration.setOpenAiApi(openAiApi);

        // 4. 实例化执行器
        HashMap<Model, Executor> executorGroup = configuration.newExecutorGroup();

        return new DefaultOpenAiSession(configuration, executorGroup);
    }
}
