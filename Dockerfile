# 1. Build Stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Копираме всичко
COPY . .

# ВАЖНО: Влизаме в папката, където е pom.xml
# Промени "SmartGarage" с името на папката от твоя GitHub!
WORKDIR /app/SmartGarage

RUN mvn clean package -DskipTests

# 2. Run Stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Тук също трябва да внимаваме откъде копираме
# Ако горната стъпка е в папка, файлът ще е там
COPY --from=build /app/SmartGarage/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]