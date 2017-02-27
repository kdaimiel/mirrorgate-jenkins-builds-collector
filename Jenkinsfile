#!groovy
JENKINS_PLUGIN_REPO = "ssh://git@globaldevtools.bbva.com:7999/bgdfm/jenkins_plugin_collector.git"
JENKINS_PLUGIN_DIR = "hygieia-jenkins-plugin"
JENKINS_PLUGIN_BASEDIR = "jenkins_plugin_collector"
HYGIEIA_BASEDIR = "hygieia" 
HYGIEIA_REPO = "https://github.com/capitalone/Hygieia.git"
JENKINS_PLUGIN_PACKAGE = "hygieia-publisher.hpi"
JENKINS_HOST="globaldevtools.bbva.com"

node ('global') {
  try {

      hygieiaBuildPublishStep buildStatus: 'InProgress'

      withCredentials([[$class: 'FileBinding', credentialsId: 'artifactory-maven-settings-global', variable: 'M2_SETTINGS']]) {
        sh 'mkdir $WORKSPACE/.m2 || true'        
	    sh 'cp -f ${M2_SETTINGS} $WORKSPACE/.m2/settings.xml'
      }

      stage(' Checkout SCM ') {

       dir (JENKINS_PLUGIN_BASEDIR) {
         checkout(scm)
       }
      }

      stage(' Build app ') {
        withMaven(mavenLocalRepo: '$WORKSPACE/.m2/repository', mavenSettingsFilePath: '$WORKSPACE/.m2/settings.xml') {
          dir (JENKINS_PLUGIN_BASEDIR) {
            sh "mvn test"
            sh "mvn clean package"
          }
		}        
      }

      stage(' Publish app ') {
      	step([$class: "ArtifactArchiver", artifacts: "${JENKINS_PLUGIN_BASEDIR}/target/${JENKINS_PLUGIN_PACKAGE}", fingerprint: true])
      }
      
      hygieiaBuildPublishStep buildStatus: 'Success'

      stage(' Deploy to Jenkins ') {
      	withCredentials([[$class: 'UsernamePasswordMultiBinding',
                          credentialsId: 'bot-jenkins-ldap',
                          usernameVariable: 'JENKINS_USER',
                          passwordVariable: 'JENKINS_PWD']]){

      	  dir (JENKINS_PLUGIN_BASEDIR) {
      	    sh "curl -i -F file=@target/${JENKINS_PLUGIN_PACKAGE} https://${JENKINS_USER}:${JENKINS_PWD}@${JENKINS_HOST}/jenkins-api/pluginManager/uploadPlugin"
      	    //sh "curl -kX POST https://${JENKINS_USER}:${JENKINS_PWD}@${JENKINS_HOST}/safeRestart"
      	  }
      	}
      }

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

      hygieiaBuildPublishStep buildStatus: 'Failure'

      throw e;
  } 
}