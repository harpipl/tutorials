FROM adoptopenjdk:16-jre-hotspot

ARG JAR_FILE=*.jar
COPY maven/${JAR_FILE} application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]