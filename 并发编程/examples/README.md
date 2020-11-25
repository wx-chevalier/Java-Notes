[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/wx-chevalier/ms-java-commons">
    <img src="header.svg" alt="Logo" style="width: 100vw;height: 400px" />
  </a>

  <p align="center">
    <a href="https://github.com/wx-chevalier/ms-java-commons">Demo</a>
    ·
    <a href="https://github.com/wx-chevalier">更多项目</a>
    ·
    <a href="https://ng-tech.icu/books">参考资料</a>
  </p>
</p>

<!-- ABOUT THE PROJECT -->

# ms-java-commons

# Usage

## IDE

安装 google-java-format 和 lombok 插件；在配置中 Editor | Code Style | scheme 中导入 `conf/intellij-java-google-style.xml` 风格配置。

## Database Migration

使用 flyway 进行数据库迁移，迁移脚本默认放在 `msjc-infrastructure/src/main/resources/db/migration` 下，在
`msjc-infrastructure/build.gradle` 已经默认配置了测试数据库。

```sh
./gradlew flywayInfo
```

可以通过 `flyway.configFiles` 来替换配置文件：

```sh
./gradlew -Pflyway.configFiles=/path/to/flyway.conf flywayInfo
```

flyway 依赖在 msjc-infrastructure 子项目中添加，如果配置文件使用相对路径，它相对的将是该子项目根目录。

如在根目录中执行命令，要使用 `conf/flyway-local.conf` 配置：

```sh
./gradlew -Pflyway.configFiles=../conf/flyway-local.conf flywayInfo
```

数据库迁移脚本的命名见 [https://flywaydb.org/documentation/migrations#naming](https://flywaydb.org/documentation/migrations#naming)。我们正常使用的就是 Versioned
Migrations。

## Publish

此发布指将 tools/ 下公共库发布到 Maven Central 等仓库，参考 [Bintray](https://reflectoring.io/guide-publishing-to-bintray-with-gradle/)。

```sh
$ ./gradlew bintrayUpload -Dbintray.user=<YOUR_USER_NAME> -Dbintray.key=<YOUR_API_KEY>
```

## Deployment

此部署指部署 Web 应用。

- 本地部署

```sh
cp conf/env.tpl .dev.env
# 设定 .dev.env 中的环境变量

# 构建、推送镜像
(source .dev.env && ./scripts/docker/build-locally.sh)
# 部署
(source .dev.env && ./scripts/deploy-locally.sh)
```

- 正式版本部署

```sh
git checkout master
git merge dev

# 修改 .prod.env 版本如 1.0
(source .prod.env && ./scripts/docker/build-locally.sh && ./scripts/deploy-locally.sh)

# 部署成功后，推送对应 tag
git tag 1.0 -m
git push --tags
```

# About

<!-- ROADMAP -->

## Roadmap

See the [open issues](https://github.com/wx-chevalier/ms-java-commons/issues) for a list of proposed features (and known issues).

<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- LICENSE -->

## License

Distributed under the MIT License. See `LICENSE` for more information.

<!-- ACKNOWLEDGEMENTS -->

## Acknowledgements

- [Awesome-Lists](https://github.com/wx-chevalier/Awesome-Lists): 📚 Guide to Galaxy, curated, worthy and up-to-date links/reading list for ITCS-Coding/Algorithm/SoftwareArchitecture/AI. 💫 ITCS-编程/算法/软件架构/人工智能等领域的文章/书籍/资料/项目链接精选。

- [Awesome-CS-Books](https://github.com/wx-chevalier/Awesome-CS-Books): :books: Awesome CS Books/Series(.pdf by git lfs) Warehouse for Geeks, ProgrammingLanguage, SoftwareEngineering, Web, AI, ServerSideApplication, Infrastructure, FE etc. :dizzy: 优秀计算机科学与技术领域相关的书籍归档。

- [Hutool #Project#](https://www.hutool.cn/): A set of tools that keep Java sweet.

- [freedom](https://github.com/8treenet/freedom): freedom 是一个基于六边形架构的框架，可以支撑充血的领域模型范式。

## Copyright & More | 延伸阅读

笔者所有文章遵循[知识共享 署名 - 非商业性使用 - 禁止演绎 4.0 国际许可协议](https://creativecommons.org/licenses/by-nc-nd/4.0/deed.zh)，欢迎转载，尊重版权。您还可以前往 [NGTE Books](https://ng-tech.icu/books/) 主页浏览包含知识体系、编程语言、软件工程、模式与架构、Web 与大前端、服务端开发实践与工程架构、分布式基础架构、人工智能与深度学习、产品运营与创业等多类目的书籍列表：

[![NGTE Books](https://s2.ax1x.com/2020/01/18/19uXtI.png)](https://ng-tech.icu/books/)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/wx-chevalier/ms-java-commons.svg?style=flat-square
[contributors-url]: https://github.com/wx-chevalier/ms-java-commons/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/wx-chevalier/ms-java-commons.svg?style=flat-square
[forks-url]: https://github.com/wx-chevalier/ms-java-commons/network/members
[stars-shield]: https://img.shields.io/github/stars/wx-chevalier/ms-java-commons.svg?style=flat-square
[stars-url]: https://github.com/wx-chevalier/ms-java-commons/stargazers
[issues-shield]: https://img.shields.io/github/issues/wx-chevalier/ms-java-commons.svg?style=flat-square
[issues-url]: https://github.com/wx-chevalier/ms-java-commons/issues
[license-shield]: https://img.shields.io/github/license/wx-chevalier/ms-java-commons.svg?style=flat-square
[license-url]: https://github.com/wx-chevalier/ms-java-commons/blob/master/LICENSE.txt
