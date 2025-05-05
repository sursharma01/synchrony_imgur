pipeline {
    agent any

    environment {
        IMAGE_NAME = "yourappname"
        DOCKER_REGISTRY = "your-docker-registry.com"
        VERSION = "v1.0.${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set up JDK and Maven') {
            steps {
                echo "Setting up JDK 17 and Maven"
                tool name: 'jdk17', type: 'jdk'
                tool name: 'maven-3.8.6', type: 'maven'
            }
        }

        stage('Build') {
            steps {
                echo "Building Spring Boot app..."
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo "Running unit tests..."
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                echo "Packaging application..."
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            when {
                branch 'main'
            }
            steps {
                echo "Running SonarQube scan..."
                withSonarQubeEnv('SonarQubeServer') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build & Push') {
            when {
                branch 'main'
            }
            steps {
                echo "Building Docker Image..."
                sh """
                    docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION} .
                    docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION}
                """
            }
        }

        stage('Deploy to Dev') {
            when {
                branch 'main'
            }
            steps {
                echo "Deploying to Dev Environment..."
                // added suggestion: we can also replace this with kubernetes, or, ecs deploy scripts
                    sh "./deploy/deploy-dev.sh ${VERSION}"
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
            slackSend channel: '#ci-cd-alerts', color: 'good', message: "Build Success: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
        }
        failure {
            echo 'Pipeline failed!'
            slackSend channel: '#ci-cd-alerts', color: 'danger', message: "Build Failed: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
        }
        always {
            cleanWs()
        }
    }
}