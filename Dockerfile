# Stage 1: Build application using JDK 25 & Maven Wrapper
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copy maven wrapper & pom.xml for dependency caching
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code and build final jar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Lightweight Java 25 JRE runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Memory optimizations for 512MB free tier containers
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseG1GC"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
