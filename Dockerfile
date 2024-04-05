FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR application

RUN java -jar tourguide-1.0.0.jar 

FROM eclipse-temurin:17

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE docker

COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]


