package com.myxh.chatglm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 角色
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Getter
@AllArgsConstructor
public enum Role
{
    /**
     * user 用户输入的内容，role 位 user
     */
    user("user"),

    /**
     * 模型生成的内容，role 位 assistant
     */
    assistant("assistant"),

    /**
     * 系统
     */
    system("system"),
    ;

    private final String code;
}
