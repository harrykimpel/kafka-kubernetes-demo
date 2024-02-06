FROM openjdk:17.0.2-jdk-slim

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

COPY opentelemetry-javaagent.jar .

ENV JAVA_TOOL_OPTIONS="-javaagent:./opentelemetry-javaagent.jar -Dotel.instrumentation.messaging.experimental.receive-telemetry.enabled=true"
ENV OTEL_SERVICE_NAME="kafka-kubernetes-demo-1.0.0"
ENV OTEL_EXPORTER_OTLP_ENDPOINT='https://otlp.nr-data.net'
ENV OTEL_EXPORTER_OTLP_HEADERS="api-key=NEW_RELIC_LICENSE_KEY"
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENV OTEL_LOGS_EXPORTER=otlp

ENTRYPOINT ["java", "-jar", "/app.jar"]
