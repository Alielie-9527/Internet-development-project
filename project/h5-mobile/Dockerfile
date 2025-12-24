# ==================== 前端 Dockerfile ====================
# 多阶段构建：在 Node 容器中构建，再用 Nginx 提供服务

# ==================== 阶段 1: 构建阶段 ====================
FROM node:18-alpine AS builder

WORKDIR /app

# 复制 package.json 和 lock 文件
COPY package*.json ./

# 安装依赖（包括 devDependencies，因为构建需要 Vite）
RUN npm ci

# 复制源代码
COPY . .

# 构建 Vite 项目
RUN npm run build

# ==================== 阶段 2: 运行阶段 ====================
FROM nginx:alpine

# 删除默认配置
RUN rm /etc/nginx/conf.d/default.conf

# 复制自定义 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 从构建阶段复制编译后的前端文件到 Nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# 暴露端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
