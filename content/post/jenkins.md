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


# agent
```js
/** 声明性Pipeline
   > 没有分号作为语句分隔符。每个声明必须在自己的一行
 */
pipeline {
    /**该agent部分指定整个Pipeline或特定阶段将在Jenkins环境中执行的位置，
        具体取决于该agent 部分的放置位置。该部分必须在pipeline块内的顶层定义 ，
        但阶段级使用是可选的。 */
    agent any // 在任何可用的代理上执行 Pipeline
    agent none // 不会为整个Pipeline运行全局代理，每个 stage 需要包含自己的 agent
    agent {
        // 使用提供的标签在 Jenkins 环境可用的代理上执行 Pipeline
        label 'my-defined-label'
    }
    agent {
        node { 
            label 'labelName0' 
            // customWorkspace 在 node，docker 下可用
            customWorkspace '/some/other/path'
        }
    }
    agent {
        // args 参数直接传递给 docker run 调用的参数
        docker {
            // 在给定名称和tag的新创建的容器中执行Pipeline定义的步骤
            image 'node',
            label 'my-defined-label'
            args '-v /tmp:/tmp'
        }
    }
}
```

# post
post部分定义将在Pipeline运行或阶段结束时运行的操作。
一些条件后 的块的内支持post：部分 always，changed，failure，success，unstable，和aborted。这些块允许在Pipeline运行或阶段结束时执行步骤，具体取决于Pipeline的状态。

条件如下：
- always 无论Pipeline运行的完成状态如何都运行。
- changed 只有当前Pipeline运行的状态与先前完成的Pipeline的状态不同时，才能运行。
- failure 仅当当前Pipeline处于“失败”状态时才运行，通常在Web UI中用红色指示表示。
- success 仅当当前Pipeline具有“成功”状态时才运行，通常在具有蓝色或绿色指示的Web UI中表示。
- unstable 只有当前Pipeline具有“不稳定”状态，通常由测试失败，代码违例等引起，才能运行。通常在具有黄色指示的Web UI中表示。
- aborted 只有当前Pipeline处于“中止”状态时，才会运行，通常是由于Pipeline被手动中止。通常在具有灰色指示的Web UI中表示。

```js
pipeline {
    agent any
    stages {
        stage('Example') {
            steps {
                echo 'Hello World'
            }
        }
    }
    // 通常 post 部分放在 Pipeline 末端
    post { 
        always { 
            echo 'I will always say Hello again!'
        }
    }
}
```

# steps
包含一个或多个阶段指令的序列，是pipeline描述大部分工作的位置，只能在 pipeline 中出现一次

```js
pipeline {
    agent any
    stages {
        stage('Example') {
            steps { 
                echo 'Hello World'
            }
        }
    }
}
```

# environment
environment 指令指定一系列键值对，这些键值对是所有步骤的环境变量或特定步骤，具体取决于 environment 在 pipeline 中的位置。
```js
pipeline {
    agent any
    environment { 
        CC = 'clang'
    }
    stages {
        stage('Example') {
            environment { 
                AN_ACCESS_KEY = credentials('my-prefined-secret-text') 
            }
            steps {
                sh 'printenv'
            }
        }
    }
}
```

- environment顶级pipeline块中使用的指令将适用于Pipeline中的所有步骤
- 在一个environment意图中定义的一个指令stage将仅将给定的环境变量应用于该过程中的步骤stage
- 该environment块具有一个帮助方法credentials()，可用于在Jenkins环境中通过其标识符访问预定义的凭据


# options
`options` 指令是 pipeline 本身内配置的专用选项：
- buildDiscarder: 持久化工件和控制台输出，用于最近Pipeline运行的具体数量。例如：`options { buildDiscarder(logRotator(numToKeepStr: '1')) }`
- disableConcurrentBuilds: 不允许并行执行Pipeline。可用于防止同时访问共享资源等。例如：`options { disableConcurrentBuilds() }`
- skipDefaultCheckout: 在 `agent` 指令中默认跳过来自源代码控制的代码。例如：`options { skipDefaultCheckout() }`
- skipStagesAfterUnstable: 一旦构建状态进入了“不稳定”状态，就跳过阶段。例如：`options { skipStagesAfterUnstable() }`
- timeout: 设置Pipeline运行的超时时间，之后Jenkins应该中止Pipeline。例如：`options { timeout(time: 1, unit: 'HOURS') }`
- timestamps: 预处理由Pipeline生成的所有控制台输出运行时间与发射线的时间。例如：`options { timestamps() }`

# 参数 `parameters`
`parameters` 指令提供用户在触发 Pipeline 时提供的参数列表
可用参数：
- 串：字符串类型的参数，如： `parameters { string(name: 'DEPLOY_ENV', defaultValue: 'staging', description: '') }`
- booleanParam: 布尔参数，如：`parameters { booleanParam(name: 'DEBUG_BUILD', defaultValue: true, description: '') }`

```js
pipeline {
    agent any
    parameters {
        string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
    }
    stages {
        stage('Example') {
            steps {
                echo "Hello ${params.PERSON}"
            }
        }
    }
}
```

# 触发器 triggers
`triggers` 指令定义了 Pipeline 应重新触发的自动化方式。
 
- cron: 接受一个 cron 风格的字符串来定义 Pipeline 应重新触发的间隔，如：`triggers { cron('H 4/* 0 0 1-5') }`
- pollSCM: 接受一个cron风格的字符串来定义Jenkins应该检查新的源更改的常规间隔。如果存在新的更改，则Pipeline将被重新触发。例如：`triggers { pollSCM('H 4/* 0 0 1-5') }`

# `stage`
`stage` 指令是在 `stages` 内部，包含步骤的部分。实际上 Pipeline 完成的所有实际工作都包含在一个或者多个 stage 指令中。

```js
pipeline {
    agent any
    stages {
        stage('Example') {
            steps {
                echo 'Hello World'
            }
        }
    }
}
```

# when
`when` 指令允许 Pipeline 根据给定的条件确定是否执行该阶段。
内置条件：
- branch: 当正在构建的分支与给出的分支模式匹配时执行阶段，例如：`when { branch 'master' }`。请注意，这仅适用于多分支Pipeline。
- environment: 当指定的环境变量设置为给定值时执行阶段，例如： `when { environment name: 'DEPLOY_TO', value: 'production' }`
- expression: 当指定 Groovy 表达式求值为 true 时执行阶段，例如：`when { expression { return params.DEBUG_BUILD } }`
- not: 嵌套条件为 false 时执行阶段，必须包含一个条件，例如：`when { not { branch 'master' } }`
- allOf: 当所有嵌套条件都为 true 时才执行，例如：`when { allOf { branch 'master'; environment name: 'DEPLOY_TO', value: 'production' } }`
- anyOf: 当至少一个嵌套条件为 true 时执行，例如：`when { anyOf { branch 'master'; branch 'staging' } }`

```js
stage('Example Deploy') {
    when {
        expression { BRANCH_NAME ==~ /(production|staging)/ }
        anyOf {
            environment name: 'DEPLOY_TO', value: 'production'
            environment name: 'DEPLOY_TO', value: 'staging'
        }
    }
    steps {
        echo 'Deploying'
    }
}
```

# script 
script 步骤需要一个 script pipeline，支持 Groovy 语言提供的大多数功能。

```js
script {
    def browsers = ['chrome', 'firefox']
    for (int i = 0; i < browsers.size(); ++i) {
        echo "Testing the ${browsers[i]} browser"
    }
}
```


```groovy
#!groovy
def changeLog = "";
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
                dir('wap_server') {
                    wrap([$class: 'BuildUser']) {
                           git branch: 'release/prod', credentialsId: '0a19ed85-97a0-4abc-bb3f-aed3456591b7', url: 'https://git.sogou-inc.com/ps-front/wap-front.git'
                           sh '''
                             git checkout release/prod
                             git pull origin release/prod
                             git fetch origin master
                             git merge origin/master
                             diff_package="$(git diff release/prod origin/release/prod)"
							 if  `echo "$diff_package" | grep -q "package"`; then
                                npm install --prodcution
                                git commit -am 'node_mudule change'
                             fi
                              sogou-changelog -o CHANGELOG.md  -t $FORMATTED_VERSION
                              sogou-changelog -t $FORMATTED_VERSION -r 1 > version.txt
                              git add -A
                              git commit -m "version: $FORMATTED_VERSION"
                              git push origin release/prod
                              git tag -a $FORMATTED_VERSION -m "Tagged by Jenkins \nBuild:$FORMATTED_VERSION \n User:$BUILD_USER_ID \n Comment:$Publish_Comment"
                              git push origin $FORMATTED_VERSION
                              git tag -l
                           '''
							script {
							    changeLog = readFile("version.txt").trim()
							    echo "changelog--> ${changeLog}"
							    currentBuild.getChangeSets().clear()
						        git branch: 'release/prod', credentialsId: '0a19ed85-97a0-4abc-bb3f-aed3456591b7', url: 'https://git.sogou-inc.com/ps-front/wap-front.git'
                          
						    }
                    }
                }
            }
        }
    }
    
   post {
        always {
                wrap([$class: 'BuildUser']) {
                    emailext(
                        attachLog: true, body:[ 
                            "构建信息",
                            "项目: \$PROJECT_NAME",
                            "启动用户: ${BUILD_USER_ID}",
                            "版本:  ${FORMATTED_VERSION}",
                            "状态: \$BUILD_STATUS",
                            "可以在页面 \$BUILD_URL 的控制台查看结果.",
                            "新版本更改内容:\n ${changeLog}"
                        ].join('\n'), 
                        postsendScript: "\$DEFAULT_POSTSEND_SCRIPT", 
                        presendScript: "\$DEFAULT_PRESEND_SCRIPT", 
                        recipientProviders: [developers(), requestor()], 
                        to: "ps_front@sogou-inc.com",
                        subject: "项目[\$PROJECT_NAME] - $BUILD_USER_ID 构建 # $FORMATTED_VERSION  - \$BUILD_STATUS!"
                    )
                } 
        }
    }
}
``` 