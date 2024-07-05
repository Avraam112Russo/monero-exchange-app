FROM amazoncorretto:17-alpine
COPY target/*.jar myapp.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "myapp.jar"]