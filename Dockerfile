FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY finatiol-common/pom.xml ./finatiol-common/pom.xml
COPY finatiol-common/src ./finatiol-common/src
RUN mvn -f finatiol-common/pom.xml install -DskipTests -q
COPY finatiol-productos-ms/pom.xml ./finatiol-productos-ms/pom.xml
COPY finatiol-productos-ms/src ./finatiol-productos-ms/src
RUN mvn -f finatiol-productos-ms/pom.xml package -DskipTests -q

FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8084
COPY --from=build /app/finatiol-productos-ms/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]