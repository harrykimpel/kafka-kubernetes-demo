FROM openjdk:17.0.2-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "/app.jar"]
