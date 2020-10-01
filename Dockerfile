FROM adoptopenjdk:8-jre-openj9

EXPOSE 8030

ADD build/libs/*.jar transfer.jar

ENTRYPOINT ["java", "-jar", "transfer.jar"]
