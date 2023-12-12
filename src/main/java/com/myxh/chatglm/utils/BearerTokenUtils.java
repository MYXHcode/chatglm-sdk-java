package com.myxh.chatglm.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 签名工具包：过期时间 30 分钟，缓存时间 29 分钟
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Slf4j
public class BearerTokenUtils
{
    // 过期时间；默认 30 分钟
    private static final long expireMillis = 30 * 60 * 1000L;

    // 缓存服务
    public static Cache<String, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(expireMillis - (60 * 1000L), TimeUnit.MILLISECONDS)
            .build();

    /**
     * 对 ApiKey 进行签名
     *
     * @param apiKey    登录创建 ApiKey <a href="https://open.bigmodel.cn/usercenter/apikeys">apikeys</a>
     * @param apiSecret apiKey的后半部分 2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMwuLVfL 取 YQcsJ0j4CMwuLVfL 使用
     * @return Token
     */
    public static String getToken(String apiKey, String apiSecret)
    {
        // 缓存 Token
        String token = cache.getIfPresent(apiKey);

        if (null != token) return token;

        // 创建 Token
        Algorithm algorithm = Algorithm.HMAC256(apiSecret.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_key", apiKey);
        payload.put("exp", System.currentTimeMillis() + expireMillis);
        payload.put("timestamp", Calendar.getInstance().getTimeInMillis());
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", "HS256");
        headerClaims.put("sign_type", "SIGN");
        token = JWT.create().withPayload(payload).withHeader(headerClaims).sign(algorithm);
        cache.put(apiKey, token);

        return token;
    }
}
