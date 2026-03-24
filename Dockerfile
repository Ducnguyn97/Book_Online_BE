# ==========================================
# STAGE 1: Build
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml trước để cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source và build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==========================================
# STAGE 2: Run
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Tạo user không phải root (bảo mật)
RUN addgroup -S spring && adduser -S spring -G spring

# Copy jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

RUN chown spring:spring app.jar
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]