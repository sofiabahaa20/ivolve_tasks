def call(Map config) {
    sh "sed -i 's|image: .*|image: ${config.image}:${config.tag}|' deployment.yaml"
    withCredentials([string(credentialsId: config.tokenId, variable: 'K8S_TOKEN')]) {
        sh "kubectl --server=${config.server} --token=\$K8S_TOKEN --insecure-skip-tls-verify apply -f deployment.yaml"
        sh "kubectl --server=${config.server} --token=\$K8S_TOKEN --insecure-skip-tls-verify rollout status deployment/demoapp -n ivolve --timeout=180s"
    }
}
