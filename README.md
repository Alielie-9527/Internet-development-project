# 饮食健康管理智能体系统 (Diet Health Agent)

本项目是一个基于 AI 驱动的饮食与健康管理智能体系统，旨在通过人工智能技术为用户提供个性化的饮食建议、食物营养分析、体重追踪及健康评估。

## 🚀 项目简介

该系统集成了大语言模型（LLM）能力，构建了一个能够理解用户需求并提供专业健康指导的智能助手。用户可以通过移动端 H5 界面轻松记录饮食、查询食物营养、进行健康测评，并与 AI 智能体进行实时对话。

### 核心功能
- **AI 智能对话**：基于通义千问（Qwen）大模型的健康咨询与饮食建议。
- **食物营养分析**：分析食物成分，提供热量及营养素参考。
- **饮食与体重管理**：记录每日饮食摄入与体重变化，可视化健康趋势。
- **健康测评**：多维度的健康评估问卷与 AI 深度分析报告。
- **知识库管理**：集成专业健康知识，提升 AI 回答的准确性。

## 🛠 技术栈

### 前端 (Mobile H5)
- **框架**: Vue 3 (Composition API)
- **状态管理**: Pinia
- **UI 组件库**: Vant UI
- **构建工具**: Vite
- **适配方案**: PostCSS (amfe-flexible + pxtorem)
- **网络请求**: Axios

### 后端 (API Backend)
- **核心框架**: Spring Boot 3.2.1
- **开发语言**: Java 17
- **持久层**: MyBatis
- **安全认证**: Spring Security + JWT (JSON Web Token)
- **API 文档**: Knife4j (Swagger)
- **AI 集成**: Dashscope SDK (阿里云通义千问)
- **实时通信**: WebSocket

### 数据库与中间件
- **数据库**: MySQL 8.0 (持久化存储)
- **缓存**: Redis 7 (会话管理与数据缓存)
- **管理工具**: PhpMyAdmin (数据库可视化管理)

### 运维与测试
- **容器化**: Docker & Docker Compose (一键部署)
- **自动化测试**: Python (基于 Requests 的 API 自动化测试脚本)
- **环境管理**: Dotenv (.env) 统一配置管理

## 📂 项目结构

```text
Internet-development-project/
├── project/
│   ├── api-backend/     # Java 后端源码
│   ├── h5-mobile/       # Vue 3 前端源码
│   └── database/        # 数据库初始化脚本与配置
├── scripts/             # Python 自动化测试与 AI 服务脚本
├── docker-compose.yml   # Docker 一键启动配置
└── .env.example         # 环境变量模板
```
