FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .

# --- ДИАГНОСТИКА ---
# Тази команда ще покаже в лога всички папки и файлове.
# Така ще разберем къде точно е pom.xml
RUN echo "================== СТАРТ НА СПИСЪКА =================="
RUN ls -R
RUN echo "================== КРАЙ НА СПИСЪКА ==================="
# -------------------

# Опитваме се да стартираме (вероятно ще гръмне пак, но целта е да видим списъка горе)
RUN mvn clean package -DskipTests

# Втората част не е важна сега, защото ще гръмне преди това
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]