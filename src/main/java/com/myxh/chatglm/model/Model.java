package com.myxh.chatglm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 会话模型
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Getter
@AllArgsConstructor
public enum Model
{
    @Deprecated
    CHATGLM_6B_SSE("chatGLM_6b_SSE", "ChatGLM-6B 测试模型"),

    @Deprecated
    CHATGLM_LITE("chatglm_lite", "轻量版模型，适用对推理速度和成本敏感的场景"),

    @Deprecated
    CHATGLM_LITE_32K("chatglm_lite_32k", "标准版模型，适用兼顾效果和成本的场景"),

    @Deprecated
    CHATGLM_STD("chatglm_std", "适用于对知识量、推理能力、创造力要求较高的场景"),

    @Deprecated
    CHATGLM_PRO("chatglm_pro", "适用于对知识量、推理能力、创造力要求较高的场景"),

    /**
     * 智谱 AI 2023 年 6 月发布
     */
    CHATGLM_TURBO("chatglm_turbo", "适用于对知识量、推理能力、创造力要求较高的场景"),

    /**
     * 智谱 AI 2024 年 1 月发布
     */
    GLM_3_5_TURBO("glm-3-turbo", "适用于对知识量、推理能力、创造力要求较高的场景"),

    GLM_4("glm-4", "适用于复杂的对话交互和深度内容创作设计的场景"),
    GLM_4V("glm-4v", "根据输入的自然语言指令和图像信息完成任务，推荐使用 SSE 或同步调用方式请求接口"),
    COGVIEW_3("cogview-3", "根据用户的文字描述生成图像,使用同步调用方式请求接口"),
    ;

    private final String code;
    private final String info;
}
