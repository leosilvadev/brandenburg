node {

   stage 'Checkout'
   checkout scm

   step([$class: 'GitHubCommitStatusSetter',
      contextSource: [$class: 'ManuallyEnteredCommitContextSource', context: 'Checkout'],
      statusResultSource: [$class: 'ConditionalStatusResultSource',
      results: [[$class: 'AnyBuildResult', message: 'Project was checked out', state: 'SUCCESS']]]])

   stage 'Build'
   sh "chmod u+x gradlew"
   sh "./gradlew clean build"
}
