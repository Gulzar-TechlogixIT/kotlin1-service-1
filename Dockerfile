FROM gradle:8.14-jdk21 AS build
WORKDIR /workspace

COPY kot_lib_1/ /workspace/kot_lib_1/
RUN gradle -p /workspace/kot_lib_1 publishToMavenLocal --no-daemon

COPY kotlin1-service-1/ /workspace/kotlin1-service-1/
RUN gradle -p /workspace/kotlin1-service-1 installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/kotlin1-service-1/build/install/kotlin1-service-1/ /app/
EXPOSE 8081
CMD ["/app/bin/kotlin1-service-1"]