node {

   stage 'Checkout'
   checkout scm

   step([$class: 'GitHubCommitStatusSetter',
      contextSource: [$class: 'ManuallyEnteredCommitContextSource', context: 'Checkout'],
      statusResultSource: [$class: 'ConditionalStatusResultSource',
      results: [[$class: 'AnyBuildResult', message: 'Project was checked out', state: 'SUCCESS']]]])

   stage 'Build'
   sh "chmod u+x gradlew"
   sh "gradle clean build"

   step([$class: 'GitHubCommitStatusSetter',
      contextSource: [$class: 'ManuallyEnteredCommitContextSource', context: 'Build'],
      statusResultSource: [$class: 'ConditionalStatusResultSource',
      results: [[$class: 'AnyBuildResult', message: 'Project was built successfully!', state: 'SUCCESS']]]])
}

