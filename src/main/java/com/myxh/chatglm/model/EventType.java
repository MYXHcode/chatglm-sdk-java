package com.myxh.chatglm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author MYXH
 * @date 2023/12/12
 * @description 消息类型 <a href="https://open.bigmodel.cn/dev/api#chatglm_lite">chatglm_lite</a>
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Getter
@AllArgsConstructor
public enum EventType
{
    add("add", "增量"),
    finish("finish", "结束"),
    error("error", "错误"),
    interrupted("interrupted", "中断"),
    ;

    private final String code;
    private final String info;
}
