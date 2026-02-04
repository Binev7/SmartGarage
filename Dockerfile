# 1. Използваме Maven image, за да компилираме проекта (Build Stage)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Използваме лек Java image, за да пуснем приложението (Run Stage)
FROM openjdk:17-jdk-slim
WORKDIR /app
# Копираме готовия JAR файл от първата стъпка
COPY --from=build /app/target/*.jar app.jar

# Отваряме порт 8080 (стандартния за Spring Boot)
EXPOSE 8080

# Стартираме приложението
ENTRYPOINT ["java", "-jar", "app.jar"]