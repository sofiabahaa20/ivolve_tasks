# Lab 2: Building and Packaging a Java Application with Gradle

## Objective
Build, test, package and run a Java calculator app using Gradle.

## Environment
- RHEL-based VM (VMware)
- Docker 29.8.1
- Gradle 8 + JDK 17 (via Docker containers)

## Steps

### 1. Clone source code
```bash
docker run --rm -v "$PWD":/git alpine/git clone https://github.com/Ibrahim-Adel15/CalculatorGradleApp.git
```

### 2. Run unit tests
```bash
docker run --rm --user root -e GRADLE_USER_HOME=/root/.gradle -v "$PWD":/app -v ~/.gradle-docker:/root/.gradle -w /app gradle:8-jdk17 gradle test
```
Result: `BUILD SUCCESSFUL`.

![Tests](screenshots/test.png)

### 3. Build the app
The jar name included the version (`calculator-1.0.0.jar`), so `archiveVersion = ''` was added to the `jar` block in `build.gradle` to match the required name.
```bash
docker run --rm --user root -e GRADLE_USER_HOME=/root/.gradle -v "$PWD":/app -v ~/.gradle-docker:/root/.gradle -w /app gradle:8-jdk17 gradle clean build
```
Artifact generated: `build/libs/calculator.jar`.

![Build](screenshots/build.png)

### 4. Run the app
```bash
echo -e "10\n5" | docker run --rm -i -v "$PWD":/app -w /app eclipse-temurin:17-jre java -jar build/libs/calculator.jar
```

### 5. Verify
Output with inputs 10 and 5:
```
Sum: 15.0
Subtract: 5.0
Multiply: 50.0
Divide: 2.0
```
All results are correct.

![Run](screenshots/run.png)

## Maven vs Gradle

| | Maven | Gradle |
|---|---|---|
| Config file | `pom.xml` (XML) | `build.gradle` (Groovy) |
| Test command | `mvn test` | `gradle test` |
| Build command | `mvn clean package` | `gradle clean build` |
| Artifact path | `target/calculator.jar` | `build/libs/calculator.jar` |
