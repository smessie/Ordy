pipeline {
  agent any
  stages {
    stage('Build-Android') {
      sh 'cd app && ./gradlew build'
    }
    stage('Build-Spring') {
      sh 'cd spring && ./mvnw compiler:compile'
    }
    stage('Test-Android') {
      sh 'cd app && ./gradlew test'
    }
    stage('Test-Spring') {
      sh 'cd spring && ./mvnw test'
    }
    stage('Deploy-Production') {
      when {
        branch 'master'
      }
      steps {
        echo 'Building War file for production ...'
        sh 'spring/runapp.sh war:war'
        sh 'sudo cp spring/target/*.war /home/ordy/ordy/production/app.war'
        sh 'sudo systemctl restart ordy.service'
      }
    }
    stage('Self Destruct') {
      steps {
        sh 'rm -rf .[!.]* *'
      }
    }
  }
}
