pipeline {
    agent {
        label 'node-1'
    }
    stages {
        stage('Pull') {
            steps {
                git branch: 'main', url: 'https://github.com/SurajBele/studentdata.git'
                echo "pulling successfully!"
            }
        }
        stage('Building') {
            steps {
                sh 'mvn clean package'
                echo "building successfully!"
            }
        }

        stage('Test') {
            steps {
                withSonarQubeEnv(installationName: 'sonar-server', credentialsId: 'sonar-token') {
                  sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=myproject'
                }
                echo "testing successfully!"
            }
        }

        stage('QualityGate') {
            steps {
                waitForQualityGate abortPipeline: false, credentialsId: 'sonar-secret-key'
                echo "qulity gate check successfully!"
            }
        }

        stage('Deploy') {
            steps {
                deploy adapters: [tomcat9(credentialsId: 'tomcat-pass', path: '', url: 'http://65.0.73.96:8080/')], contextPath: '/', war: '**/*.war'
                echo "deploy successfully!"
            }
        }
    }
}