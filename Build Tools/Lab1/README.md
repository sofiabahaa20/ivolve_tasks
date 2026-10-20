# Lab 1: Building and Packaging a Java Application with Maven

## Objective
Build, test, package and run a Java calculator app using Maven.

## Environment
- RHEL-based VM (VMware)
- Docker 29.8.1
- Maven 3.9 + Java 17 (via Docker containers)

## Steps

### 1. Clone source code
```bash
docker run --rm -v "$PWD":/git alpine/git clone https://github.com/Ibrahim-Adel15/CalculatorMavenApp.git
```

### 2. Run unit tests
```bash
docker run --rm -v "$PWD":/app -v ~/.m2:/root/.m2 -w /app maven:3.9-eclipse-temurin-17 mvn test
```
Result: `Tests run: 5, Failures: 0, Errors: 0` and `BUILD SUCCESS`.

![Tests](screenshots/test.png)

### 3. Build the app
```bash
docker run --rm -v "$PWD":/app -v ~/.m2:/root/.m2 -w /app maven:3.9-eclipse-temurin-17 mvn clean package
```
Artifact generated: `target/calculator.jar`.

![Build](screenshots/build.png)

### 4. Run the app
```bash
echo -e "10\n5" | docker run --rm -i -v "$PWD":/app -w /app eclipse-temurin:17-jre java -jar target/calculator.jar
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
