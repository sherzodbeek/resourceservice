FROM amazoncorretto:21-alpine-jdk

COPY build/libs/resourceservice-0.0.1-SNAPSHOT.jar /home/resourceservice.jar
CMD ["java", "-jar", "/home/resourceservice.jar"]

#docker run --name resourceservice --network app-network -p 8080:8080 -d resourceservice