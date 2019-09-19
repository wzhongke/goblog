---
title: jenkins
---
# jenkins 启动
```sh
docker run -d -p 8080:8080 -p 50000:50000 -v ~/.ssh:/root/.ssh -v /var/jenkins_home:/var/jenkins_home -v /etc/localtime:/etc/localtime -v /var/run/docker.sock:/var/run/docker.sock jenkinsci/blueocean
```

# pipeline

## agent docker 
```groovy
agent {
    docker {
        image: 'node'
        args: ''
    }
}
```

```groovy
#!groovy
pipeline {
    agent {node {label 'master'}}
    environment {
        PATH="/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin"
        FORMATTED_VERSION = VersionNumber([
            projectStartDate: '1970-01-01', 
            versionNumberString: '${BUILD_DATE_FORMATTED, "yyyyMMdd"}.${Publish_Type}.${BUILDS_TODAY}', 
            versionPrefix: '', 
            worstResultForIncrement: 'NOT_BUILT'
        ])
    }

    parameters {
        choice(
            choices: 'req\nzhong\njin',
            description: '发布类型：\nreq->例行\nzhong->重大\njin->紧急',
            name: 'Publish_Type'
        )
        
        text(
            name: 'Publish_Comment', 
            defaultValue: '', 
            description: 'Enter some information about TAG'
        )
    }

    stages {
        stage('env') {
            steps {
                sh 'id'
                sh 'whoami'
                sh 'pwd'
            }
        }
        stage("create git tag") {
            steps{
                wrap([$class: 'BuildUser']) {
                        git branch: 'master', credentialsId: 'wangzhongke', url: 'https://git.sogou-inc.com/wangzhongke/umis_front.git'
                        sh '''
                            git checkout master
                            git pull origin master
                            npm install --prodcution
                            git add -A
                            echo 'add'
                            git commit -m "version: $FORMATTED_VERSION"
                            echo "$FORMATTED_VERSION"
                            echo "$BUILD_USER_ID"
                            git tag -a $FORMATTED_VERSION -m "Tagged by Jenkins \nBuild:$FORMATTED_VERSION \n User:$BUILD_USER_ID \n Comment:$Publish_Comment"
                            git push origin $FORMATTED_VERSION
                            git tag -l
                        '''
                }
            }
        }
    }
    
   post {
        always {
            echo 'status'
        }
    }
}
```