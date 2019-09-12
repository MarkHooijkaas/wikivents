FROM openjdk:8-jre-alpine
COPY lib /site/lib
COPY src/main/resources /site/resources
COPY src/main/templates /site/templates
COPY config /site/config
COPY wikivents*.jar /site/
WORKDIR /site
EXPOSE 80
CMD ["/usr/bin/java", "-jar", "/site/wikivents-0.0.1.jar"]
#CMD ["sh"]