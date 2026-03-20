FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /src
COPY buildSrc ./buildSrc/
COPY utils ./utils/
COPY spring-jpa ./spring-jpa/
COPY spring-exposed ./spring-exposed/
COPY settings.gradle.kts ./
COPY gradle ./gradle/
COPY gradlew ./
RUN chmod +x ./gradlew
RUN ./gradlew :spring-exposed:bootJar -x test

##
#  Second Stage
##
FROM eclipse-temurin:25-jre-alpine
EXPOSE 8080
COPY --from=builder /src/spring-exposed/build/libs/spring-exposed*.jar /app/spring-boot.jar
ENTRYPOINT ["java", "-jar", "/app/spring-boot.jar"]