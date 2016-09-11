node {
   stage 'Checkout'
   checkout scm

   stage 'Build'
   sh "chmod u+x gradlew"
   sh "./gradlew clean build"
}

