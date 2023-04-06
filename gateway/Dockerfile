FROM amazoncorretto:11

COPY target/*.jar shareit-gateway.jar
ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /shareit-gateway.jar"]
