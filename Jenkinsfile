node {
   stage 'Checkout'
   checkout scm

   stage 'Build'
   sh "chmod u+x gradlew"
   sh "./gradlew clean build"

   publishHTML (target: [
      allowMissing: false,
      alwaysLinkToLastBuild: false,
      keepAll: true,
      reportDir: "${env.WORKSPACE}/build/reports/tests/",
      reportFiles: 'index.html',
      reportName: "Test Report"
    ])
}
