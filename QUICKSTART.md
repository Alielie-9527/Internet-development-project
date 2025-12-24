# 🚀 快速开始指南

## 一句话开始

```bash
cd Internet-development-project
cp .env.example .env
chmod +x deploy.sh
./deploy.sh
```

等待 10-20 分钟，然后访问：

- **前端**: http://localhost
- **后端**: http://localhost:8080
- **文档**: http://localhost:8080/doc.html
- **数据库管理**: http://localhost:8081

## 常用命令

```bash
# 启动（首次部署）
./deploy.sh

# 查看日志
./deploy.sh logs

# 停止服务
./deploy.sh stop

# 完全清理
./deploy.sh clean

# 重新部署
./deploy.sh rebuild
```

## 文件结构

```
├── deploy.sh                          # 部署脚本（一键启动）
├── docker-compose.yml                 # Docker Compose 配置
├── .env.example                       # 环境变量示例
├── .env                               # 环境变量（自动生成）
├── DEPLOY.md                          # 详细文档
├── project/
│   ├── api-backend/
│   │   ├── Dockerfile                # 后端镜像
│   │   ├── .dockerignore
│   │   └── ...
│   ├── h5-mobile/
│   │   ├── Dockerfile                # 前端镜像
│   │   ├── nginx.conf                # Nginx 配置
│   │   ├── .dockerignore
│   │   └── ...
│   └── database/
│       ├── init/                      # 数据库初始化脚本
│       └── ...
└── ...
```

## 故障排查

### 1. 镜像构建失败

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

### 2. 服务启动失败

```bash
# 检查容器日志
docker-compose logs [service]

# 重启单个服务
docker-compose restart [service]

# 例如：
docker-compose logs backend
docker-compose restart backend
```

### 3. 端口冲突

```bash
# 查看占用情况
lsof -i :8080

# 编辑 .env 文件修改端口
# 然后重新启动
./deploy.sh stop
./deploy.sh
```

### 4. 无法连接到数据库

```bash
# 检查 MySQL 是否运行
docker-compose logs mysql

# 重启 MySQL
docker-compose restart mysql

# 等待 MySQL 完全启动后重启后端
sleep 10
docker-compose restart backend
```

## 内存和 CPU 配置

编辑 `.env` 文件：

```env
# Java 应用程序内存配置
JAVA_OPTS=-Xmx512m -Xms256m    # 默认：512MB 堆内存

# 根据服务器配置调整：
# 小型服务器（1GB）：-Xmx256m -Xms128m
# 中型服务器（4GB）：-Xmx1024m -Xms512m  
# 大型服务器（8GB+）：-Xmx2048m -Xms1024m
```

## 容器镜像大小

部署完成后的镜像大小约为：

- **后端**: ~400MB
- **前端**: ~150MB
- **MySQL**: ~500MB
- **Redis**: ~20MB
- **Nginx**: ~50MB

总计：约 1.1GB

## 性能优化建议

1. **关闭 PhpMyAdmin**（生产环境）
   - 编辑 `docker-compose.yml`，注释 phpmyadmin 服务

2. **增加 Java 内存**
   - 编辑 `.env`，修改 `JAVA_OPTS`

3. **使用容器日志驱动**
   - 防止日志文件过大

4. **定期清理未使用的镜像**
   ```bash
   docker image prune -a
   ```

## 生产部署检查清单

- [ ] 修改所有默认密码（.env 文件）
- [ ] 禁用 PhpMyAdmin（生产环境）
- [ ] 配置 HTTPS（使用 Let's Encrypt）
- [ ] 配置备份策略
- [ ] 配置日志轮转
- [ ] 配置监控告警
- [ ] 配置自动恢复
- [ ] 测试灾难恢复方案

## 更多帮助

详见 [DEPLOY.md](./DEPLOY.md) 了解完整文档。

```bash
./deploy.sh help
```
