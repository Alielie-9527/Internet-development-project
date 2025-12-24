# 🎯 容器化部署方案 - 完整信息

> 您的完整的前后端项目已配置为容器化部署，只需 Docker 即可一键启动整个应用栈。

## 📦 已为您创建的部署文件清单

### 📂 根目录文件
```
Internet-development-project/
├── deploy.sh                    ⭐ 【最重要】一键部署脚本
├── docker-compose.yml           ⭐ 【最重要】容器编排配置
├── .env.example                 环境变量模板
├── .env                         环境变量（自动生成）
├── DEPLOY.md                    详细部署文档（60+ 行）
├── QUICKSTART.md                快速开始指南
└── DEPLOYMENT_SUMMARY.md        部署方案说明
```

### 📂 后端文件 (api-backend/)
```
project/api-backend/
├── Dockerfile                   ⭐ 后端镜像定义（多阶段构建）
├── .dockerignore                构建优化配置
└── pom.xml, src/...            （原始文件无需修改）
```

### 📂 前端文件 (h5-mobile/)
```
project/h5-mobile/
├── Dockerfile                   ⭐ 前端镜像定义（多阶段构建）
├── nginx.conf                   ⭐ Nginx 反向代理配置
├── .dockerignore                构建优化配置
└── package.json, src/...        （原始文件无需修改）
```

## 🚀 一键启动（3 个命令，3 分钟）

### 第一步：进入项目目录
```bash
cd Internet-development-project
```

### 第二步：创建环境配置（首次运行）
```bash
cp .env.example .env
```

### 第三步：启动部署脚本
```bash
./deploy.sh
```

✨ **完成！** 部署脚本会自动：
- ✅ 检查 Docker 环境
- ✅ 拉取基础镜像（eclipse-temurin, node, nginx, mysql, redis）
- ✅ 构建后端 JAR 镜像
- ✅ 构建前端 Nginx 镜像
- ✅ 启动 5 个容器：MySQL、Redis、phpMyAdmin、后端、前端
- ✅ 等待所有服务就绪
- ✅ 显示访问地址

**首次运行时间**: 10-20 分钟（取决于网络速度）

## 🌐 访问地址

部署完成后，在浏览器中访问：

| 服务 | 地址 | 用途 |
|------|------|------|
| 📱 **前端应用** | http://localhost | 移动端 Vue 应用 |
| 📚 **API 文档** | http://localhost:8080/doc.html | 接口文档（Swagger） |
| 🔌 **后端 API** | http://localhost:8080 | RESTful 接口 |
| 🗄️ **数据库管理** | http://localhost:8081 | PhpMyAdmin 管理工具 |

## 📋 常用命令集合

```bash
# 启动服务（首次部署）
./deploy.sh

# 查看实时日志
./deploy.sh logs

# 显示容器运行状态
./deploy.sh ps

# 停止服务（保留数据）
./deploy.sh stop

# 启动已停止的服务
docker-compose up -d

# 停止并删除容器（保留数据）
./deploy.sh down

# 完全清理（删除镜像、容器、数据）⚠️ 谨慎使用
./deploy.sh clean

# 重新构建镜像并启动
./deploy.sh rebuild

# 查看帮助信息
./deploy.sh help
```

## 🏗️ 部署架构

### 容器网络结构
```
┌─────────────────────────────────────────────────┐
│         health-agent-network (Docker)           │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────────┐  ┌──────────────────┐ │
│  │  Frontend (Nginx)   │  │ Backend (JRE)    │ │
│  │  http://localhost   │  │ http://localhost │ │
│  │  :80 (Vite dist)    │  │ :8080            │ │
│  └──────────┬──────────┘  └────────┬─────────┘ │
│             │ (反向代理)            │           │
│             └────────────┬──────────┘           │
│                          │ (JDBC/Redis)        │
│    ┌─────────────────────┼────────────────┐   │
│    │                     │                │   │
│  ┌─▼────────┐  ┌────────▼──┐  ┌────────┐ │   │
│  │  MySQL   │  │   Redis   │  │PhpMyAd-│ │   │
│  │  :3306   │  │  :6379    │  │min     │ │   │
│  │ nutrition│  │(缓存)      │  │ :8081  │ │   │
│  │_db       │  │           │  │        │ │   │
│  └──────────┘  └───────────┘  └────────┘ │   │
│                                            │   │
└────────────────────────────────────────────────┘
```

### 多阶段构建的优势

#### 后端
1. **构建阶段**: Maven 容器中编译 Java → 生成 agent-api-1.0.0.jar
2. **运行阶段**: JRE 轻量容器运行 JAR → 镜像仅 ~400MB

#### 前端
1. **构建阶段**: Node 容器中构建 Vue 项目 → 生成 dist/ 文件
2. **运行阶段**: Nginx 容器提供静态服务和反向代理 → 镜像仅 ~150MB

## ⚙️ 环境变量配置 (.env 文件)

### 查看当前配置
```bash
cat .env
```

### 主要配置项目

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| DB_ROOT_PASSWORD | root | MySQL root 用户密码 |
| DB_NAME | nutrition_db | 数据库名称 |
| DB_USER | nutrition_user | 数据库用户名 |
| DB_PASSWORD | nutrition123 | 数据库用户密码 |
| REDIS_PASSWORD | redis123 | Redis 访问密码 |
| BACKEND_PORT | 8080 | 后端服务端口 |
| FRONTEND_PORT | 80 | 前端服务端口 |
| JAVA_OPTS | -Xmx512m -Xms256m | Java JVM 内存配置 |

### 修改配置的方法

```bash
# 1. 编辑 .env 文件
vim .env

# 2. 停止现有服务
./deploy.sh stop

# 3. 重新启动
./deploy.sh
```

#### 常见调整示例

**增加 Java 内存**（针对大数据处理）
```env
JAVA_OPTS=-Xmx2048m -Xms1024m
```

**修改服务端口**（避免与其他服务冲突）
```env
BACKEND_PORT=9090
FRONTEND_PORT=8000
```

**修改数据库密码**（生产环境必做）
```env
DB_PASSWORD=your_secure_password
REDIS_PASSWORD=your_redis_password
```

## 🐳 Docker 基础知识

### 查看运行的容器
```bash
# 使用部署脚本
./deploy.sh ps

# 或直接使用 docker
docker-compose ps

# 或查看所有容器（包括停止的）
docker ps -a
```

### 查看容器日志
```bash
# 查看所有服务日志（最后 100 行）
docker-compose logs --tail=100

# 实时跟踪所有日志
docker-compose logs -f

# 只看后端日志
docker-compose logs backend

# 只看前端日志
docker-compose logs frontend

# 导出日志到文件
docker-compose logs > all-logs.txt
```

### 进入容器调试
```bash
# 进入后端容器
docker exec -it health-agent-backend sh

# 进入前端容器
docker exec -it health-agent-frontend sh

# 连接 MySQL 数据库
docker exec -it health-agent-mysql mysql -uroot -proot nutrition_db

# 连接 Redis
docker exec -it health-agent-redis redis-cli
```

### 查看镜像和容器大小
```bash
# 查看所有镜像大小
docker images

# 示例输出：
# REPOSITORY              TAG    SIZE
# health-agent-backend    latest ~400MB
# health-agent-frontend   latest ~150MB
# mysql                   8.0    ~500MB
# redis                   7      ~20MB
# nginx                   alpine ~50MB
```

## 💾 数据备份和恢复

### 备份数据库
```bash
# 备份 MySQL 数据库
docker exec health-agent-mysql mysqldump -uroot -proot nutrition_db > backup.sql

# 验证备份文件
ls -lh backup.sql

# 备份 Redis 数据
docker cp health-agent-redis:/data/dump.rdb ./redis-backup.rdb
```

### 恢复数据库
```bash
# 恢复 MySQL 数据库
docker exec -i health-agent-mysql mysql -uroot -proot nutrition_db < backup.sql

# 恢复 Redis 数据
docker cp redis-backup.rdb health-agent-redis:/data/dump.rdb
docker-compose restart redis
```

## 🔒 生产环境安全建议

### 修改所有默认密码
```bash
# 编辑 .env 文件
vim .env

# 修改以下变量为强密码：
DB_ROOT_PASSWORD=your_strong_password
DB_PASSWORD=your_strong_password
REDIS_PASSWORD=your_strong_password
```

### 禁用 PhpMyAdmin（生产环境）
```bash
# 编辑 docker-compose.yml
vim docker-compose.yml

# 找到 phpmyadmin 部分，注释掉或删除：
# phpmyadmin:
#   image: phpmyadmin:5-apache
#   ...
```

### 启用 HTTPS（使用 Let's Encrypt）
```bash
# 安装 Certbot
sudo apt-get install certbot python3-certbot-nginx

# 获取证书
sudo certbot certonly --standalone -d yourdomain.com

# 修改 Nginx 配置以支持 HTTPS（高级）
```

### 配置防火墙
```bash
# 只允许特定端口
sudo ufw allow 80/tcp      # HTTP
sudo ufw allow 443/tcp     # HTTPS
sudo ufw deny 3306/tcp     # MySQL（仅内部访问）
sudo ufw deny 6379/tcp     # Redis（仅内部访问）
```

## 🚀 部署到云服务器

### 支持的平台
- ✅ AWS EC2
- ✅ 阿里云 ECS
- ✅ 腾讯云 CVM
- ✅ DigitalOcean
- ✅ Linode
- ✅ 任何支持 Docker 的 Linux 服务器

### 部署步骤
```bash
# 1. 连接到服务器
ssh user@your-server-ip

# 2. 安装 Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 3. 将项目上传到服务器
git clone <your-repo-url>
cd Internet-development-project

# 4. 一键部署
./deploy.sh
```

### 自启动配置（systemd）
```bash
# 创建服务文件
sudo tee /etc/systemd/system/health-agent.service << EOF
[Unit]
Description=Health Agent Service
After=docker.service
Requires=docker.service

[Service]
Type=simple
WorkingDirectory=/path/to/Internet-development-project
ExecStart=/bin/bash -c './deploy.sh'
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 启用自启动
sudo systemctl enable health-agent
sudo systemctl start health-agent
sudo systemctl status health-agent
```

## 🐛 故障排查快速指南

### 1️⃣ 镜像构建失败
```bash
# 查看详细日志
docker-compose logs backend
docker-compose logs frontend

# 检查磁盘空间
df -h

# 清理并重试
./deploy.sh clean
./deploy.sh
```

### 2️⃣ 容器启动失败
```bash
# 查看容器日志
docker logs health-agent-backend
docker logs health-agent-frontend

# 重启容器
docker-compose restart backend
docker-compose restart frontend
```

### 3️⃣ 端口被占用
```bash
# 查看占用情况
lsof -i :8080
lsof -i :80

# 修改 .env 中的端口，然后重启
./deploy.sh stop
./deploy.sh
```

### 4️⃣ 无法连接数据库
```bash
# 检查 MySQL 状态
docker-compose logs mysql

# 重启 MySQL
docker-compose restart mysql
sleep 10

# 重启后端
docker-compose restart backend
```

### 5️⃣ 前端无法访问后端 API
```bash
# 测试网络连接
docker exec health-agent-frontend curl http://backend:8080/health

# 查看 Nginx 配置
docker exec health-agent-frontend cat /etc/nginx/conf.d/default.conf

# 查看后端日志
docker-compose logs backend
```

## 📚 完整文档索引

| 文档 | 内容 | 适用场景 |
|------|------|--------|
| [QUICKSTART.md](./QUICKSTART.md) | 快速开始（3 个命令） | 想快速启动 |
| [DEPLOY.md](./DEPLOY.md) | 详细部署指南（60+ 行） | 需要深入了解 |
| [DEPLOYMENT_SUMMARY.md](./DEPLOYMENT_SUMMARY.md) | 部署方案说明 | 理解整体架构 |
| README.md | 项目说明 | 了解项目信息 |

## ❓ 常见问题

### Q: 为什么要用容器化？
**A**: 
- 无需本地安装 Java、Node、MySQL
- 开发、测试、生产环境完全一致
- 轻松扩展和部署
- 便于团队协作

### Q: 首次部署为什么这么慢？
**A**: 需要：
1. 下载基础镜像（几百 MB）
2. 下载 Maven 依赖（几百 MB）
3. 下载 npm 依赖（几百 MB）
4. 编译构建 Java 项目
5. 编译构建 Vue 项目

后续更新只需重新构建变更的层，会快得多。

### Q: 容器镜像总大小多少？
**A**: 约 1.1GB：
- MySQL: 500MB
- Node 基础: 150MB  
- 后端 JAR: 400MB
- Redis: 20MB
- Nginx: 50MB

### Q: 能在 Windows 上运行吗？
**A**: 可以！需要：
1. 安装 [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
2. 启用 WSL 2
3. 打开项目，运行 `./deploy.sh`

### Q: 生产环境怎么部署？
**A**: 
1. 修改 `.env` 中的所有密码
2. 禁用 PhpMyAdmin
3. 配置 HTTPS
4. 配置防火墙
5. 设置自动备份

详见 [DEPLOY.md](./DEPLOY.md) 的"生产部署"章节。

## 📞 获得帮助

```bash
# 查看部署脚本帮助
./deploy.sh help

# 查看 Docker Compose 帮助
docker-compose --help

# 查看具体命令的帮助
docker-compose logs --help
```

## 🎉 总结

您现在拥有：

✅ **完全容器化的前后端应用**
- 后端：Spring Boot 3.2.1 + MySQL 8 + Redis 7
- 前端：Vue 3 + Vite 6 + Nginx
- 数据库管理：PhpMyAdmin

✅ **一键部署脚本**
- 自动化构建、启动、验证
- 支持日志查看、容器管理等常用命令

✅ **详细的文档和配置**
- 支持端口、密码、内存等自定义
- 包含故障排查和生产部署指南

✅ **开箱即用**
- 无需本地开发环境
- 任何有 Docker 的地方都能运行

**现在就开始**:
```bash
cd Internet-development-project
./deploy.sh
```

祝您使用愉快! 🚀
