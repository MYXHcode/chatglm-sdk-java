package com.myxh.chatglm.test;

import com.alibaba.fastjson.JSON;
import com.myxh.chatglm.model.ChatCompletionResponse;
import com.myxh.chatglm.model.ImageCompletionResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author MYXH
 * @date 2024/1/28
 * @description JSON 测试
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Slf4j
public class JSONTest
{
    @Test
    public void testGlmJson()
    {
        String json01 = "{\n" +
                "    \"id\": \"8305987191663349153\",\n" +
                "    \"created\": 1705487423,\n" +
                "    \"model\": \"glm-3-turbo\",\n" +
                "    \"choices\": [\n" +
                "        {\n" +
                "            \"index\": 0,\n" +
                "            \"delta\": {\n" +
                "                \"role\": \"assistant\",\n" +
                "                \"content\": \"1\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        String json02 = "{\n" +
                "    \"id\": \"8308763664682731117\",\n" +
                "    \"created\": 1705490859,\n" +
                "    \"model\": \"glm-3-turbo\",\n" +
                "    \"choices\": [\n" +
                "        {\n" +
                "            \"index\": 0,\n" +
                "            \"finish_reason\": \"stop\",\n" +
                "            \"delta\": {\n" +
                "                \"role\": \"assistant\",\n" +
                "                \"content\": \"\"\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"usage\": {\n" +
                "        \"prompt_tokens\": 8,\n" +
                "        \"completion_tokens\": 12,\n" +
                "        \"total_tokens\": 20\n" +
                "    }\n" +
                "}";

        ChatCompletionResponse response = JSON.parseObject(json01, ChatCompletionResponse.class);
        log.info("测试结果：{}", JSON.toJSONString(response.getChoices()));
    }

    @Test
    public void testImageJson()
    {
        String json = "{\n" +
                "    \"created\": 1705549253,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"url\": \"https://sfile.chatglm.cn/testpath/cbffcbf4-ac63-50a3-9d1e-b644c77ffaa2_0.png\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ImageCompletionResponse response = JSON.parseObject(json, ImageCompletionResponse.class);
        log.info("测试结果：{}", response.getData().get(0).getUrl());
    }
}
