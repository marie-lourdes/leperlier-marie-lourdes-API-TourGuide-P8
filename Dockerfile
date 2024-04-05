FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

COPY target/*.jar /tourguide-1.0.0.jar
 
EXPOSE 8080
 
ENTRYPOINT ["java","-jar","tourguide-1.0.0.jar"]

