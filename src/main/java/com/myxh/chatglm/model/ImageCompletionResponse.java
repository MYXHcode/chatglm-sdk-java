package com.myxh.chatglm.model;

import lombok.Data;

import java.util.List;

/**
 * @author MYXH
 * @date 2024/1/28
 * @description CogView <a href="https://open.bigmodel.cn/dev/api#cogview">根据用户的文字描述生成图像，使用同步调用方式请求接口</a>
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Data
public class ImageCompletionResponse
{
    /**
     * 请求创建时间，是以秒为单位的 Unix 时间戳
     */
    private Long created;

    private List<Image> data;

    @Data
    public static class Image
    {
        private String url;
    }
}
