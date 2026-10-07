pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'MAVEN_HOME'
    }

    environment {
        // TODO: replace with your Docker Hub username
        IMAGE = 'mazizbs/student-management'
        TAG   = "${BUILD_NUMBER}"
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                // TODO: replace with the ID of your GitHub credentials in Jenkins
                git branch: 'main',
                    credentialsId: 'github-creds',
                    url: 'https://github.com/Aziz-BenSaad/devops-project.git'
            }
        }

        stage('Unit tests') {
            steps {
                // Uses the in-memory H2 database from src/test/resources, no MySQL needed
                sh 'mvn clean test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        mvn sonar:sonar \
                          -Dsonar.projectKey=student-management \
                          -Dsonar.host.url=http://192.168.50.4:9000 \
                          -Dsonar.token=$SONAR_TOKEN \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    '''
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker image') {
            steps {
                sh 'docker build -t $IMAGE:$TAG -t $IMAGE:latest .'
            }
        }

        stage('Push Docker image') {
            steps {
                // TODO: replace with the ID of your Docker Hub credentials in Jenkins
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push $IMAGE:$TAG
                        docker push $IMAGE:latest
                        docker logout
                    '''
                }
            }
        }

        stage('Archive artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy on Kubernetes') {
            steps {
                // Once the manifests are copied into the repo's k8s/ folder,
                // these lines can become: kubectl apply -f k8s/ -n devops
                sh '''
                    kubectl apply -f /home/vagrant/hello-k8s/namespace.yaml
                    kubectl apply -f /home/vagrant/hello-k8s/mysql-deployment.yaml -n devops
                    kubectl apply -f /home/vagrant/hello-k8s/spring-configmap.yaml -n devops
                    kubectl apply -f /home/vagrant/hello-k8s/spring-secret.yaml -n devops
                    kubectl apply -f /home/vagrant/hello-k8s/spring-deployment.yaml -n devops
                    kubectl apply -f /home/vagrant/hello-k8s/spring-service.yaml -n devops
                    kubectl rollout restart deployment -n devops
                '''
            }
        }
    }

    post {
        success { echo 'Pipeline OK: application déployée sur Kubernetes' }
        failure { echo 'Pipeline KO' }
    }
}
