FROM openjdk:16-alpine

COPY target/bot.jar .

VOLUME [ "/data", "/config" ]

EXPOSE 5555

ENTRYPOINT ["java","-jar", "-Dfile.encoding=UTF-8", "/bot.jar"]