# ==================== 后端 Dockerfile ====================
# 多阶段构建：先在 Maven 容器中编译，再在 JRE 容器中运行

# ==================== 阶段 1: 构建阶段 ====================
FROM maven:3.8-eclipse-temurin-17 AS builder

WORKDIR /usr/src/app

# 复制 pom.xml 先行缓存依赖层
COPY pom.xml .

# 下载依赖（分离依赖层以便缓存）
RUN mvn dependency:resolve -DskipTests

# 复制源代码
COPY src ./src

# 打印配置文件内容以验证
RUN cat src/main/resources/application.yml

# 编译并打包
RUN mvn clean package -DskipTests

# ==================== 阶段 2: 运行阶段 ====================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /usr/src/app/target/*.jar app.jar

# 创建非 root 用户（安全最佳实践）
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
