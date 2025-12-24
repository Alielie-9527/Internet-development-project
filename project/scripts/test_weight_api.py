#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试体重记录API
"""
import requests
import json
from datetime import datetime

BASE_URL = "http://localhost:8088"
USERNAME = "testuser"
PASSWORD = "123456"

def test_weight_apis():
    """测试体重相关API"""
    print("=" * 60)
    print("测试体重记录API")
    print("=" * 60)
    
    # 1. 登录
    print("\n1. 登录...")
    login_resp = requests.post(
        f"{BASE_URL}/api/user/login",
        json={"username": USERNAME, "password": PASSWORD}
    )
    
    if login_resp.status_code != 200:
        print(f"   ✗ 登录失败: {login_resp.text}")
        return
    
    login_data = login_resp.json()
    if login_data.get('code') != 200:
        print(f"   ✗ 登录失败: {login_data.get('message')}")
        return
    
    token = login_data.get('data', {}).get('token')
    print(f"   ✓ 登录成功")
    
    headers = {"Authorization": f"Bearer {token}"}
    
    # 2. 获取最新体重记录
    print("\n2. 获取最新体重记录...")
    latest_resp = requests.get(f"{BASE_URL}/api/weight/latest", headers=headers)
    print(f"   状态码: {latest_resp.status_code}")
    print(f"   响应: {latest_resp.text[:500]}")
    
    if latest_resp.status_code == 200:
        latest_data = latest_resp.json()
        if latest_data.get('code') == 200:
            weight_data = latest_data.get('data')
            if weight_data:
                print(f"   ✓ 最新体重: {weight_data.get('weight')} kg")
                print(f"   记录时间: {weight_data.get('recordTime')}")
            else:
                print("   ⚠ 没有体重记录")
        else:
            print(f"   ✗ 获取失败: {latest_data.get('message')}")
    
    # 3. 添加新的体重记录
    print("\n3. 添加新的体重记录...")
    new_weight = 70.5
    add_resp = requests.post(
        f"{BASE_URL}/api/weight/add",
        json={
            "weight": new_weight,
            "bodyFat": 20.5,
            "recordTime": datetime.now().isoformat()
        },
        headers=headers
    )
    print(f"   状态码: {add_resp.status_code}")
    print(f"   响应: {add_resp.text[:500]}")
    
    if add_resp.status_code == 200:
        add_data = add_resp.json()
        if add_data.get('code') == 200:
            print(f"   ✓ 添加成功, ID: {add_data.get('data')}")
        else:
            print(f"   ✗ 添加失败: {add_data.get('message')}")
    
    # 4. 再次获取最新体重记录
    print("\n4. 再次获取最新体重记录...")
    latest_resp2 = requests.get(f"{BASE_URL}/api/weight/latest", headers=headers)
    
    if latest_resp2.status_code == 200:
        latest_data2 = latest_resp2.json()
        if latest_data2.get('code') == 200:
            weight_data2 = latest_data2.get('data')
            if weight_data2:
                print(f"   ✓ 最新体重: {weight_data2.get('weight')} kg")
                if weight_data2.get('weight') == new_weight:
                    print("   ✓ 体重已更新为新值")
                else:
                    print(f"   ⚠ 体重未更新 (期望: {new_weight}, 实际: {weight_data2.get('weight')})")
    
    # 5. 获取体重记录列表
    print("\n5. 获取体重记录列表...")
    list_resp = requests.post(
        f"{BASE_URL}/api/weight/list",
        json={"pageNum": 1, "pageSize": 5},
        headers=headers
    )
    
    if list_resp.status_code == 200:
        list_data = list_resp.json()
        if list_data.get('code') == 200:
            records = list_data.get('data', [])
            print(f"   ✓ 获取成功, 共 {len(records)} 条记录")
            for i, record in enumerate(records[:3], 1):
                print(f"   {i}. {record.get('weight')} kg - {record.get('recordTime')}")
    
    print("\n" + "=" * 60)
    print("测试完成")
    print("=" * 60)

if __name__ == '__main__':
    test_weight_apis()
