FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 443 8443 8080 80
ADD demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","./app.jar"]