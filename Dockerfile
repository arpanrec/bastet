FROM eclipse-temurin@sha256:69384a6005201078433af90c8355f960a9fe36e6148d88342ef672e71d65d660 as runner
# eclipse-temurin:17-jre
WORKDIR /app
COPY ./build/libs/minerva-boot-*.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]
