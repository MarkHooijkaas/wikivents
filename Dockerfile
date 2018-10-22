FROM openjdk:8-jre-alpine
RUN apk update
RUN apk add rsync openssh
COPY lib /site/lib
COPY src/main/resources /site/resources
COPY config /site/config
COPY wikivents*.jar /site/
WORKDIR /site
CMD ["/usr/bin/java", "-jar", "/site/wikivents-0.0.1.jar"]
#CMD ["sh"]