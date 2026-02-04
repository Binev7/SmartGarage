# 1. Build Stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# СТЪПКА А: Копираме САМО pom.xml първо.
# Ако файлът липсва в GitHub, Docker ще спре ТУК с грешка "failed to copy".
COPY pom.xml .

# СТЪПКА Б: Сваляме библиотеките (това прави build-а по-бърз следващия път)
RUN mvn dependency:go-offline -B

# СТЪПКА В: Копираме останалия код (src)
COPY src ./src

# СТЪПКА Г: Компилираме
RUN mvn clean package -DskipTests

# 2. Run Stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]