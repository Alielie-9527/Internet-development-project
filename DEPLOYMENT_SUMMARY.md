# ✨ 容器化部署方案 - 部署完成

> 您的项目已配置为完全容器化部署，无需本地开发环境，只需 Docker。

## 📦 已创建的文件

### 核心部署文件

| 文件 | 说明 |
|------|------|
| `deploy.sh` | 🚀 部署脚本（一键启动所有服务） |
| `docker-compose.yml` | 🐳 Docker Compose 配置（定义所有服务） |
| `.env.example` | ⚙️ 环境变量模板 |
| `.env` | ⚙️ 环境变量（自动生成） |

### 镜像文件

| 文件 | 说明 |
|------|------|
| `project/api-backend/Dockerfile` | 后端 Java 镜像（多阶段构建） |
| `project/api-backend/.dockerignore` | 后端构建优化 |
| `project/h5-mobile/Dockerfile` | 前端 Vue 镜像（多阶段构建） |
| `project/h5-mobile/nginx.conf` | Nginx 反向代理配置 |
| `project/h5-mobile/.dockerignore` | 前端构建优化 |

### 文档文件

| 文件 | 说明 |
|------|------|
| `DEPLOY.md` | 📚 详细部署文档 |
| `QUICKSTART.md` | 🚀 快速开始指南 |
| `DEPLOYMENT_SUMMARY.md` | 📝 本文件 |

## 🎯 快速启动（3 个命令）

```bash
# 1️⃣ 进入项目目录
cd Internet-development-project

# 2️⃣ 创建环境变量文件（如果不存在）
cp .env.example .env

# 3️⃣ 启动所有服务（一键部署）
./deploy.sh
```

**预计时间**: 首次运行 10-20 分钟（取决于网络和硬件）

## 🌐 访问地址

部署完成后，访问以下地址：

```
📱 前端应用:    http://localhost
📚 API 文档:    http://localhost:8080/doc.html
🔌 后端 API:    http://localhost:8080
🗄️ 数据库管理:  http://localhost:8081 (PhpMyAdmin)
```

## 📋 部署脚本命令

```bash
./deploy.sh              # 启动所有服务（默认）
./deploy.sh logs         # 查看实时日志
./deploy.sh ps           # 显示容器状态
./deploy.sh stop         # 停止服务
./deploy.sh down         # 停止并删除容器
./deploy.sh clean        # 完全清理（谨慎使用）
./deploy.sh rebuild      # 重新构建镜像
./deploy.sh help         # 显示帮助信息
```

## 🏗️ 架构设计

### 容器服务

```
┌─────────────────────────────────────────────┐
│        Docker Compose Network               │
├─────────────────────────────────────────────┤
│                                             │
│  ┌────────────────────┐  ┌───────────────┐ │
│  │ Frontend (Nginx)   │  │ Backend (JRE) │ │
│  │ Vue + Vite        │  │ Spring Boot   │ │
│  │ Port: 80          │  │ Port: 8080    │ │
│  └────────────────────┘  └───────────────┘ │
│          │                      │           │
│   ┌──────┴──────┬────────────────┘           │
│   │             │                           │
│  ┌▼───────┐  ┌──▼──────┐  ┌──────────────┐ │
│  │ MySQL  │  │ Redis   │  │ PhpMyAdmin   │ │
│  │ 3306   │  │ 6379    │  │ Port: 8081   │ │
│  └────────┘  └─────────┘  └──────────────┘ │
│                                             │
└─────────────────────────────────────────────┘
```

### 多阶段构建优势

#### 后端
- **阶段 1**: 在 Maven 容器中编译 → 生成 JAR 包
- **阶段 2**: 在轻量级 JRE 容器中运行 → 镜像仅 400MB

#### 前端
- **阶段 1**: 在 Node 容器中构建 → 编译 Vue 项目
- **阶段 2**: 在 Nginx 容器中运行 → 提供静态文件和反向代理

## ⚙️ 配置说明

### .env 文件配置

```env
# 数据库
DB_ROOT_PASSWORD=root              # MySQL root 密码
DB_NAME=nutrition_db               # 数据库名
DB_USER=nutrition_user             # 数据库用户
DB_PASSWORD=nutrition123           # 数据库密码
DB_PORT=3306                       # MySQL 端口

# Redis
REDIS_PASSWORD=redis123            # Redis 密码
REDIS_PORT=6379                    # Redis 端口

# 后端
BACKEND_PORT=8080                  # 后端端口
JAVA_OPTS=-Xmx512m -Xms256m       # Java 内存（可调整）

# 前端
FRONTEND_PORT=80                   # 前端端口

# 工具
PHPMYADMIN_PORT=8081               # PhpMyAdmin 端口
```

### 修改配置的方法

```bash
# 1. 编辑 .env 文件
vim .env

# 2. 停止服务
./deploy.sh stop

# 3. 重新启动
./deploy.sh
```

## 📊 镜像和容器

### 构建的镜像

```bash
# 查看所有镜像
docker images | grep health-agent

# 结果示例：
# REPOSITORY                           TAG       SIZE
# internet-development-project-backend  latest    ~400MB
# internet-development-project-frontend latest    ~150MB
```

### 运行的容器

```bash
# 查看容器状态
docker-compose ps

# 或使用部署脚本
./deploy.sh ps
```

## 🔄 更新和维护

### 更新代码后重新部署

```bash
# 方式 1: 完整重建
./deploy.sh rebuild

# 方式 2: 手动操作
docker-compose down
docker-compose up -d --build
```

### 查看日志

```bash
# 查看所有服务日志
./deploy.sh logs

# 查看特定服务日志
docker-compose logs backend
docker-compose logs frontend

# 持续跟踪（实时）
docker-compose logs -f

# 查看最后 100 行
docker-compose logs --tail=100
```

### 进入容器进行调试

```bash
# 进入后端容器
docker exec -it health-agent-backend bash

# 进入前端容器
docker exec -it health-agent-frontend sh

# 进入 MySQL 容器
docker exec -it health-agent-mysql mysql -uroot -proot
```

## 🔒 安全建议

### 生产环境检查清单

- [ ] **修改默认密码**: 编辑 `.env` 中的所有密码
- [ ] **禁用 PhpMyAdmin**: 编辑 `docker-compose.yml` 注释 phpmyadmin 服务
- [ ] **配置 HTTPS**: 使用 Nginx + Let's Encrypt
- [ ] **配置防火墙**: 只开放必要的端口
- [ ] **备份策略**: 定期备份数据库和文件
- [ ] **监控告警**: 配置日志监控和告警
- [ ] **访问控制**: 使用 Nginx 认证或反向代理

### 备份数据库

```bash
# 备份 MySQL
docker exec health-agent-mysql mysqldump -uroot -proot nutrition_db > backup.sql

# 恢复 MySQL
docker exec -i health-agent-mysql mysql -uroot -proot nutrition_db < backup.sql

# 备份 Redis
docker cp health-agent-redis:/data/dump.rdb ./redis-backup.rdb
```

## 🚀 部署到生产环境

### 最小化部署脚本

创建 `deploy-prod.sh`：

```bash
#!/bin/bash
set -e

# 停止旧服务
docker-compose down

# 拉取最新代码
git pull origin main

# 构建并启动
docker-compose up -d --build

# 检查健康状态
sleep 30
docker-compose ps
```

### 自动启动（systemd）

创建 `/etc/systemd/system/health-agent.service`：

```ini
[Unit]
Description=Health Agent Service
After=docker.service
Requires=docker.service

[Service]
Type=simple
WorkingDirectory=/path/to/project
ExecStart=/bin/bash -c './deploy.sh'
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启用自启动：

```bash
sudo systemctl enable health-agent
sudo systemctl start health-agent
sudo systemctl status health-agent
```

### 使用 Docker Swarm 或 Kubernetes（高级）

对于大规模部署，可以将容器镜像推送到镜像仓库，然后使用编排平台。

## 🐛 常见问题

### Q: 镜像构建失败
**A**: 检查网络连接和磁盘空间，查看日志：
```bash
docker-compose logs backend
docker-compose logs frontend
```

### Q: 容器启动后立即停止
**A**: 查看容器日志：
```bash
docker logs health-agent-backend
docker logs health-agent-frontend
```

### Q: 如何修改端口
**A**: 编辑 `.env` 文件，修改 `BACKEND_PORT`、`FRONTEND_PORT` 等，然后重启：
```bash
./deploy.sh stop
./deploy.sh
```

### Q: 数据库无法连接
**A**: 检查 MySQL 容器状态：
```bash
docker-compose logs mysql
docker-compose restart mysql
```

### Q: 前端无法访问后端 API
**A**: 检查 Nginx 配置和网络连接：
```bash
docker exec health-agent-frontend curl http://backend:8080/health
```

## 📚 相关文档

- [详细部署文档](./DEPLOY.md)
- [快速开始指南](./QUICKSTART.md)
- [项目 README](./README.md)

## 💡 提示

1. **首次部署较慢**: 需要下载基础镜像和构建项目镜像，预计 10-20 分钟
2. **磁盘空间**: 镜像总大小约 1.1GB，确保有足够空间
3. **内存需求**: 建议至少 2GB 可用内存，4GB+ 更佳
4. **网络要求**: Docker Hub 访问，若网络较慢建议配置镜像加速源

## 🎉 完成！

您现在拥有一套**完全容器化**的部署方案，可以：

- ✅ 在任何有 Docker 的机器上一键部署
- ✅ 无需本地安装 Java、Node、MySQL 等开发环境
- ✅ 轻松管理服务的启停和更新
- ✅ 方便地扩展和定制配置

**开始部署**:

```bash
cd Internet-development-project
./deploy.sh
```

祝您使用愉快! 🚀
