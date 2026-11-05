def call(Map config) {
    def img = "${config.image}:${config.tag}"
    sh "docker build -t ${img} ."
    sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v trivy-cache:/root/.cache/ aquasec/trivy:latest image --db-repository ghcr.io/aquasecurity/trivy-db:2 --java-db-repository ghcr.io/aquasecurity/trivy-java-db:1 --scanners vuln --timeout 30m --severity HIGH,CRITICAL --exit-code 0 ${img}"
    withCredentials([usernamePassword(credentialsId: config.credentialsId, usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
        sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
        sh "docker push ${img}"
    }
    sh "docker rmi ${img} || true"
}
