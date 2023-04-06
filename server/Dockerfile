FROM amazoncorretto:11

ENV JAVA_OPTS="-Xms512m"

COPY target/*.jar shareit-server.jar
ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /shareit-server.jar"]

#RUN mkdir -p /var/log/shareit
#RUN touch /var/log/shareit/serverapplication.log
#RUN chmod 777 /var/log/shareit/server/app.log

VOLUME /var/log/shareit/server

#CMD ["java", "-Dlogging.level.root=INFO", "-jar", "/shareit-server.jar"]