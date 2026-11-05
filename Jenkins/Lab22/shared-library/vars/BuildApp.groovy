def call() {
    sh 'mvn -B test'
    sh 'mvn -B clean package -DskipTests'
}
