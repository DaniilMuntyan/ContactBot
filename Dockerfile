FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 443 8443 8080 80
ADD target/demo-0.0.1-SNAPSHOT.jar app.jar
# ADD src $HOME/src
# CMD apt-get install libssl-dev
# CMD openssl req -newkey rsa:2048 -sha256 -nodes -keyout myprivate.key -x509 -days 365 -out mypublic.pem -subj "/C=US/ST=New York/L=Brooklyn/O=Example Brooklyn Company/CN=35.242.252.221"
ENTRYPOINT ["java","-jar","./app.jar"]