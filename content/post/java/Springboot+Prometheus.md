---
title: Springboot2 + Prometheus 监控
---

从 **SpringBoot 2.0** 开始，[Micrometer](https://micrometer.io/) 就成为默认的指标输出引擎。 Micrometer 是一种支持多种检测系统的应用，像 Atlas，Datadog, Prometheus 等等。

# 配置SpringBoot
只需要在依赖中加入 SpringBoot Actuator 和 micrometer，Spring Boot 就会自动装配一个 MeterRegistry，并且把在 classpath 中找到的支持的实现都注册到 MeterRegistry 中。

在 `pom.xml` 中加入如下依赖：
```xml
<!-- Spring boot actuator to expose metrics endpoint -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!-- https://mvnrepository.com/artifact/io.micrometer/micrometer-core -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
    <version>1.0.4</version>
</dependency>
 <!-- https://mvnrepository.com/artifact/io.micrometer/micrometer-registry-prometheus -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <version>1.0.4</version>
</dependency>
```

然后在 `application.properties` 配置文件中加入如下配置：
```
# Metrics related configurations
management.endpoint.metrics.enabled=true
management.endpoints.web.exposure.include=*
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true
```

启动应用程序后，使用浏览器访问 http://localhost:8080/actuator，可以看到如下的端点：
```json
{
    ...,
    "prometheus": {
        "href": "http://localhost:8080/actuator/prometheus",
        "templated": false
    },
    ...
}
```

在浏览器中访问 http://localhost:8080/actuator/prometheus，可以看到应用程序的一些指标。
```
# HELP system_cpu_usage The "recent cpu usage" for the whole system
# TYPE system_cpu_usage gauge
system_cpu_usage{application="MYAPPNAME",} 0.08734939759036145
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{application="MYAPPNAME",area="nonheap",id="Code Cache",} 2.4843776E7
jvm_memory_used_bytes{application="MYAPPNAME",area="nonheap",id="Metaspace",} 5.107712E7
jvm_memory_used_bytes{application="MYAPPNAME",area="nonheap",id="Compressed Class Space",} 6436928.0
jvm_memory_used_bytes{application="MYAPPNAME",area="heap",id="Par Eden Space",} 2.32902576E8
jvm_memory_used_bytes{application="MYAPPNAME",area="heap",id="Par Survivor Space",} 2.3072776E7
jvm_memory_used_bytes{application="MYAPPNAME",area="heap",id="CMS Old Gen",} 4.85477784E8
# HELP tomcat_servlet_request_max_seconds  
# TYPE tomcat_servlet_request_max_seconds gauge
...
```

# 配置 Prometheus
在 `/tmp` 目录下新建文件 `prometheus.yml`，并写入如下配置：
```yml
#Global configurations
global:
    scrape_interval:     5s # Set the scrape interval to every 5 seconds.
    evaluation_interval: 5s # Evaluate rules every 5 seconds.
scrape_configs:
    - job_name: 'person-app'
        metrics_path: '/actuator/prometheus'
        static_configs:
        - targets: ['localhost:8080'] # 写入机器端口
```

然后通过 docker 执行：`docker run -p 9090:9090 -v /tmp/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus`，就可以启动 prometheus。

通过浏览器访问 http://localhost:9090 就可以看到监控。