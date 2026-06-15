pipeline {
    agent any

    triggers {
        // Periodically check for updates from Git (Poll SCM every 5 minutes)
        pollSCM('*/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                // SCM checkout is automatic in Jenkins Pipelines, but we can verify it
                echo "Checking out latest updates..."
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    echo "Auto building and testing the Spring Boot application..."
                    if (isUnix()) {
                        // Make Maven wrapper executable and run tests
                        sh 'chmod +x mvnw'
                        sh './mvnw clean test'
                    } else {
                        // On Windows, run the Maven wrapper cmd
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Deploy via Ansible') {
            steps {
                script {
                    echo "Tests successful. Deploying to Web Server using Ansible Playbook..."
                    if (isUnix()) {
                        dir('ansible') {
                            sh 'bash run-ansible.sh'
                        }
                    } else {
                        dir('ansible') {
                            // Run PowerShell deployment script on Windows agent
                            powershell 'powershell.exe -ExecutionPolicy Bypass -File run-ansible.ps1'
                        }
                    }
                }
            }
        }
    }

    post {
        failure {
            script {
                echo "Build or deployment failed! Sending email notifications..."
                // Send CC email to srengty@gmail.com and also to the developer who committed the error
                emailext (
                    subject: "Build Failed: Job '${env.JOB_NAME}' [Build #${env.BUILD_NUMBER}]",
                    body: """<h3>Build / Deployment Error Detected</h3>
                             <p>The Jenkins build for project <b>${env.JOB_NAME}</b> (Build #${env.BUILD_NUMBER}) has failed.</p>
                             <p>Check the console log for errors here: <a href="${env.BUILD_URL}console">${env.BUILD_URL}console</a></p>""",
                    to: 'srengty@gmail.com',
                    recipientProviders: [culprits(), developers()]
                )
            }
        }
        success {
            echo "Build and deployment finished successfully!"
        }
    }
}
