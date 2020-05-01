FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 9100
ADD ./target/oauth-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
ENTRYPOINT ["sh","-c","java -jar /app.jar"]