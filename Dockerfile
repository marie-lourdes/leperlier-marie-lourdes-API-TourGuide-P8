FROM eclipse-temurin:17-jdk-alpine as builder

VOLUME /tmp
ARG JAR_FILE
COPY &{JAR_FILE} /tourguide-1.0.0.jar

RUN java -Djarmode=layertools -jar tourguide-1.0.0.jar extract 

FROM eclipse-temurin:17

WORKDIR application

# Dockerize
COPY --from=builder application/dockerize ./

ARG EXPOSED_PORT=8080
EXPOSE ${EXPOSED_PORT}

ENV SPRING_PROFILES_ACTIVE docker

COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]


