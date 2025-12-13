FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy project files
COPY . .

# 🔥 FIX: give execute permission to mvnw
RUN chmod +x mvnw

# Build the Spring Boot app
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/worknow-backend-0.0.1-SNAPSHOT.jar"]
