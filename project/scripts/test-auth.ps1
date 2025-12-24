# 测试登录注册功能的 PowerShell 脚本

Write-Host "========== 测试登录注册功能 ==========" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080"

# 测试后端健康检查
Write-Host "1. 检查后端服务..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/ai/test/health" -Method Get -TimeoutSec 5
    Write-Host "✓ 后端服务正常运行" -ForegroundColor Green
} catch {
    Write-Host "✗ 后端服务未启动" -ForegroundColor Red
    Write-Host "请先启动后端服务: cd api-backend; mvn spring-boot:run" -ForegroundColor Yellow
    exit
}

Write-Host ""

# 测试登录
Write-Host "2. 测试登录功能..." -ForegroundColor Yellow
$loginData = @{
    username = "testuser"
    password = "admin123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/user/login" -Method Post -Body $loginData -ContentType "application/json"
    Write-Host "✓ 登录成功" -ForegroundColor Green
    Write-Host "响应数据:" -ForegroundColor Gray
    $response | ConvertTo-Json -Depth 3
    
    if ($response.data.token) {
        Write-Host "✓ Token获取成功: $($response.data.token.Substring(0, [Math]::Min(30, $response.data.token.Length)))..." -ForegroundColor Green
    } else {
        Write-Host "⚠ 未获取到token" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ 登录失败" -ForegroundColor Red
    Write-Host "错误信息: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 测试注册
Write-Host "3. 测试注册功能..." -ForegroundColor Yellow
$timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$registerData = @{
    username = "testuser$timestamp"
    password = "123456"
    nickname = "测试用户$timestamp"
    email = "test$timestamp@example.com"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/user/register" -Method Post -Body $registerData -ContentType "application/json"
    Write-Host "✓ 注册成功" -ForegroundColor Green
    Write-Host "响应数据:" -ForegroundColor Gray
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "✗ 注册失败" -ForegroundColor Red
    Write-Host "错误信息: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "响应内容: $responseBody" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========== 测试完成 ==========" -ForegroundColor Cyan
