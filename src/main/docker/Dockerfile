FROM frolvlad/alpine-oraclejdk8:slim
ADD bootbatchintegrationdemo.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENV JAVA_ARGS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar $JAVA_ARGS"]
