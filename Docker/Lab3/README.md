# Lab 3: Creating a Docker Image for a Java Application

Application: [CalculatorMavenApp](https://github.com/Ibrahim-Adel15/CalculatorMavenApp) (Java 17, Maven console app).

## Part 1: Build Inside Docker
- Base image: `maven:3.9-eclipse-temurin-17`
- Non-root user `appuser`, working directory `/app`
- Source copied, then built inside the image with `mvn package`
- Image: `app1` - **806MB**
- Run: `docker run -it --name container1 app1` (verified output, e.g. `Divide: 5.0`), then removed container and image.

## Part 2: Build Locally
- JAR built locally with Maven (`mvn package`) -> `target/calculator.jar`
- Base image: `eclipse-temurin:17-jre-alpine`
- Non-root user `appuser`, working directory `/app`, only the JAR copied
- Image: `app2` - **263MB**
- Run: `docker run -it --name container2 app2` (verified output), then removed container and image.

> The app is a console application (reads input with `Scanner`), so `EXPOSE 8080` is documentation only; run with `-it`.

## Bonus: Size Comparison

| | app1 (build inside Docker) | app2 (build locally) |
|---|---|---|
| Image size | 806MB | 263MB |
| Base image | Maven + full JDK (Ubuntu) | JRE only (Alpine) |
| Contains | Maven, JDK, source, `.m2` dependencies, `target/` | Only the runnable JAR |

**Why app2 is ~3x smaller:** it ships a JRE instead of a JDK, uses minimal Alpine Linux, and contains only the final JAR, with no build tools, source code or downloaded dependencies.

**Trade-offs:** app1 needs only Docker on the machine and gives a reproducible build, but the image is large and includes build tools. app2 is small and has a smaller attack surface, but requires Maven and Java on the build machine. A multi-stage build combines both advantages.

## Screenshots
_Add




cd ~/DevOps-Labs/Docker/Lab3

cat > README.md << 'EOF'
# Lab 3: Creating a Docker Image for a Java Application

Application: [CalculatorMavenApp](https://github.com/Ibrahim-Adel15/CalculatorMavenApp) (Java 17, Maven console app).

## Part 1: Build Inside Docker
- Base image: `maven:3.9-eclipse-temurin-17`
- Non-root user `appuser`, working directory `/app`
- Source copied, then built inside the image with `mvn package`
- Image: `app1` - **806MB**
- Run: `docker run -it --name container1 app1` (verified output, e.g. `Divide: 5.0`), then removed container and image.

## Part 2: Build Locally
- JAR built locally with Maven (`mvn package`) -> `target/calculator.jar`
- Base image: `eclipse-temurin:17-jre-alpine`
- Non-root user `appuser`, working directory `/app`, only the JAR copied
- Image: `app2` - **263MB**
- Run: `docker run -it --name container2 app2` (verified output), then removed container and image.

> The app is a console application (reads input with `Scanner`), so `EXPOSE 8080` is documentation only; run with `-it`.

## Bonus: Size Comparison

| | app1 (build inside Docker) | app2 (build locally) |
|---|---|---|
| Image size | 806MB | 263MB |
| Base image | Maven + full JDK (Ubuntu) | JRE only (Alpine) |
| Contains | Maven, JDK, source, `.m2` dependencies, `target/` | Only the runnable JAR |

**Why app2 is ~3x smaller:** it ships a JRE instead of a JDK, uses minimal Alpine Linux, and contains only the final JAR, with no build tools, source code or downloaded dependencies.

**Trade-offs:** app1 needs only Docker on the machine and gives a reproducible build, but the image is large and includes build tools. app2 is small and has a smaller attack surface, but requires Maven and Java on the build machine. A multi-stage build combines both advantages.

## Screenshots
_Add screenshots here._
