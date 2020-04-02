pipeline {
  agent any
  stages {
    stage('Build-Android') {
      when {
        branch 'master'
      }
      steps {
        sh 'cd app && ./gradlew build'
      }
    }
    stage('Build-Spring') {
      when {
        branch 'master'
      }
      steps {
        sh 'cd spring && ./mvnw compiler:compile'
      }
    }
    stage('Test-Android') {
      when {
        branch 'master'
      }
      steps {
        sh 'cd app && ./gradlew test'
      }
    }
    stage('Test-Spring') {
      when {
        branch 'master'
      }
      steps {
        sh 'cp spring/.env.template spring/.env'
        sh 'cd spring && ./runapp.sh test'
      }
    }
    stage('Deploy-Production') {
      when {
        branch 'master'
      }
      steps {
        echo 'Building War file for production ...'
        sh 'spring/runapp.sh -Dmaven.test.skip=true package'
        sh 'sudo cp spring/target/*.war /home/ordy/ordy/production/app.war'
        sh 'sudo systemctl restart ordy.service'
      }
    }
  }
  post {
    always {
      sh 'rm -rf .[!.]* *'
    }
  }
}
