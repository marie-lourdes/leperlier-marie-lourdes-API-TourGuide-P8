# API  Tourguide Application

TourGuide is a Spring Boot application and a centerpiece of the company's application portfolio. 
It allows users to see nearby tourist attractions and get discounts on hotel stays and tickets to various shows.

### Technologies

> Java 17  
> Spring Boot 3.X  
> JUnit 5  
> Docker-desktop 4.28.0


### Installing

A step by step series of examples that tell you how to get a development env running:

1.Install Java:

https://java.tutorials24x7.com/blog/how-to-install-java-17-on-windows

2.Install Maven:

https://maven.apache.org/install.html

3.Install Spring Tools for Eclipse

https://www.eclipse.org/community/eclipse_newsletter/2018/february/springboot.php

4.Install modules

- mvn install:install-file -Dfile=/libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=/libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=/libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar

5.Install Docker-desktop

https://www.docker.com/products/docker-desktop/

### Running App

Post installation of Java, Maven, and modules and Spring Tools 4, you will have to run app  with  Boot DashBoard of Spring Tools 
or with your CLI , mvn spring-boot:run .

Finally, you will be ready to  use API and request 
The host and port is :http://localhost:8080

### Endpoints of API TourGuide

#### Endpoints TourGuide service

This service makes it possible to determine the five closest attractions of the last user position, and to determine  trip deals

- GET: **/tourguide/getNearbyAttractions'**
- GET: **/tourguide/getTripDeals'**

####Reward   Service

This service makes it possible to determine the points (specific to each tourist attraction) which are awarded to the user who visits a given tourist attraction

- GET: **/tourguide/getRewards?userName=<userName>**

####GpsUtil  Service

This service makes it possible to determine the geographical position of the user

- GET: **/tourguide/'getLocation?userName=<userName>'**


####Endpoints  User Service

- GET: **/tourguide/user/getUser?userName=<userName>'**


### Use Docker Container

In root of project, the file Dockerfile allow you to build image and create container 

- Run command Docker in your favorite CLI:'docker build -t api-tourguide:v1 .' for  creating image, you will see in the interface of Docker-desktop installed

- Then run: 'docker run -d -p 8080:8080  api-tourguide:v1 '

- Finally, you can run the homepage of application and test the  Docker container image (with the jar of the application) on 'http://localhost:8080/tourguide/


### Testing

 For testing application run  'mvn verify' 

 For testing request:
 Use Postman after running application


