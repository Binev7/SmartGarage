# 1. Build Stage (Използваме Java, за да пуснем Gradle)
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Копираме всички файлове
COPY . .

# Даваме права за изпълнение на "gradlew" (важно за Linux/Render)
RUN chmod +x gradlew

# Стартираме build процеса с Gradle (вместо Maven)
RUN ./gradlew clean build -x test

# 2. Run Stage (Стартиране)
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# При Gradle готовите файлове са в папка build/libs/
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]