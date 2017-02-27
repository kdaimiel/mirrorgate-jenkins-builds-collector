#!groovy
JENKINS_PLUGIN_REPO = "ssh://git@globaldevtools.bbva.com:7999/bgdfm/jenkins_plugin_collector.git"
JENKINS_PLUGIN_DIR = "hygieia-jenkins-plugin"
JENKINS_PLUGIN_BASEDIR = "jenkins_plugin_collector"
HYGIEIA_BASEDIR = "hygieia" 
HYGIEIA_REPO = "https://github.com/capitalone/Hygieia.git"
JENKINS_PLUGIN_PACKAGE = "hygieia-publisher.hpi"

node ('global') {
  try {

      hygieiaBuildPublishStep buildStatus: 'InProgress'

      withCredentials([[$class: 'FileBinding', credentialsId: 'artifactory-maven-settings-global', variable: 'M2_SETTINGS']]) {
        //sh 'rm $WORKSPACE/.m2 -Rf'
        sh 'mkdir $WORKSPACE/.m2 || true'        
	    sh 'cp -f ${M2_SETTINGS} $WORKSPACE/.m2/settings.xml'
	    //sh 'cat $WORKSPACE/.m2/settings.xml'
      }

      stage('-------- Checkout SCM ---------') {

       dir (JENKINS_PLUGIN_BASEDIR) {
         checkout(scm)
       //	git url: "${JENKINS_PLUGIN_REPO}", branch: 'develop'
       }
       //sh "ls -la ${JENKINS_PLUGIN_BASEDIR}"
       //dir (HYGIEIA_BASEDIR) {
       // 	git url: "${HYGIEIA_REPO}", branch: 'master'
       // 	sh "rm ${JENKINS_PLUGIN_DIR} -Rf"
       // 	sh "mkdir ${JENKINS_PLUGIN_DIR}"
       // 	sh "cp ${WORKSPACE}/${JENKINS_PLUGIN_BASEDIR}/* ${WORKSPACE}/${HYGIEIA_BASEDIR}/${JENKINS_PLUGIN_DIR} -R"
       // 	sh "sed -i '/<module>collectors*\$/d' pom.xml"
       // 	sh "sed -i '/<module>UI*\$/d' pom.xml"
       //}
      }

      //stage('---------- Compile Hygieia Core -----------') {
      //	dir (HYGIEIA_BASEDIR) {
      //   sh "cd core; mvn clean install"
      //  }
      //}

      stage('----------- Build app -----------') {
        withMaven(mavenLocalRepo: '$WORKSPACE/.m2/repository', mavenSettingsFilePath: '$WORKSPACE/.m2/settings.xml') {
          dir (JENKINS_PLUGIN_BASEDIR) {
            sh "ls -la *"
            sh "mvn test"
            sh "mvn clean package"
          }
		}        
      }

      stage('------------ Publish app -----------') {
      	step([$class: "ArtifactArchiver", artifacts: "${JENKINS_PLUGIN_BASEDIR}/target/${JENKINS_PLUGIN_PACKAGE}", fingerprint: true])
      }
      
      hygieiaBuildPublishStep buildStatus: 'Success'

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