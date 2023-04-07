FROM amazoncorretto:11

ENV JAVA_OPTS="-Xms512m"

COPY target/*.jar shareit-server.jar

#RUN echo "Started with $JAVA_OPTS" > /var/log/shareit/server/serverapp.log

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /shareit-server.jar" ]