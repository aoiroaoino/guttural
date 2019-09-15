# Monoton

単調で退屈な Scala 向けの Web Framework.

## What's Monoton?

Monoton は Netty と Akka HTTP をベースにした関数型の Web Framework です。
多くの部分で Play Framework に影響を受けており、これに慣れている開発者であればすぐに使いこなすことができるでしょう。
もちろん、Play Framework を使ったことなくても大丈夫。ありとあらゆる処理をシンプルな Handler という概念を合成することで、
実現することができます。Monoton という名前の由来は「monotone: 単調な、単純で退屈な」。
きっと使い始めてすぐにそのコンセプトに納得するはずです。それではさっそく始めましょう！


## Quick Start

SBT を使うのであればセットアップはとても簡単です。`plugins.sbt` ファイルに以下の一行を追加し、
`build.sbt` ファイルで対象のプロジェクトで `MonotonPlugin` を有効化するだけです。

### build settings

`project/plugins.sbt`

```scala
addSbtPlugin("dev.aoiroaoino" % "monoton-plugin" % "0.1.0-SNAPSHOT")
```

`build.sbt`

```scala
lazy val root = (project in file("."))
  .enablePlugins(MonotonPlugin)
```

なんと、たったこれだけで REST API サーバーが起動します！
試してみましょう。

```bash
$ sbt run
```

```bash
$ curl -i http://localhost:8080
HTTP/1.1 200 OK
content-type: text/html
content-length: 24
connection: close

<h1>Hello, Monoton!</h1>
```

とても簡単ですね？さぁ、より深く知るためにもドキュメントを読み進めましょう。
