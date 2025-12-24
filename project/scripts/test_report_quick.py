#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
快速测试报告API
"""
import requests
import json
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8088"
USERNAME = "testuser"
PASSWORD = "123456"

def test_report_apis():
    """测试报告相关API"""
    print("=" * 60)
    print("测试报告API")
    print("=" * 60)
    
    # 1. 登录
    print("\n1. 登录...")
    login_resp = requests.post(
        f"{BASE_URL}/api/user/login",
        json={"username": USERNAME, "password": PASSWORD}
    )
    print(f"   状态码: {login_resp.status_code}")
    print(f"   响应: {login_resp.text[:200]}")
    
    if login_resp.status_code != 200:
        print("   ✗ 登录失败")
        return
    
    login_data = login_resp.json()
    if login_data.get('code') != 200:
        print(f"   ✗ 登录失败: {login_data.get('message')}")
        return
    
    token = login_data.get('data', {}).get('token')
    print(f"   ✓ 登录成功, token: {token[:30]}...")
    
    headers = {"Authorization": f"Bearer {token}"}
    
    # 2. 生成周报
    print("\n2. 生成周报...")
    end_date = datetime.now()
    start_date = end_date - timedelta(days=6)
    
    gen_resp = requests.post(
        f"{BASE_URL}/api/nutrition/report/generate",
        json={
            "reportPeriod": "WEEK",
            "startDate": start_date.strftime("%Y-%m-%d"),
            "endDate": end_date.strftime("%Y-%m-%d"),
            "useAI": False  # 不使用AI加快测试
        },
        headers=headers
    )
    print(f"   状态码: {gen_resp.status_code}")
    print(f"   响应: {gen_resp.text[:500]}")
    
    if gen_resp.status_code != 200:
        print("   ✗ 生成报告失败")
        return
    
    gen_data = gen_resp.json()
    if gen_data.get('code') != 200:
        print(f"   ✗ 生成报告失败: {gen_data.get('message')}")
        return
    
    report_id = gen_data.get('data')
    print(f"   ✓ 报告生成成功, ID: {report_id}")
    
    # 3. 获取报告列表
    print("\n3. 获取报告列表...")
    list_resp = requests.post(
        f"{BASE_URL}/api/nutrition/report/list",
        json={
            "reportPeriod": "WEEK",
            "pageNum": 1,
            "pageSize": 10
        },
        headers=headers
    )
    print(f"   状态码: {list_resp.status_code}")
    print(f"   响应: {list_resp.text[:500]}")
    
    if list_resp.status_code == 200:
        list_data = list_resp.json()
        if list_data.get('code') == 200:
            reports = list_data.get('data', [])
            print(f"   ✓ 获取成功, 共 {len(reports)} 条报告")
            if reports:
                print(f"   首条报告: ID={reports[0].get('id')}, 日期={reports[0].get('reportDate')}")
        else:
            print(f"   ✗ 获取失败: {list_data.get('message')}")
    else:
        print(f"   ✗ 请求失败")
    
    # 4. 获取报告详情
    if report_id:
        print(f"\n4. 获取报告详情 (ID={report_id})...")
        detail_resp = requests.get(
            f"{BASE_URL}/api/nutrition/report/{report_id}",
            headers=headers
        )
        print(f"   状态码: {detail_resp.status_code}")
        print(f"   响应: {detail_resp.text[:800]}")
        
        if detail_resp.status_code == 200:
            detail_data = detail_resp.json()
            if detail_data.get('code') == 200:
                report = detail_data.get('data', {})
                print(f"   ✓ 获取成功")
                print(f"   - reportPeriod: {report.get('reportPeriod')}")
                print(f"   - overallScore: {report.get('overallScore')}")
                print(f"   - avgCalories: {report.get('avgCalories')}")
                print(f"   - totalDays: {report.get('totalDays')}")
                print(f"   - startDate: {report.get('startDate')}")
                print(f"   - endDate: {report.get('endDate')}")
            else:
                print(f"   ✗ 获取失败: {detail_data.get('message')}")
        else:
            print(f"   ✗ 请求失败")
    
    print("\n" + "=" * 60)
    print("测试完成")
    print("=" * 60)

if __name__ == '__main__':
    test_report_apis()
