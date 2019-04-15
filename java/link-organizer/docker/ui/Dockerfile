FROM openjdk:8-jdk

RUN set -o errexit -o nounset \
    && mkdir /app

WORKDIR /app/

VOLUME /app

EXPOSE 8080
EXPOSE 1099
EXPOSE 2099

CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
