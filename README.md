## spring cloud 初始化项目

### 默认集成了以下组件

- nacos-config
- nacos-discovery
- openfeign
- sentinel
- mybatis-plus

### 配置说明
- 通用配置

```yaml
logging:
  level:
    com.example.demo: debug
  pattern:
    dateformat: HH:mm:ss:SSS
  file:
    path: "logs/${spring.application.name}"


knife4j:
  enable: true
  openapi:
    title: 项目初始化接口文档
    description: "项目初始化接口文档"
    version: v1.0.0
    group:
      default:
        group-name: default
        api-rule: package
        api-rule-resources:
          - com.lyw.springbootstarter.controller
```

默认开启 SQL OpenFeign Sentinel(本地数据源), 添加以下配置即可

```yaml
spring:
  application:
    name: spring-cloud-starter
    
  datasource:
    url: jdbc:mysql://${my.db.host}:3300/my_db?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
    username: ${my.db.username}
    password: ${my.db.password}
    driver-class-name: com.mysql.cj.jdbc.Driver

  cloud:
    #  openfeign配置 开启okhttp
    openfeign:
      okhttp:
        enabled: true
        
    #  sentinel配置 后台监控
    sentinel:
      http-method-specify: true
      transport:
        dashboard: localhost:8089

# OpenFeign的限流配置
feign:
  sentinel:
    enabled: true

# MybatisPlus配置
mybatis-plus:
  configuration:
    default-enum-type-handler: com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler
  global-config:
    db-config:
      update-strategy: not_null
      id-type: auto
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.lyw.springbootstarter.domain
```
如果需要提供OpenFeign服务接口给其他服务调用, 需要添加以下配置,请看com/lyw/springcloudstarter/openfeign包下的示例代码


### 拓展配置
 注意下面的配置都需要独立搭建额外的服务
- nacos-config
配置,同时需要搭建nacos服务
- https://blog.liuyaowen.club/blog/2024/07/15/springcloud/
```yaml
  cloud:
    #  nacos配置
    nacos:
      # nacos配置中心地址 configServer
      config:
        server-addr: 192.168.208.128:8848
        namespace: 9194952e-02a9-4737-89c2-1f3dee3317f0
        group: SEATA_GROUP
        file-extension: yaml
```
- nacos-discovery

```yaml
  cloud:
    #  nacos配置
    nacos:
      # nacos服务发现地址 discoveryClient
      discovery:
        server-addr: 192.168.208.128:8848
```

- seata 配置如下, 需要搭建seata服务, 参考文档
- 搭建TC 
- 建表
- 配置注解 GlobalTransactionScanner
- https://blog.liuyaowen.club/blog/2024/07/15/springcloud/
```yaml
  cloud:
    seata:
      registry:
        nacos:
          server-addr: 192.168.208.128:8848
          namespace: 9194952e-02a9-4737-89c2-1f3dee3317f0
          group: SEATA_GROUP
          application: seata-server
          username: liuyaowen
          password: 123123
      tx-service-group: hmall
      service:
        vgroup-mapping:
          hmall: default
```

### 其他配置可以自行集成
如redis mq es等等