FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-Xms64m -Xmx192m -Xss512k -XX:+UseSerialGC -XX:ReservedCodeCacheSize=48m -XX:MaxDirectMemorySize=32m -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
