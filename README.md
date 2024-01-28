# 智谱 Ai 大模型开放 SDK - By MYXH

为了让研发伙伴更快，更方便的接入使用智谱 Ai 大模型。从而开发的 chatglm-sdk-java 也欢迎👏🏻大家基于智谱API接口补充需要的功能。

此SDK设计，以 Session 会话模型，提供工厂🏭创建服务。代码非常清晰，易于扩展、易于维护。你的 PR/ISSUE 贡献💐会让 AI 更加璀璨，[感谢智谱AI团队](https://www.zhipuai.cn/)。

---

> **作者**：MYXH，GitHub：[https://github.com/MYXHcode](https://github.com/MYXHcode)，CSDN：[https://blog.csdn.net/qq_40734758](https://blog.csdn.net/qq_40734758)

## 👣目录

1. 组件配置

2. 功能测试

    1. 代码执行 - `使用：代码的方式主要用于程序接入`

    2. 脚本测试 - `测试：生成 Token，直接通过 HTTP 访问 Ai 服务`

3. 程序接入

## 1. 组件配置

- 申请 ApiKey：[https://open.bigmodel.cn/usercenter/apikeys](https://open.bigmodel.cn/usercenter/apikeys) - 注册申请开通，即可获得 ApiKey

- 运行环境：JDK 1.8+

- 支持模型：chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro、chatglm_turbo、glm-3-turbo、glm-4、glm-4v、cogview-3

- maven pom - `暂时测试阶段，未推送到 Maven 中央仓库，需要下载代码本地 install 后使用`

```Pom
<dependency>
    <groupId>com.myxh.chatglm</groupId>
    <artifactId>chatglm-sdk-java</artifactId>
    <version>2.0</version>
</dependency>
```

- 源码(Github)：[https://github.com/MYXHcode/chatglm-sdk-java](https://github.com/MYXHcode/chatglm-sdk-java)

## 2. 功能测试

### 2.1 代码执行

```Java
private OpenAiSession openAiSession;

@Before
public void testOpenAiSessionFactory()
{
    // 1. 配置文件
    Configuration configuration = new Configuration();
    configuration.setApiHost("https://open.bigmodel.cn/");
    configuration.setApiSecretKey("2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMwuLVfL");
    configuration.setLevel(HttpLoggingInterceptor.Level.BODY);
    
    // 2. 会话工厂
    OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
   
    // 3. 开启会话
    this.openAiSession = factory.openSession();
}
```

- 测试前申请你的 ApiKey 填写到 setApiSecretKey 中使用。

#### 2.1.1 流式对话 - 兼容旧版模式运行

```Java
/**
 * 流式对话；
 * 1. 默认 isCompatible = true 会兼容新旧版数据格式
 * 2. GLM_3_5_TURBO、GLM_4 支持联网等插件
 */
@Test
public void testCompletions() throws Exception
{
    CountDownLatch countDownLatch = new CountDownLatch(1);
    
    // 入参：模型、请求信息
    ChatCompletionRequest request = new ChatCompletionRequest();

    // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
    request.setModel(Model.GLM_3_5_TURBO); 
    request.setIncremental(false);

   // 是否对返回结果数据做兼容，2024 年 1 月发布的 GLM_3_5_TURBO、GLM_4 模型，与之前的模型在返回结果上有差异。开启 true 可以做兼容
    request.setIsCompatible(true);
    
    // 2024 年 1 月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
    request.setTools(new ArrayList<ChatCompletionRequest.Tool>()
    {
        private static final long serialVersionUID = -7988151926241837899L;
        {
            add(ChatCompletionRequest.Tool.builder()
                    .type(ChatCompletionRequest.Tool.Type.web_search)
                    .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("末影小黑xh").build())
                    .build());
        }
    });
    
    request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>()
    {
        private static final long serialVersionUID = -7988151926241837899L;
        {
            add(ChatCompletionRequest.Prompt.builder()
                    .role(Role.user.getCode())
                    .content("末影小黑xh是谁？")
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
        public void onClosed(EventSource eventSource)
        {
            log.info("对话完成");
            countDownLatch.countDown();
        }
        
        @Override
        public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response)
        {
            log.info("对话异常");
            countDownLatch.countDown();
        }
    });
    
    // 等待
    countDownLatch.await();
}
```

#### 2.1.2 流式对话 - 新版调用

<details><summary><a>👉查看代码</a></summary></br>

```Java
/**
 * 流式对话；
 * 1. 与 testCompletions 测试类相比，只是设置 isCompatible = false 这样就是使用了新的数据结构。onEvent 处理接收数据有差异
 * 2. 不兼容旧版格式的话，仅支持 GLM_3_5_TURBO、GLM_4，其他模型会有解析错误
 */
@Test
public void testCompletionsNew() throws Exception
{
    CountDownLatch countDownLatch = new CountDownLatch(1);

    // 入参；模型、请求信息
    ChatCompletionRequest request = new ChatCompletionRequest();

    // GLM_3_5_TURBO、GLM_4
    request.setModel(Model.GLM_3_5_TURBO);
    request.setIsCompatible(false);
    
    // 24年1月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
    request.setTools(new ArrayList<ChatCompletionRequest.Tool>()
    {
        private static final long serialVersionUID = -7988151926241837899L;
        {
            add(ChatCompletionRequest.Tool.builder()
                    .type(ChatCompletionRequest.Tool.Type.web_search)
                    .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("末影小黑xh").build())
                    .build());
        }
    });
    
    request.setMessages(new ArrayList<ChatCompletionRequest.Prompt>()
    {
        private static final long serialVersionUID = -7988151926241837899L;
        {
            add(ChatCompletionRequest.Prompt.builder()
                    .role(Role.user.getCode())
                    .content("末影小黑xh是谁？")
                    .build());
        }
    });
    
    // 请求
    openAiSession.completions(request, new EventSourceListener()
    {
        @Override
        public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) 
        {
            if ("[DONE]".equals(data)) 
            {
                log.info("[输出结束] Tokens {}", JSON.toJSONString(data));
               
                return;
            }
            
            ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
            log.info("测试结果：{}", JSON.toJSONString(response));
        }
        
        @Override
        public void onClosed(EventSource eventSource)
        {
            log.info("对话完成");
            countDownLatch.countDown();
        }
        
        @Override
        public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response)
        {
            log.error("对话失败", t);
            countDownLatch.countDown();
        }
    });
    
    // 等待
    countDownLatch.await();
}
```

</details>

#### 2.1.3 流式对话 - 多模态图片识别 4v（vision）

<details><summary><a>👉查看代码</a></summary></br>

```Java
@Test
public void testCompletions4V() throws Exception
{
    CountDownLatch countDownLatch = new CountDownLatch(1);
    
    // 入参；模型、请求信息
    ChatCompletionRequest request = new ChatCompletionRequest();

    // GLM_3_5_TURBO、GLM_4
    request.setModel(Model.GLM_4V);
    request.setStream(true);
    request.setMessages(new ArrayList<ChatCompletionRequest.Prompt>()
    {
        private static final long serialVersionUID = -7988151926241837899L;
        {
            // content 字符串格式
            add(ChatCompletionRequest.Prompt.builder()
                    .role(Role.user.getCode())
                    .content("这个图片写了什么？")
                    .build());
           
            // content 对象格式
            add(ChatCompletionRequest.Prompt.builder()
                    .role(Role.user.getCode())
                    .content(ChatCompletionRequest.Prompt.Content.builder()
                            .type(ChatCompletionRequest.Prompt.Content.Type.text.getCode())
                            .text("这是什么图片？")
                            .build())
                    .build());
          
            // content 对象格式，上传图片；图片支持 url、basde64
            add(ChatCompletionRequest.Prompt.builder()
                    .role(Role.user.getCode())
                    .content(ChatCompletionRequest.Prompt.Content.builder()
                            .type(ChatCompletionRequest.Prompt.Content.Type.image_url.getCode())
                            .imageUrl(ChatCompletionRequest.Prompt.Content.ImageUrl.builder().url("https://bugstack.cn/images/article/project/chatgpt/chatgpt-extra-231011-01.png").build())
                            .build())
                    .build());
        }
    });
    
    openAiSession.completions(request, new EventSourceListener()
    {
        @Override
        public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data)
        {
            if ("[DONE]".equals(data))
            {
                log.info("[输出结束] Tokens {}", JSON.toJSONString(data));
              
                return;
            }
            
            ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
            log.info("测试结果：{}", JSON.toJSONString(response));
        }
        
        @Override
        public void onClosed(EventSource eventSource)
        {
            log.info("对话完成");
            countDownLatch.countDown();
        }
        
        @Override
        public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response)
        {
            log.error("对话失败", t);
            countDownLatch.countDown();
        }
    });
    
    // 等待
    countDownLatch.await();
}
```

</details>

#### 2.1.4 同步请求 - future 模式

<details><summary><a>👉查看代码</a></summary></br>

```Java
@Test
public void testCompletionsFuture() throws Exception
{
    // 入参；模型、请求信息
    ChatCompletionRequest request = new ChatCompletionRequest();

    // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
    request.setModel(Model.CHATGLM_TURBO);
    
    request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>() 
    {
        private static final long serialVersionUID = -7988151926241837899L;
        {
            add(ChatCompletionRequest.Prompt.builder()
                    .role(Role.user.getCode())
                    .content("1+1")
                    .build());
        }
    });
    
    CompletableFuture<String> future = openAiSession.completions(request);
    String response = future.get();
    log.info("测试结果：{}", response);
}
```

</details>


#### 2.1.5 同步请求 - 普通模式

<details><summary><a>👉查看代码</a></summary></br>

```Java
@Test
public void testCompletionsSync() throws Exception
{
    // 入参；模型、请求信息
    ChatCompletionRequest request = new ChatCompletionRequest();

    // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
    request.setModel(Model.GLM_4V);
    
    request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>()
    {
        private static final long serialVersionUID = -7988151926241837899L;
        {
            add(ChatCompletionRequest.Prompt.builder()
                    .role(Role.user.getCode())
                    .content("末影小黑xh是谁？")
                    .build());
        }
    });
    
    // 2024 年 1 月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
    request.setTools(new ArrayList<ChatCompletionRequest.Tool>()
    {
        private static final long serialVersionUID = -7988151926241837899L;
        {
            add(ChatCompletionRequest.Tool.builder()
                    .type(ChatCompletionRequest.Tool.Type.web_search)
                    .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("末影小黑xh").build())
                    .build());
        }
    });
    
    ChatCompletionSyncResponse response = openAiSession.completionsSync(request);
    log.info("测试结果：{}", JSON.toJSONString(response));
}
```

</details>

#### 2.1.6 文生图

<details><summary><a>👉查看代码</a></summary></br>

```Java
@Test
public void testGenImages() throws Exception
{
    ImageCompletionRequest request = new ImageCompletionRequest();
    request.setModel(Model.COGVIEW_3);
    request.setPrompt("画一条小狗");
    ImageCompletionResponse response = openAiSession.genImages(request);
    log.info("测试结果：{}", JSON.toJSONString(response));
}
```

</details>


### 2.2 脚本测试

```Java
@Test
public void testCurl()
{
    // 1. 配置文件
    Configuration configuration = new Configuration();
    configuration.setApiHost("https://open.bigmodel.cn/");
    configuration.setApiSecretKey("2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMwuLVfL");
   
    // 2. 获取Token
    String token = BearerTokenUtils.getToken(configuration.getApiKey(), configuration.getApiSecret());
    log.info("1. 在智谱 Ai 官网，申请 ApiSeretKey 配置到此测试类中，替换 setApiSecretKey 值。 https://open.bigmodel.cn/usercenter/apikeys");
    log.info("2. 运行 testCurl 获取 token：{}", token);
    log.info("3. 将获得的 token 值，复制到 curl.sh 中，填写到 Authorization: Bearer 后面");
    log.info("4. 执行完步骤 3 以后，可以复制直接运行 curl.sh 文件，或者复制 curl.sh 文件内容到控制台/终端/ApiPost 中运行");
}
```

```Shell
curl -X POST \
        -H "Authorization: Bearer <把获得的 Token 填写这，并去掉两个尖括号>" \
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
        "content": "写个 java 冒泡排序"
        }
        ]
        }' \
  http://open.bigmodel.cn/api/paas/v3/model-api/chatglm_lite/sse-invoke
```

- 运行后你会获得一个 Token 信息，之后在 curl.sh 中替换  Authorization: Bearer 后面的值。就可以执行测试了。

- [curl.sh](https://github.com/MYXHcode/chatglm-sdk-java/blob/master/docs/curl/curl.sh) | [curl-cogview-3.sh](https://github.com/MYXHcode/chatglm-sdk-java/blob/master/docs/curl/curl-cogview-3.sh) | [curl-glm-3-turbo.sh](https://github.com/MYXHcode/chatglm-sdk-java/blob/master/docs/curl/curl-glm-3-turbo.sh) | [curl-glm-4.sh](https://github.com/MYXHcode/chatglm-sdk-java/blob/master/docs/curl/curl-glm-4.sh) | [curl-glm-4v.sh](https://github.com/MYXHcode/chatglm-sdk-java/blob/master/docs/curl/curl-glm-4v.sh)

## 3. 程序接入

SpringBoot 配置类

```Java
@Configuration
@EnableConfigurationProperties(ChatGLMSDKConfigProperties.class)
public class ChatGLMSDKConfig
{

    @Bean
    @ConditionalOnProperty(value = "chatglm.sdk.config.enabled", havingValue = "true", matchIfMissing = false)
    public OpenAiSession openAiSession(ChatGLMSDKConfigProperties properties)
    {
        // 1. 配置文件
        cn.bugstack.chatglm.session.Configuration configuration = new cn.bugstack.chatglm.session.Configuration();
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
    状态：open = 开启、close 关闭 
     */
    private boolean enable;
    
    /**
    转发地址
     */
    private String apiHost;
    
    /**
    可以申请 apiSecretKey
     */
    private String apiSecretKey;
}
```

```Java
@Autowired(required = false)
private OpenAiSession openAiSession;
```

- 注意：如果你在服务中配置了关闭启动 ChatGLM SDK 那么注入 openAiSession 为 null

yml 配置

```pom
# ChatGLM SDK Config
chatglm:
  sdk:
    config:
      # 状态：true = 开启、false 关闭
      enabled: false
     
      # 官网地址 
      api-host: https://open.bigmodel.cn/
      
      # 官网申请 https://open.bigmodel.cn/usercenter/apikeys
      api-secret-key: 2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMwuLVfL
```

---
