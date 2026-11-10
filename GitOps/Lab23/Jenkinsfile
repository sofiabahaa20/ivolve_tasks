@Library('shared-lib') _

pipeline {
    agent { label 'agent-1' }

    environment {
        IMAGE = 'sophiabahaaboshra/demoapp'
        TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Build App') {
            steps {
                BuildApp()
            }
        }

        stage('Build Image') {
            steps {
                BuildImage(
                    image: env.IMAGE,
                    tag: env.TAG,
                    credentialsId: 'dockerhub-creds'
                )
            }
        }

        stage('Update Manifest (GitOps)') {
            steps {
                DeployOnK8s(
                    image: env.IMAGE,
                    tag: env.TAG,
                    repo: 'github.com/sofiabahaa20/DemoApp.git',
                    gitCredentialsId: 'github-creds'
                )
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
