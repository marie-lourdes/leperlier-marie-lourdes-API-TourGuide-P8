FROM eclipse-temurin:17-jdk-alpine as builder

VOLUME /tmp

ADD target/*.jar tourguide-1.0.0.jar

RUN java -Djarmode=layertools -jar tourguide-0.0.1-SNAPSHOT.jar extract
 

FROM eclipse-temurin:17-jdk-alpine

EXPOSE 8080

COPY --from=builder dependencies/ ./

COPY --from=builder snapshot-dependencies/ ./

COPY --from=builder spring-boot-loader/ ./

COPY --from=builder application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]