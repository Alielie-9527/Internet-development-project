# 🚀 容器化部署指南

> 完全基于 Docker 的一键部署方案，无需本地安装开发环境，只需要 Docker。

## 📋 前置要求

- **Docker**: v20.10+
- **Docker Compose**: v2.0+
- **硬盘空间**: 至少 5GB
- **内存**: 至少 2GB 空闲（推荐 4GB+）

### 安装 Docker

**Ubuntu/Debian:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 配置 sudo 权限（可选）
sudo usermod -aG docker $USER
```

**macOS:**
```bash
# 使用 Homebrew
brew install docker
brew install docker-compose

# 或直接下载 Docker Desktop
# https://www.docker.com/products/docker-desktop
```

**Windows:**
- 安装 [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
- 启用 WSL 2

## 🎯 快速开始

### 1️⃣ 初始化配置

```bash
# 进入项目根目录
cd Internet-development-project

# 从示例配置创建 .env 文件
cp .env.example .env

# （可选）编辑 .env 文件以修改端口和密码
# vim .env
```

### 2️⃣ 一键部署

```bash
# 添加执行权限
chmod +x deploy.sh

# 启动所有服务（约 10-20 分钟，取决于网络）
./deploy.sh
```

部署脚本会自动：
- ✅ 检查 Docker 环境
- ✅ 拉取基础镜像
- ✅ 构建项目镜像
- ✅ 启动所有容器
- ✅ 等待服务就绪
- ✅ 显示访问地址

### 3️⃣ 访问应用

部署完成后，访问以下地址：

| 服务 | 地址 | 用途 |
|------|------|------|
| 📱 前端应用 | http://localhost | 移动端应用 |
| 📚 API 文档 | http://localhost:8080/doc.html | 接口文档 |
| 🔌 后端 API | http://localhost:8080 | RESTful API |
| 🗄️ 数据库管理 | http://localhost:8081 | PhpMyAdmin |

## 📝 常用命令

```bash
# 查看实时日志
./deploy.sh logs

# 显示所有容器状态
./deploy.sh ps

# 停止服务（保留容器和数据）
./deploy.sh stop

# 启动已停止的服务
docker-compose up -d

# 删除所有容器（保留数据卷）
./deploy.sh down

# 完全清理（删除容器、镜像、数据）
./deploy.sh clean

# 重新构建镜像
./deploy.sh rebuild

# 查看帮助信息
./deploy.sh help
```

## 🔧 自定义配置

### 修改端口

编辑 `.env` 文件：

```env
FRONTEND_PORT=80          # 前端端口
BACKEND_PORT=8080         # 后端端口
DB_PORT=3306              # MySQL 端口
REDIS_PORT=6379           # Redis 端口
PHPMYADMIN_PORT=8081      # PhpMyAdmin 端口
```

重启服务：
```bash
docker-compose down
./deploy.sh
```

### 修改数据库密码

编辑 `.env` 文件：

```env
DB_ROOT_PASSWORD=root          # 根用户密码
DB_USER=nutrition_user         # 普通用户名
DB_PASSWORD=nutrition123       # 普通用户密码
REDIS_PASSWORD=redis123        # Redis 密码
```

### 修改 Java 内存配置

编辑 `.env` 文件：

```env
JAVA_OPTS=-Xmx1024m -Xms512m   # JVM 内存（需要重启）
```

## 📊 容器架构

```
┌─────────────────────────────────────────────────┐
│         Docker Compose Network                  │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌──────────────┐   ┌──────────────────┐      │
│  │ Nginx+Vue    │   │  Spring Boot     │      │
│  │ (Port 80)    │◄──┤  Backend API     │      │
│  │              │   │  (Port 8080)     │      │
│  └──────────────┘   └─────────┬────────┘      │
│                                │               │
│        ┌───────────────────────┼───────────┐   │
│        │                       │           │   │
│   ┌────▼─────┐           ┌─────▼──┐  ┌───▼──┐ │
│   │  MySQL   │           │ Redis  │  │PhpMy-│ │
│   │(3306)    │           │(6379)  │  │Admin │ │
│   │          │           │        │  │(8081)│ │
│   └──────────┘           └────────┘  └──────┘ │
│                                                 │
└─────────────────────────────────────────────────┘
```

## 🐛 故障排查

### 容器无法启动

```bash
# 查看详细日志
docker-compose logs [service_name]

# 例如查看后端日志
docker-compose logs backend
```

### 端口已被占用

```bash
# 检查占用情况
lsof -i :8080

# 或修改 .env 文件中的端口配置
```

### MySQL 连接失败

```bash
# 检查 MySQL 容器状态
docker-compose logs mysql

# 重启 MySQL
docker-compose restart mysql
```

### 前端无法访问后端 API

检查 Nginx 配置：
```bash
# 进入 Nginx 容器
docker exec -it health-agent-frontend sh

# 检查代理配置
cat /etc/nginx/conf.d/default.conf
```

## 📈 性能优化

### 增加内存配置

编辑 `.env`：
```env
# 根据服务器配置调整
JAVA_OPTS=-Xmx2048m -Xms1024m
```

### 使用本地镜像加速

```bash
# 预先拉取镜像
docker pull eclipse-temurin:17-jre-alpine
docker pull node:18-alpine
docker pull nginx:alpine
docker pull mysql:8.0
docker pull redis:7-alpine
```

## 📦 生产部署

### 使用 registry 加速

编辑 `docker-compose.yml`，使用 DaoCloud 或阿里云镜像加速：

```yaml
services:
  mysql:
    image: registry.cn-hangzhou.aliyuncs.com/library/mysql:8.0
```

### 启用 HTTPS

使用 Let's Encrypt 证书（需要域名）：

```bash
# 使用 Certbot
docker run --rm -v /etc/letsencrypt:/etc/letsencrypt certbot certbot certonly -d yourdomain.com
```

### 备份数据

```bash
# 备份 MySQL 数据
docker exec health-agent-mysql mysqldump -uroot -proot nutrition_db > backup.sql

# 备份 Redis 数据
docker cp health-agent-redis:/data/dump.rdb ./redis-backup.rdb
```

## 🚀 部署到云服务器

### AWS / 阿里云 / 腾讯云

1. 安装 Docker 和 Docker Compose
2. 上传项目到服务器
3. 运行部署脚本

```bash
git clone <repo-url>
cd Internet-development-project
chmod +x deploy.sh
./deploy.sh
```

### 使用 systemd 自启动

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
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：
```bash
sudo systemctl enable health-agent
sudo systemctl start health-agent
```

## 📝 日志查看

### 查看所有服务日志

```bash
# 查看最后 100 行
docker-compose logs --tail=100

# 持续跟踪日志
docker-compose logs -f

# 只看特定服务
docker-compose logs backend
docker-compose logs frontend
```

### 导出日志

```bash
# 导出所有日志到文件
docker-compose logs > logs.txt
```

## 🔒 安全建议

1. **修改默认密码**
   - 编辑 `.env` 文件修改 MySQL 和 Redis 密码
   - 重新部署服务

2. **禁用 PhpMyAdmin（生产环境）**
   - 编辑 `docker-compose.yml` 注释 phpmyadmin 服务
   - 删除 `docker-compose.yml` 中的 phpmyadmin 段落

3. **启用防火墙**
   ```bash
   # 只开放必要的端口
   sudo ufw allow 80/tcp    # HTTP
   sudo ufw allow 443/tcp   # HTTPS
   sudo ufw allow 3306/tcp  # MySQL（仅内部）
   ```

4. **定期备份数据**
   ```bash
   # 每天凌晨 2 点备份
   0 2 * * * docker exec health-agent-mysql mysqldump -uroot -proot nutrition_db > /backups/backup-$(date +\%Y\%m\%d).sql
   ```

## 📞 支持和反馈

- 📧 Email: your-email@example.com
- 🐛 Issue: GitHub Issues
- 💬 Discussion: GitHub Discussions

## 📄 许可证

MIT License - 详见 LICENSE 文件

---

**提示**: 首次部署可能需要 10-20 分钟，具体时间取决于网络速度和计算机配置。
