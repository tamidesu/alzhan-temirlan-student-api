pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timeout(time: 20, unit: 'MINUTES')
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    environment {
        IMAGE_NAME = 'alzhan-temirlan-student-api'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.SOURCE_COMMIT = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                    env.APP_VERSION = sh(script: "sed -n 's/^version=//p' gradle.properties", returnStdout: true).trim()
                    env.COMMIT_TAG = "git-${env.SOURCE_COMMIT.take(12)}"
                    env.RELEASE_TAG = "v${env.APP_VERSION}-alzhan-temirlan-38372"
                }
                echo 'Student: Temirlan Alzhan; Group: IT2-2307; Student ID: 38372'
                echo "Source commit: ${env.SOURCE_COMMIT}"
            }
        }

        stage('Build') {
            steps {
                sh './gradlew --no-daemon clean assemble'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew --no-daemon test'
            }
            post {
                always {
                    junit testResults: 'build/test-results/test/*.xml', allowEmptyResults: false
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    set -eu
                    docker build --progress=plain \
                      --label "org.opencontainers.image.revision=$SOURCE_COMMIT" \
                      -t "$IMAGE_NAME:$COMMIT_TAG" \
                      -t "$IMAGE_NAME:$RELEASE_TAG" .
                '''
            }
        }

        stage('Verify') {
            steps {
                sh '''
                    set -eu
                    BUILT_COMMIT="$(docker image inspect "$IMAGE_NAME:$COMMIT_TAG" --format '{{ index .Config.Labels "org.opencontainers.image.revision" }}')"
                    test "$BUILT_COMMIT" = "$SOURCE_COMMIT"
                    docker image inspect "$IMAGE_NAME:$COMMIT_TAG" --format 'ID={{.Id}} Size={{.Size}} Created={{.Created}}'
                    echo "Verified $IMAGE_NAME:$COMMIT_TAG from $SOURCE_COMMIT"
                '''
            }
        }
    }
}
