FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# pom.xml ve kaynak kodu kopyala
COPY pom.xml .
COPY src ./src

# Maven ile jar dosyasını build et
RUN mvn clean package -DskipTests

# ---- Run aşaması ----
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Build edilen jar'ı kopyala
COPY --from=builder /app/target/*.jar app.jar

# Port ayarı
EXPOSE 8080

# Uygulamayı başlat
ENTRYPOINT ["java", "-jar", "app.jar"]