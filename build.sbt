val Http4sVersion = "0.23.6"
val CirceVersion = "0.14.1"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.7"
val MunitCatsEffectVersion = "1.0.6"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.mixren",
    name := "evo-scala-bootcamp-exoplanet-market",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server"  % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client"  % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client"  % Http4sVersion,
      "org.http4s"      %% "http4s-circe"         % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"           % Http4sVersion,
      "io.circe"        %% "circe-generic"        % CirceVersion,
      "io.circe"        %% "circe-parser"         % CirceVersion,
      "io.circe"        %% "circe-generic-extras" % "0.14.1",
      "org.scalameta"   %% "munit"                % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3"  % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"      % LogbackVersion,
      "org.scalameta"   %% "svm-subs"             % "20.2.0",
      "org.xerial"      %  "sqlite-jdbc"          % "3.36.0.2",
      "org.tpolecat"    %% "doobie-core"          % "1.0.0-RC1",
      "org.tpolecat"    %% "doobie-hikari"        % "1.0.0-RC1", // HikariCP transactor.
      "org.tpolecat"    %% "doobie-specs2"        % "1.0.0-RC1", // Specs2 support for typechecking statements.
      "org.tpolecat"    %% "doobie-scalatest"     % "1.0.0-RC1", // ScalaTest support for typechecking statements.
      "com.github.tototoshi" %% "scala-csv"       % "1.3.8"     // Robust csv reader
      // "org.typelevel"   %% "cats-effect"         % "2.5.2"  // Error (library incompatibilities)
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
