FROM openjdk:11
USER root
WORKDIR /app
COPY ./target/poc-kafka-stream-1.0-SNAPSHOT-jar-with-dependencies.jar /app/
ENTRYPOINT ["java", "-jar", "poc-kafka-stream-1.0-SNAPSHOT-jar-with-dependencies.jar"]
