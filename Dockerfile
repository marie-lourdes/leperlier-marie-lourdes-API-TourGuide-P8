FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

WORKDIR /api

ADD target/*.jar tourguide-1.0.0.jar
 
EXPOSE 8080
 
ENTRYPOINT ["java","-jar","/api/tourguide-1.0.0.jar"]

