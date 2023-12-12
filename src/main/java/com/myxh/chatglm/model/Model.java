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
     * 智谱 AI 最新模型
     */
    CHATGLM_TURBO("chatglm_turbo", "适用于对知识量、推理能力、创造力要求较高的场景"),
    ;

    private final String code;
    private final String info;
}
