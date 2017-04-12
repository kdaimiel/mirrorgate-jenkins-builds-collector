#!groovy
JENKINS_PLUGIN_PACKAGE = "mirrorgate-publisher.hpi"

def mirrorGateBuildPublishStep(buildStatus) {

  def time = System.currentTimeMillis()

  sh """

    echo '{'    > _msg.json
    echo '    \"number\" : \"${env.BUILD_NUMBER}\",'    >> _msg.json
    echo '    \"timestamp\" : ${time},' >> _msg.json
    echo '    \"buildUrl\" : \"${env.BUILD_URL}\",'   >> _msg.json
    echo '    \"buildStatus\" : \"$buildStatus\",' >> _msg.json
    echo '    \"projectName\" : \"MirrorGate\",'  >> _msg.json
    echo '    \"repoName\" : \"mirrorgate-app\",' >> _msg.json
    echo '    \"branch\" : \"${env.BRANCH_NAME}\"'  >> _msg.json
    echo '}'    >> _msg.json

    cat _msg.json

    curl -H "Content-Type: application/json" -X POST -d @_msg.json http://internal-dev-mirrorgate-alb-internal-1778367606.eu-west-1.elb.amazonaws.com/mirrorgate/builds

  """
}

node ('internal-global') {
  try {

      mirrorGateBuildPublishStep ('InProgress')


      withCredentials([[$class: 'FileBinding', credentialsId: 'artifactory-maven-settings-global', variable: 'M3_SETTINGS']]) {
        sh 'mkdir .m3 || true'
        sh 'cp -f ${M3_SETTINGS} .m3/settings.xml'
      }
        
      withEnv(['CI=true']) {

        stage(' Checkout SCM ') {
          checkout(scm)
        }

        stage('API - Clean app') {
          sh """
            ./gradlew clean
          """
        }

        stage('API - Build app') {
          sh """
            ./gradlew build
          """
        }

        stage('API - Run tests') {
          sh """
            ./gradlew test jacocoTestReport
          """
        }

      }

      stage(' Publish app ') {
      	step([$class: "ArtifactArchiver", artifacts: "./build/libs/${JENKINS_PLUGIN_PACKAGE}", fingerprint: true])
      }
      
      stage(' Deploy to Jenkins ') {
      	if (env.BRANCH_NAME == "master") {
      	  withCredentials([[$class: 'UsernamePasswordMultiBinding',
                          credentialsId: 'bot-jenkins-ldap',
                          usernameVariable: 'JENKINS_USER',
                          passwordVariable: 'JENKINS_PWD']]){

            JENKINS_HOST="globaldevtools.bbva.com"
                
            // sh "curl ifconfig.co"
            sh "curl -i -F file=@build/libs/${JENKINS_PLUGIN_PACKAGE} https://${JENKINS_USER}:${JENKINS_PWD}@${JENKINS_HOST}/jenkins-api/pluginManager/uploadPlugin"
            // sh "curl -kX POST https://${JENKINS_USER}:${JENKINS_PWD}@${JENKINS_HOST}/safeRestart" 
         }
      	
        }else {
      	  withCredentials([[$class: 'UsernamePasswordMultiBinding',
                          credentialsId: 'bot-dev-jenkins-ldap',
                          usernameVariable: 'JENKINS_USER',
                          passwordVariable: 'JENKINS_PWD']]){
            JENKINS_HOST="dev.globaldevtools.bbva.com"

            sh "curl ifconfig.co"
            sh "curl -i -k -F file=@build/libs/${JENKINS_PLUGIN_PACKAGE} https://${JENKINS_USER}:${JENKINS_PWD}@${JENKINS_HOST}/jenkins-api/pluginManager/uploadPlugin"
            sh "curl -kX POST https://${JENKINS_USER}:${JENKINS_PWD}@${JENKINS_HOST}/safeRestart"
      	  }
        }
      }

      mirrorGateBuildPublishStep ('Success')

  } catch(Exception e) {
      sh """
      curl -X POST \
      -H 'Content-type: application/json' \
      --data '{
        \"attachments\":[
            {
              \"fallback\":\"${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - FAILURE!\",
              \"color\":\"#D00000\",
              \"fields\":[
                  {
                    \"title\":\"${env.JOB_NAME}\",
                    \"value\":\"<${env.BUILD_URL}|Build # ${env.BUILD_NUMBER} - FAILURE!>\",
                    \"short\":false
                  }
              ]
            }
        ]
      }' \
      https://hooks.slack.com/services/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
      """
      mirrorGateBuildPublishStep ('Failure')

      throw e;
  } 
}
