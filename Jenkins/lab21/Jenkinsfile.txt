
pipeline {
    agent any

    environment {
        IMAGE = 'sophiabahaaboshra/demoapp'
        TAG = "${BUILD_NUMBER}"
        K8S_SERVER = 'https://192.168.198.137:6443'
    }

    stages {

        stage('Build App') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $IMAGE:$TAG .'
            }
        }

        stage('Scan Docker Image') {
            steps {
                sh 'docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v trivy-cache:/root/.cache/ aquasec/trivy:latest image --db-repository ghcr.io/aquasecurity/trivy-db:2 --java-db-repository ghcr.io/aquasecurity/trivy-java-db:1 --scanners vuln --timeout 30m --severity HIGH,CRITICAL --exit-code 0 $IMAGE:$TAG'
            }
        }

        stage('Push Image to DockerHub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DH_USER',
                        passwordVariable: 'DH_PASS'
                    )
                ]) {
                    sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
                    sh 'docker push $IMAGE:$TAG'
                }
            }
        }

        stage('Delete Image Locally') {
            steps {
                sh 'docker rmi $IMAGE:$TAG || true'
            }
        }

        stage('Update Deployment File') {
            steps {
                sh 'sed -i "s|image: .*|image: $IMAGE:$TAG|" deployment.yaml'
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'k8s-token',
                        variable: 'K8S_TOKEN'
                    )
                ]) {
                    sh 'kubectl --server=$K8S_SERVER --token=$K8S_TOKEN --insecure-skip-tls-verify apply -f deployment.yaml'

                    sh 'kubectl --server=$K8S_SERVER --token=$K8S_TOKEN --insecure-skip-tls-verify rollout status deployment/demoapp -n ivolve --timeout=180s'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }

        success {
            echo 'Pipeline succeeded.'
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}
