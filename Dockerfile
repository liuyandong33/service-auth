FROM openjdk:8
EXPOSE 8080
MAINTAINER service-auth
LABEL app="service-auth" version="1.0.0" by="service-auth"
COPY ./build/libs/service-auth-1.0.0.jar service-auth.jar
CMD java -jar service-auth.jar