pipeline {
    agent any  // Use any available agent to run the pipeline

    environment {
        ARTIFACTORY_JAR_URL = 'http://cvwjeemys6:8082/artifactory/libs-release-local/com/infy/TaxTracker-group7/1/TaxTracker-group7-1.jar'
        JAR_NAME = 'TaxTracker-group7-1.jar'        
        ARTIFACTORY_USER = 'admin'      
        ARTIFACTORY_PASS = 'Capstone@123'  

    }

    stages {
        stage('Download Backend JAR from Artifactory') {
            steps {
                echo 'Downloading backend JAR from Artifactory...'
                 bat "curl -u %ARTIFACTORY_USER%:%ARTIFACTORY_PASS% -o %JAR_NAME% %ARTIFACTORY_JAR_URL%"
            }
        }

        stage('Start Backend') {
            steps {
                echo 'Starting backend service...'
                // Start the backend service using the downloaded JAR file
                // 'start' runs it in a new command window (non-blocking)
                // Using port 2000 to match the expected configuration
                 bat "start java -Dserver.port=2000 -jar ${JAR_NAME}"
            }
        }

        stage('Trigger Frontend CD') {
            steps {
                echo 'Triggering frontend CD pipeline...'
                // Trigger another Jenkins pipeline named 'infymovie_react_cd/infymovie_angular_cd'
                // 'wait: true' ensures this pipeline waits for the triggered job to finish
                // build job: 'infymovie_react_cd', wait: true
                build job: 'taxtracker_frontend_group7', wait: true
                
            }
        }
    }

    post {
        success {
            // Message shown when the pipeline completes successfully.
            echo '✅ Backend deployment completed!'
        }
        failure {
            // Message shown if any stage fails
            echo '❌ Backend deployment failed.'
        }
    }
}