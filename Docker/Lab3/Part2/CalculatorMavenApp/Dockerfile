FROM eclipse-temurin:17-jre-alpine

# non-root user
RUN addgroup -S appgroup && adduser -S -G appgroup appuser

# working directory (owned by the non-root user)
WORKDIR /app
RUN chown appuser:appgroup /app

# copy the JAR built locally
COPY --chown=appuser:appgroup target/calculator.jar .

USER appuser

EXPOSE 8080

CMD ["java", "-jar", "calculator.jar"]
