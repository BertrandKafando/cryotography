FROM  openjdk:17-jdk-alpine
VOLUME /tmp
ADD target/demo*.jar /app.jar
CMD ["java","-jar","/app.jar", "--spring.profiles.active=dev"]
EXPOSE 8089
