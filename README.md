# 智谱 Ai 大模型开放 SDK - By MYXH

为了让研发更快，更方便地接入使用智谱 Ai 大模型。从而开发的 chatglm-sdk-java 。

此 SDK 设计，以 Session 会话模型，提供工厂创建服务。代码非常清晰，易于扩展、易于维护。[感谢智谱 AI 团队](https://www.zhipuai.cn/)。

---

> **作者**：MYXH，GitHub：[https://github.com/MYXHcode](https://github.com/MYXHcode)

## 目录

1. 组件配置

2. 功能测试

    1. 代码执行 - `使用：代码的方式主要用于程序接入`

    2. 脚本测试 - `测试：生成 Token，直接通过 HTTP 访问 Ai 服务`

3. 程序接入

## 1. 组件配置

- 申请 ApiKey：[https://open.bigmodel.cn/usercenter/apikeys](https://open.bigmodel.cn/usercenter/apikeys) - 注册申请开通，即可获得 ApiKey

- 运行环境：JDK 1.8+

- maven pom - `暂时测试阶段，未推送到Maven中央仓库，需要下载代码本地 install 后使用`

```pom
<dependency>
    <groupId>com.myxh.chatglm</groupId>
    <artifactId>chatglm-sdk-java</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

- 源码(Github)：[https://github.com/MYXHcode/chatglm-sdk-java](https://github.com/MYXHcode/chatglm-sdk-java)

## 2. 功能测试

### 2.1 代码执行

```java
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
        configuration.setApiSecretKey("2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMw*****");

        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        // 3. 开启会话
        this.openAiSession = factory.openSession();
    }

    /**
     * 流式对话
     */
    @Test
    public void testCompletions() throws JsonProcessingException, InterruptedException
    {
        // 入参；模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();

        // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setModel(Model.CHATGLM_LITE);
        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>()
        {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("写个java冒泡排序")
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
            public void onClosed(EventSource eventSource) {
                log.info("对话完成");
            }
        });

        // 等待
        new CountDownLatch(1).await();
    }

}
```

- 这是一个单元测试类，也是最常使用的流式对话模式。

### 2.2 脚本测试

```java
@Test
public void testCurl()
{
    // 1. 配置文件
    Configuration configuration = new Configuration();
    configuration.setApiHost("https://open.bigmodel.cn/");
    configuration.setApiSecretKey("2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMw*****");

    // 2. 获取Token
    String token = BearerTokenUtils.getToken(configuration.getApiKey(), configuration.getApiSecret());
    log.info("1. 在智谱Ai官网，申请 ApiSeretKey 配置到此测试类中，替换 setApiSecretKey 值。 https://open.bigmodel.cn/usercenter/apikeys");
    log.info("2. 运行 testCurl 获取 token：{}", token);
    log.info("3. 将获得的 token 值，复制到 curl.sh 中，填写到 Authorization: Bearer 后面");
    log.info("4. 执行完步骤 3 以后，可以复制直接运行 curl.sh 文件，或者复制 curl.sh 文件内容到控制台/终端/ApiPost中运行");
}
```

```shell
curl -X POST \
        -H "Authorization: Bearer <把获得的Token填写这，并去掉两个尖括号>" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -H "Accept: text/event-stream" \
        -d '{
        "top_p": 0.7,
        "sseFormat": "data",
        "temperature": 0.9,
        "incremental": true,
        "request_id": "xfg-1696992276607",
        "prompt": [
        {
        "role": "user",
        "content": "写个java冒泡排序"
        }
        ]
        }' \
  http://open.bigmodel.cn/api/paas/v3/model-api/chatglm_lite/sse-invoke
```

- 运行后会获得一个 Token 信息，之后在 curl.sh 中替换 Authorization: Bearer 后面的值。就可以执行测试了。

## 3. 程序接入

SpringBoot 配置类

```java
@Configuration
@EnableConfigurationProperties(ChatGLMSDKConfigProperties.class)
public class ChatGLMSDKConfig
{
    @Bean
    @ConditionalOnProperty(value = "chatglm.sdk.config.enabled", havingValue = "true", matchIfMissing = false)
    public OpenAiSession openAiSession(ChatGLMSDKConfigProperties properties)
    {
        // 1. 配置文件
        com.myxh.chatglm.session.Configuration configuration = new com.myxh.chatglm.session.Configuration();
        configuration.setApiHost(properties.getApiHost());
        configuration.setApiSecretKey(properties.getApiSecretKey());

        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        // 3. 开启会话
        return factory.openSession();
    }
}

@Data
@ConfigurationProperties(prefix = "chatglm.sdk.config", ignoreInvalidFields = true)
public class ChatGLMSDKConfigProperties
{
    /**
     *  状态；open = 开启、close 关闭
     */
    private boolean enable;

    /**
     * 转发地址
     */
    private String apiHost;

    /**
     * 可以申请 apiSecretKey
    */
    private String apiSecretKey;

}
```

```java
@Autowired(required = false)
private OpenAiSession openAiSession;
```

- 注意：如果在服务中配置了关闭启动 ChatGLM SDK 那么注入 openAiSession 为 null

yml 配置

```pom
# ChatGLM SDK Config
chatglm:
  sdk:
    config:
      # 状态；true = 开启、false 关闭
      enabled: true

      # 官网地址
      api-host: https://open.bigmodel.cn/

      # 官网申请 https://open.bigmodel.cn/usercenter/apikeys
      api-secret-key: 2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMw*****
```
