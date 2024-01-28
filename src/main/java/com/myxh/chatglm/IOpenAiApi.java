package com.myxh.chatglm;

import com.myxh.chatglm.model.*;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description OpenAi 接口，用于扩展通用类服务
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public interface IOpenAiApi
{
    String v3_completions = "api/paas/v3/model-api/{model}/sse-invoke";
    String v3_completions_sync = "api/paas/v3/model-api/{model}/invoke";

    @POST(v3_completions)
    Single<ChatCompletionResponse> completions(@Path("model") String model, @Body ChatCompletionRequest chatCompletionRequest);

    @POST(v3_completions_sync)
    Single<ChatCompletionSyncResponse> completions(@Body ChatCompletionRequest chatCompletionRequest);

    String v4 = "api/paas/v4/chat/completions";

    String cogview3 = "api/paas/v4/images/generations";

    @POST(cogview3)
    Single<ImageCompletionResponse> genImages(@Body ImageCompletionRequest imageCompletionRequest);
}
