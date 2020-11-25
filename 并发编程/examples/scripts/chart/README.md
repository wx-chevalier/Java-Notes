# 参数配置

| 参数                               | 描述                  | 默认                                    | 必须 | 作用域                 |
|------------------------------------|-----------------------|-----------------------------------------|------|------------------------|
| `serverPort`                       | Spring 应用端口       | `80`                                    |      | `configmap/deployment` |
| `serverTimezone`                   | JVM 启动时区          | `Asia/Shanghai`                         |      | `deployment`           |
| `localFileStore.savePath`          | 本地存储文件存储路径  | `/ufc/files`                            |      | `deployment/configmap` |
| `localFileStore.maxFileSize`       | 本地存储文件最大大小  | `1024MB`                                |      | `deployment/configmap` |
| `localFileStore.endpoint`          | 本地存储文件 endpoint | `https://api.biz.com/file`         |      | `deployment/configmap` |
| `oss.accessKeyId`                  |                       |                                         | true | `secret`               |
| `oss.accessKeySecret`              |                       |                                         | true | `secret`               |
| `oss.endpoint`                     |                       |                                         | true | `configmap`            |
| `oss.bucketName`                   |                       |                                         | true | `configmap`            |
| `oss.expiration`                   |                       | `900`                                   |      | `configmap`            |
| `oss.maxSize`                      |                       | `17179869184`                           |      | `configmap`            |
| `oss.callbackUrl`                  |                       | `https://api.biz.com/callback/oss` |      | `configmap`            |
| `mail.port`                        |                       | `465`                                   |      | `configmap`            |
| `mail.host`                        |                       | `smtp.exmail.qq.com`                    |      | `configmap`            |
| `mail.username`                    |                       | `notice@biz.com`                   |      | `configmap`            |
| `mail.password`                    |                       |                                         | true | `secret`               |
| `sms.regionId`                     |                       | `cn-hangzhou`                           |      | `configmap`            |
| `sms.signName`                     |                       | `优联智造`                              |      | `configmap`            |
| `sms.accessKeyId`                  |                       |                                         | true | `secret`               |
| `sms.accessKeySecret`              |                       |                                         | true | `secret`               |
| `jwt.secret`                       |                       |                                         | true | `secret`               |
| `wechat.appId`                     |                       |                                         | true | `configmap`            |
| `wechat.token`                     |                       |                                         | true | `secret`               |
| `wechat.appSecret`                 |                       |                                         | true | `secret`               |
| `gexin.appId`                      |                       |                                         | true | `configmap`            |
| `gexin.appKey`                     |                       |                                         | true | `secret`               |
| `gexin.appSecret`                  |                       |                                         | true | `secret`               |
| `gexin.masterSecret`               |                       |                                         | true | `secret`               |
| `dingTalk.accessToken`             |                       |                                         |      | `secret`               |
| `rabbitMqVirtualHost`              |                       | `/dev`                                  |      | `configmap`            |
| `mqttMaxBytesInMessage`            |                       | `819200`                                |      | `configmap`            |
| `mqttReconnect`                    |                       | `true`                                  |      | `configmap`            |
| `persistence.enabled`              |                       | `false`                                 |      | `deployment/pvc`       |
| `persistence.existingClaim`        |                       |                                         |      | `deployment/pvc`       |
| `persistence.size`                 |                       |                                         |      | `pvc`                  |
| `persistence.accessMode`           |                       |                                         |      | `pvc`                  |
| `persistence.storageClass`         |                       |                                         |      | `pvc`                  |
| `persistence.annotations`          |                       | `-`                                     |      | `pvc`                  |
| `service.type`                     |                       | `ClusterIP`                             |      | `service`              |
| `service.port`                     |                       | `80`                                    |      | `service/ingress`      |
| `ingress.enabled`                  |                       |                                         |      | `ingress`              |
| `ingress.annotations`              |                       |                                         |      | `ingress`              |
| `ingress.hosts`                    |                       |                                         |      | `ingress`              |
| `ingress.tls`                      |                       |                                         |      | `ingress`              |

# 阿里云 Arms 监控

| 参数              | 描述                 | 默认    | 必须 | 作用域                 |
|-------------------+----------------------+---------+------+------------------------|
| `arms.enabled`    | 是否开启监控         | `false` |      | `configmap/deployment` |
| `arms.appName`    | JVM 启动时区         |         |      | `deployment`           |
| `arms.licenseKey` | 本地存储文件存储路径 |         |      | `deployment/secret`    |

# MySQL 配置

如果 `mysql.enabled`，启动内部 MySQL，`mysql` 的配置见 [helm/charts/stable/mysql](https://github.com/helm/charts/tree/master/stable/mysql)，否则使用 `externalMySql` 配置：

| 参数                     | 描述                                        | 必须 | 作用域                      |
|--------------------------|---------------------------------------------|------|-----------------------------|
| `mysql.enabled`          | false 则使用 `externalMySql.*` 配置的数据库 |      | deployment/configmap/secret |
| `externalMySql.url`      |                                             | true | configmap                   |
| `externalMySql.username` |                                             | true | configmap                   |
| `externalMySql.password` |                                             | true | secret                      |

另外 `mysql` 添加了自定义 jdbc 参数：

| 参数                   | 默认                                                                                         | 作用域    |
|------------------------|----------------------------------------------------------------------------------------------|-----------|
| `mysql.extralJdbcArgs` | `allowMultiQueries=true&useLegacyDatetimeCode=false&useUnicode=true&characterEncoding=utf-8` | configmap |

# RabbitMq 配置

**作用域：`secret`**

如果 `rabbitmq.enabled`，启动内部 Rabbitmq，`rabbitmq` 配置见 [helm/charts/stable/rabbitmq](https://github.com/helm/charts/tree/master/stable/rabbitmq)，否则使用
`externalRabbitMq` 配置：

| 参数                        | 必须 | 作用域                        |
|-----------------------------|------|-------------------------------|
| `rabbitmq.enabled`          |      | `deployment/configmap/secret` |
| `externalRabbitMq.host`     | true | `configmap`                   |
| `externalRabbitMq.port`     | true | `configmap`                   |
| `externalRabbitMq.username` | true | `configmap`                   |
| `externalRabbitMq.password` | true | `secret`                      |

另外 `rabbitmq` 下添加了一些自定义配置：

| 参数                     | 描述                                                   | 默认    | 必须 | 作用域      |
|--------------------------|--------------------------------------------------------|---------|------|-------------|
| `rabbitmq.mqtt.enabled`  | 注意开启的话确保 `extraPlugins` 配置了 `rabbitmq_mqtt` | `false` |      | `secret`    |
| `rabbitmq.mqtt.port`     | 注意自行确保和 `mqtt.listeners.tcp.default` 保持一致   | `1883`  | true | `configmap` |
| `rabbitmq.mqtt.username` | 注意自行确保和 `mqtt.default_user` 保持一致            |         | true | `configmap` |
| `rabbitmq.mqtt.password` | 注意自行确保和 `mqtt.default_pass` 保持一致            |         | true | `secret`    |

# Mqtt 配置

**作用域：`secret`**

如果 `rabbitmq` 的 mqtt 未开启，将尝试使用外部 mqtt 服务：

| 参数                      | 默认   | 必须 | 作用域      |
|---------------------------|--------|------|-------------|
| `externalMqtt.username`   |        | true | `configmap` |
| `externalMqtt.password`   |        | true | `secret`    |
| `externalMqtt.brokerHost` |        | true | `configmap` |
| `externalMqtt.brokerPort` | `1883` |      | `configmap` |

# 示例配置

## RabbitMq 配置

配置中开启了 mqtt 插件（`rabbitmq.rabbitmq.extraPlugins`），注意
- `rabbitmq.mqtt` 下配置和 `rabbitmq.rabbitmq.extraConfiguration` 中的 mqtt 配置保持一致
- 通过 `rabbitmq.service.extraPorts` 和 `rabbitmq.service.extraContainerPorts` 将 mqtt 服务暴露出来
- 开启 `rabbitmq.rabbitmq.loadDefinition`，在 `rabbitmq.rabbitmq.extraConfiguration` 中加载对应配置；
  在 `rabbitmq.rabbitmq.loadDefinition.secretName` 对应的 extraSecret 中配置用户权限信息。

配置可以参考
- https://www.rabbitmq.com/configure.html#config-file
- https://www.rabbitmq.com/mqtt.html#config

⚠️ 注意：使用了 `management.load_definition` 加载定义之后，会覆盖掉 `rabbitmq.rabbitmq.username` 定义的用户，
暂时解决方案自行同步所有用户定义到 `load_definition.json` 中。

```conf
rabbitmq:
  enabled: true
  mqtt:
    enabled: true
    user: <mqtt-user>
    password: <mqtt-password>
  rabbitmq:
    username: admin
    password: <admin-password>
    ## rabbitmq_mqtt plugin extra configurations
    # extraConfiguration mqtt related conf
    # mqtt's user & vhost permissions in extraSecrets.load-definition
    # service.extraPorts & service.extraContainerPorts mqtt related ports
    extraPlugins: "rabbitmq_auth_backend_ldap rabbitmq_mqtt"
    loadDefinition:
      enabled: true
      # extraSecrets.load-finition
      secretName: load-definition
    extraConfiguration: |
      # loadDefintion.secretName mounts to /app
      management.load_definitions = /app/load_definition.json

      mqtt.default_user = <mqtt-user>
      mqtt.default_pass = <mqtt-password>
      mqtt.allow_anonymous = false
      mqtt.vhost = /mqtt

  service:
    type: NodePort
    nodePort: 31002

    extraPorts:
      - name: mqtt
        port: 1883
        targetPort: mqtt
        nodePort: 31003
    extraContainerPorts:
      - name: mqtt
        containerPort: 1883

  persistence:
    enabled: true
    existingClaim: ufc-test-rabbitmq

  extraSecrets:
    load-definition:
      load_definition.json: |
        {
          "vhosts": [
            {
              "name": "/"
            },
            {
              "name": "/dev"
            },
            {
              "name": "/mqtt"
            }
          ],
          "users": [
            {
              "name": "admin",
              "password": "<admin-pass>",
              "tags": "administrator"
            },
            {
              "name": "<mqtt-user>",
              "password": "<mqtt-password>",
              "tags": "administrator"
            }
          ],
          "permissions": [
            {
              "user": "admin",
              "vhost": "/dev",
              "configure": ".*",
              "write": ".*",
              "read": ".*"
            },
            {
              "user": "<mqtt-user>",
              "vhost": "/mqtt",
              "configure": ".*",
              "write": ".*",
              "read": ".*"
            }
          ]
        }

  ingress:
    enabled: true
    hostName: rabbitmq.test.biz.com
    tls: true
    tlsSecret: rabbitmq-test-unionfab-com
    annotations:
      nginx.ingress.kubernetes.io/proxy-body-size: "0"
      kubernetes.io/ingress.class: "nginx"
      certmanager.k8s.io/issuer: "letsencrypt-staging-cluster"
```
