---
title: spark
draft: true

---

## Spark简介
Spark是什么？
- Spark是一个快速且通用的集群计算平台
- Spark扩充了Mapreduce计算模型
- Spark是基于内存的计算
- 批处理，迭代式计算，交互查询和流处理
- Spark提供了Python，java，Scala，SQL
- 跟其他工具能很好整合

## Spark安装
如果有Hadoop，需要下载与Hadoop相兼容的版本
目录分布如下：
- bin包含用来和Spark交互的可执行文件
- conf包含配置文件
- core, streaming, python,... 包含组件的源代码
- examples包含了一些单机的例子
### 调整日志级别
修改`conf/log4j.properties`的`INFO`为`WARN`

## Spark 开发环境搭建
1. 安装Scala：版本需要匹配 Spark 1.6.2 - Scala 2.10 Spark 2.0.0 - Scala 2.11
2. 在 idea上安装 Scala插件，使用SBT打包

```shell
ssh -keygen
cd ~/.ssh
touch authorized_keys
cat id_rsa.pub > authorized_keys
chmod 600 authorizd_keys
```
## 开发Spark程序

```scala
object WordCount {
    def main (args: Array[String]) {
        val conf = new SparkCOnf().setAppName("wordcount")
        val sc = new SparkContext(conf)
        val input = sc.textFile("./helloSpark.txt")
        val lines = input.flatMap(line => line.split(" "))
        val count = lines.map(word => (word, 1)).reduceByKey{ case (x,y) => x+y}

        val output = count.saveAsTextFile("./helloSparkResult")
    }
}
```
### 打包
project struct -> 
build -> build artifact

### 启动集群
1. 启动master ./sbin/start-master.sh
2. 启动worker ./bin/spark-class org.apache.spark.deploy.worker.Worker spark://localhost.localdomain:7077
3. 提交作业  ./bin/spark-submit --master localhost.localdomain:7077 --class WordCount ./imoocpro.jar 