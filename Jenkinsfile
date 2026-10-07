pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'MAVEN_HOME'
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Checkout from Git') {
            steps {
                git branch: 'master',
                    credentialsId: 'jenkins-token3',
                    url: 'https://github.com/Chyheeb/chihebMedini.git'
            }
        }

        stage('Clean target') {
            steps {
                dir('student-management') {
                    sh 'rm -rf target || true'
                }
            }
        }

        stage('Compile & Package') {
            steps {
                dir('student-management') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Docker image') {
            steps {
                dir('student-management') {
                    sh 'docker build -t chyheb2/student-management:latest .'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                dir('student-management') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            mvn sonar:sonar \
                              -Dsonar.projectKey=student-management \
                              -Dsonar.host.url=http://192.168.50.4:9000 \
                              -Dsonar.login=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Push Docker image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds1',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push chyheb2/student-management:latest
                        docker logout
                    '''
                }
            }
        }

        stage('Archive livrable') {
            steps {
                dir('student-management') {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Deploy on Kubernetes') {
            steps {
                sh '''
                    kubectl apply -f /home/vagrant/hello-k8s/namespace.yaml
                    kubectl apply -f /home/vagrant/hello-k8s/mysql-deployment.yaml -n devops
                    kubectl apply -f /home/vagrant/hello-k8s/spring-configmap.yaml -n devops
                    kubectl apply -f /home/vagrant/hello-k8s/spring-secret.yaml -n devops
                    kubectl apply -f /home/vagrant/hello-k8s/spring-deployment.yaml -n devops
                    kubectl apply -f /home/vagrant/hello-k8s/spring-service.yaml -n devops
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline OK — application déployée sur Kubernetes'
        }
        failure {
            echo 'Pipeline KO'
        }
    }
}
