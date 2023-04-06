FROM amazoncorretto:11

ENV JAVA_OPTS="-Xms512m"

COPY target/*.jar shareit-server.jar
ENTRYPOINT ["sh", "-c", "echo $JAVA_OPTS && java $JAVA_OPTS -jar /shareit-server.jar"]
