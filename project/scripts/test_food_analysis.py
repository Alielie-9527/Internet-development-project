#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
食物图片分析测试脚本
运行：python scripts/test_food_analysis.py
依赖：requests（见 scripts/requirements.txt）
"""

import requests
import sys
import base64
import os

BASE_URL = "http://localhost:8080"


def encode_image_to_base64(image_path):
    """
    将图片文件编码为base64字符串
    """
    with open(image_path, "rb") as image_file:
        return base64.b64encode(image_file.read()).decode('utf-8')


def test_food_analysis_with_file(image_path):
    """
    使用实际图片文件测试食物分析
    """
    print(f"正在读取图片: {image_path}")
    
    if not os.path.exists(image_path):
        print(f"❌ 图片文件不存在: {image_path}")
        return False
    
    try:
        base64_image = encode_image_to_base64(image_path)
        print(f"图片编码成功，长度: {len(base64_image)}")
    except Exception as e:
        print(f"❌ 图片编码失败: {e}")
        return False
    
    return test_food_analysis(base64_image)


def test_food_analysis(base64_image):
    """
    测试食物分析接口
    """
    print("\n========== 测试食物图片分析 ==========")
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/ai/analyze-food",
            json={
                "base64Image": base64_image,
                "userId": 1
            },
            timeout=60,
        )
        
        print(f"响应状态码: {response.status_code}")
        
        try:
            data = response.json()
            print(f"响应数据: {data}")
        except Exception as e:
            print(f"❌ 响应非JSON格式: {response.text}")
            return False
        
        if response.status_code != 200:
            print(f"❌ HTTP状态码错误: {response.status_code}")
            return False
        
        if data.get("code") != 0:
            print(f"❌ 业务状态码错误: {data.get('code')}, 消息: {data.get('message')}")
            return False
        
        result = data.get("data")
        if not result:
            print("❌ 响应中没有data字段")
            return False
        
        if not result.get("success"):
            print(f"❌ 分析失败: {result.get('errorMessage')}")
            return False
        
        food_info = result.get("food")
        if not food_info:
            print("❌ 响应中没有food信息")
            return False
        
        print("\n✅ 食物分析成功！")
        print(f"  食物名称: {food_info.get('name')}")
        print(f"  营养成分: {food_info.get('nutrition')}")
        print(f"  卡路里: {food_info.get('calories')}")
        print(f"  健康建议: {food_info.get('advice')}")
        
        return True
        
    except requests.exceptions.Timeout:
        print("❌ 请求超时")
        return False
    except Exception as e:
        print(f"❌ 请求异常: {e}")
        return False


def test_with_mock_data():
    """
    使用模拟的base64数据测试（一个1x1像素的透明PNG图片）
    """
    print("\n========== 使用模拟数据测试 ==========")
    # 这是一个1x1像素的透明PNG图片的base64编码
    mock_base64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
    return test_food_analysis(mock_base64)


def main():
    """
    主测试函数
    """
    print("========== 食物图片分析测试 ==========\n")
    
    # 检查API健康状态
    try:
        health_response = requests.get(f"{BASE_URL}/api/ai/test/health", timeout=10)
        health_data = health_response.json()
        if health_response.status_code != 200 or health_data.get("code") != 0:
            print(f"⚠️  API健康检查失败: {health_data}")
            print("提示：请确保后端服务已启动\n")
            return 1
        print("✅ API健康检查通过\n")
    except Exception as e:
        print(f"❌ 无法连接到后端服务: {e}")
        print(f"提示：请确保后端服务运行在 {BASE_URL}\n")
        return 1
    
    # 如果提供了图片路径参数，使用实际图片测试
    if len(sys.argv) > 1:
        image_path = sys.argv[1]
        success = test_food_analysis_with_file(image_path)
    else:
        # 否则使用模拟数据测试
        print("未提供图片路径，使用模拟数据测试")
        print("用法: python scripts/test_food_analysis.py <图片路径>")
        success = test_with_mock_data()
    
    if success:
        print("\n✅ 所有测试通过")
        return 0
    else:
        print("\n❌ 测试失败")
        return 1


if __name__ == "__main__":
    sys.exit(main())
