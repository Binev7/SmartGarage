# 1. Build Stage (Тук компилираме)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Run Stage (Тук е поправката!)
# Сменяме 'openjdk:17-jdk-slim' с 'eclipse-temurin:17-jdk-jammy'
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# Копираме готовия JAR файл от първата стъпка
COPY --from=build /app/target/*.jar app.jar

# Отваряме порт 8080
EXPOSE 8080

# Стартираме приложението
ENTRYPOINT ["java", "-jar", "app.jar"]