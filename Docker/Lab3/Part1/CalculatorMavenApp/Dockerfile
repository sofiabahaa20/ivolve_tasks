FROM maven:3.9-eclipse-temurin-17

# non-root user
RUN groupadd appgroup && useradd -m -g appgroup appuser

# working directory (owned by the non-root user)
WORKDIR /app
RUN chown appuser:appgroup /app

# copy source code
COPY --chown=appuser:appgroup pom.xml .
COPY --chown=appuser:appgroup src ./src

USER appuser

# build inside Docker
RUN mvn package

EXPOSE 8080

CMD ["java", "-jar", "target/calculator.jar"]
