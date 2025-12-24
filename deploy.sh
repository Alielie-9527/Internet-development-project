#!/bin/bash

# ==================== 容器化自动化部署脚本 ====================
# 特点：完全在容器内构建，无需安装任何开发环境
# 使用方式：
#   ./deploy.sh                    # 启动所有服务
#   ./deploy.sh init-db            # 初始化数据库
#   ./deploy.sh logs               # 查看日志
#   ./deploy.sh stop               # 停止服务
#   ./deploy.sh clean              # 清理容器和镜像

set -e

# ==================== 颜色定义 ====================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'  # 无颜色

# ==================== 日志函数 ====================
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_step() {
    echo -e "\n${BLUE}==================== $1 ====================${NC}\n"
}

# ==================== 检查命令是否存在 ====================
check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "$1 未安装，请先安装 $1"
        exit 1
    fi
}

# ==================== 帮助信息 ====================
show_help() {
    cat << EOF
${GREEN}容器化部署脚本${NC}

使用方式: $0 [命令]

命令:
    (无)           启动所有服务（默认）
    init-db        初始化数据库
    logs           查看实时日志
    ps             显示服务状态
    stop           停止服务
    down           停止并删除容器
    clean          清理容器和镜像
    rebuild        重新构建镜像并启动
    status         检查部署状态
    help           显示此帮助信息

示例:
    $0              # 启动服务
    $0 logs         # 查看日志
    $0 stop         # 停止服务
EOF
}

# ==================== 命令路由 ====================
COMMAND=${1:-start}

case "$COMMAND" in
    help)
        show_help
        exit 0
        ;;
    logs)
        docker-compose logs -f
        exit 0
        ;;
    ps)
        docker-compose ps
        exit 0
        ;;
    stop)
        log_step "停止服务"
        docker-compose stop
        log_info "服务已停止 ✓"
        exit 0
        ;;
    down)
        log_step "停止并删除容器"
        docker-compose down
        log_info "容器已删除 ✓"
        exit 0
        ;;
    clean)
        log_step "清理资源"
        log_warn "即将删除所有容器和镜像，请确认..."
        docker-compose down --volumes --remove-orphans
        docker rmi -f $(docker images | grep health-agent | awk '{print $3}') 2>/dev/null || true
        log_info "清理完成 ✓"
        exit 0
        ;;
    status)
        log_step "部署状态"
        docker-compose ps
        exit 0
        ;;
    init-db)
        log_step "初始化数据库"
        log_info "等待 MySQL 启动..."
        sleep 10
        log_info "导入初始化脚本..."
        docker exec health-agent-mysql mysql -uroot -proot nutrition_db < ./project/database/init/01-schema.sql
        docker exec health-agent-mysql mysql -uroot -proot nutrition_db < ./project/database/init/02-data.sql
        log_info "数据库初始化完成 ✓"
        exit 0
        ;;
    rebuild)
        COMMAND="start"
        log_step "重新构建镜像"
        docker-compose build --no-cache
        ;;
    start|"")
        ;;
    *)
        log_error "未知命令: $COMMAND"
        show_help
        exit 1
        ;;
esac

# ==================== 0. 环境检查 ====================
log_step "环境检查"

log_info "检查 Docker..."
check_command docker
docker_version=$(docker --version)
log_info "✓ $docker_version"

log_info "检查 Docker Compose..."
check_command docker-compose
compose_version=$(docker-compose --version)
log_info "✓ $compose_version"

# ==================== 1. 配置文件检查 ====================
log_step "配置文件检查"

if [ ! -f .env ]; then
    log_warn ".env 文件不存在，正在从 .env.example 复制..."
    if [ -f .env.example ]; then
        cp .env.example .env
        log_info ".env 文件已创建，请根据需要修改配置"
    else
        log_error ".env.example 文件不存在"
        exit 1
    fi
else
    log_info ".env 文件已存在 ✓"
fi

# ==================== 2. 源代码检查 ====================
log_step "源代码检查"

if [ ! -d ./project/api-backend ]; then
    log_error "后端源代码不存在: ./project/api-backend"
    exit 1
fi
log_info "✓ 后端源代码存在"

if [ ! -d ./project/h5-mobile ]; then
    log_error "前端源代码不存在: ./project/h5-mobile"
    exit 1
fi
log_info "✓ 前端源代码存在"

if [ ! -d ./project/database ]; then
    log_error "数据库初始化脚本不存在: ./project/database"
    exit 1
fi
log_info "✓ 数据库配置存在"

# ==================== 3. 构建镜像 ====================
log_step "构建 Docker 镜像"

log_info "预拉取基础镜像..."
docker pull eclipse-temurin:17-jre-alpine
docker pull node:18-alpine
docker pull nginx:alpine
docker pull mysql:8.0
docker pull redis:7-alpine
docker pull phpmyadmin:5-apache
log_info "基础镜像拉取完成 ✓"

log_warn "开始构建项目镜像（首次可能需要 10-20 分钟）..."
docker-compose build

log_info "镜像构建完成 ✓"

# ==================== 4. 停止旧服务 ====================
log_step "停止旧服务（如有）"

if docker-compose ps 2>/dev/null | grep -q "Up"; then
    log_warn "检测到正在运行的服务，停止中..."
    docker-compose down
fi
log_info "✓ 准备完毕"

# ==================== 5. 启动新服务 ====================
log_step "启动服务"

log_info "启动所有容器..."
docker-compose up -d

log_info "等待服务启动... (约 30 秒)"
sleep 30

# ==================== 6. 验证部署 ====================
log_step "验证部署"

log_info "检查服务状态..."
docker-compose ps

# 检查各服务健康状态
echo ""
log_info "等待服务就绪..."

# MySQL 检查
for i in {1..30}; do
    if docker exec health-agent-mysql mysqladmin ping -h localhost &> /dev/null; then
        log_info "✓ MySQL 已就绪"
        break
    fi
    if [ $i -eq 30 ]; then
        log_warn "⚠ MySQL 未就绪（可继续）"
    fi
    sleep 1
done

# Redis 检查
for i in {1..30}; do
    if docker exec health-agent-redis redis-cli ping &> /dev/null; then
        log_info "✓ Redis 已就绪"
        break
    fi
    if [ $i -eq 30 ]; then
        log_warn "⚠ Redis 未就绪（可继续）"
    fi
    sleep 1
done

# 后端检查
for i in {1..60}; do
    if curl -sf http://localhost:8080/doc.html &> /dev/null; then
        log_info "✓ 后端 API 已就绪"
        break
    fi
    if [ $i -eq 60 ]; then
        log_warn "⚠ 后端 API 仍在启动中，请稍候..."
    fi
    sleep 1
done

# 前端检查
for i in {1..30}; do
    if curl -sf http://localhost/health &> /dev/null; then
        log_info "✓ 前端已就绪"
        break
    fi
    if [ $i -eq 30 ]; then
        log_warn "⚠ 前端未就绪（可继续）"
    fi
    sleep 1
done

# ==================== 7. 部署完成 ====================
log_step "部署完成！"

echo -e "${GREEN}"
cat << EOF
========================================
  ✓ 部署成功！服务访问地址：
========================================
📱 前端应用:    http://localhost
📚 API 文档:    http://localhost:8080/doc.html
🔌 后端 API:    http://localhost:8080
🗄️  数据库管理:  http://localhost:8081 (PhpMyAdmin)
========================================
EOF
echo -e "${NC}"

# ==================== 8. 显示后续命令 ====================
echo -e "\n${BLUE}常用命令:${NC}"
echo "  查看日志:       ./deploy.sh logs"
echo "  显示状态:       ./deploy.sh ps"
echo "  停止服务:       ./deploy.sh stop"
echo "  删除容器:       ./deploy.sh down"
echo "  清理资源:       ./deploy.sh clean"
echo "  重新部署:       ./deploy.sh rebuild"
echo ""
log_info "💡 提示: docker-compose logs -f  可查看实时日志"
