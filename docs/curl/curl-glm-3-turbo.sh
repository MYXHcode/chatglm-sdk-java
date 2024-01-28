# Linux 命令：
curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiMmFiYmJjNDFiMTY0YzJmMjE3NDBlODI1ODJlZDQ0YjUiLCJleHAiOjE3MDY0NDY1MDU4OTQsInRpbWVzdGFtcCI6MTcwNjQ0NDcwNTg5NH0.OZ1SzP8yPwT3w2zfQRIBopETexCw_fBNF6Q5bOY6lZM" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -d '{
          "model":"glm-3-turbo",
          "stream": "true",
          "messages": [
              {
                  "role": "user",
                  "content": "写个 java 冒泡排序"
              }
          ]
        }' \
  https://open.bigmodel.cn/api/paas/v4/chat/completions

# Window 命令：
$Headers = @{
    "Authorization" = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiMmFiYmJjNDFiMTY0YzJmMjE3NDBlODI1ODJlZDQ0YjUiLCJleHAiOjE3MDY0NDY1MDU4OTQsInRpbWVzdGFtcCI6MTcwNjQ0NDcwNTg5NH0.OZ1SzP8yPwT3w2zfQRIBopETexCw_fBNF6Q5bOY6lZM"
    "Content-Type" = "application/json"
    "User-Agent" = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"
}

$Body = @{
    "model" = "glm-3-turbo"
    "stream" = $true
    "messages" = @(
        @{
            "role" = "user"
            "content" = "写个 java 冒泡排序"
        }
    )
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "https://open.bigmodel.cn/api/paas/v4/chat/completions" -Headers $Headers -Body $Body
