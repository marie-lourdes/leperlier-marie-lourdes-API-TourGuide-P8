FROM eclipse-temurin:17-jdk-alpine 

COPY /target*.jar /tourguide-1.0.0-SNAPSHOT.jar

EXPOSE 8080
  
ENTRYPOINT ["java","-jar","tourguide-1.0.0-SNAPSHOT.jar"]

