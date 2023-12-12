# Linux 命令：
curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiMmFiYmJjNDFiMTY0YzJmMjE3NDBlODI1ODJlZDQ0YjUiLCJleHAiOjE3MDIzODk4NTAwMTMsInRpbWVzdGFtcCI6MTcwMjM4ODA1MDAxM30.g97rGe3Kt6RCH-bzFsO_c7015X-ci7inZZQOCwk5Ixs" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -H "Accept: text/event-stream" \
        -d '{
        "top_p": 0.7,
        "sseFormat": "data",
        "temperature": 0.9,
        "incremental": true,
        "request_id": "myxh-1696992276607",
        "prompt": [
        {
        "role": "user",
        "content": "写个java冒泡排序"
        }
        ]
        }' \
  http://open.bigmodel.cn/api/paas/v3/model-api/chatglm_lite/sse-invoke

# Window 命令：
$Headers = @{
    "Authorization" = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiMmFiYmJjNDFiMTY0YzJmMjE3NDBlODI1ODJlZDQ0YjUiLCJleHAiOjE3MDIzODk4NTAwMTMsInRpbWVzdGFtcCI6MTcwMjM4ODA1MDAxM30.g97rGe3Kt6RCH-bzFsO_c7015X-ci7inZZQOCwk5Ixs"
    "Content-Type" = "application/json"
    "User-Agent" = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"
    "Accept" = "text/event-stream"
}

$Body = @{
    "top_p" = 0.7
    "sseFormat" = "data"
    "temperature" = 0.9
    "incremental" = $true
    "request_id" = "myxh-1696992276607"
    "prompt" = @(
        @{
            "role" = "user"
            "content" = "写个java冒泡排序"
        }
    )
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://open.bigmodel.cn/api/paas/v3/model-api/chatglm_lite/sse-invoke" -Headers $Headers -Body $Body
