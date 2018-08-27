val Http4sVersion = "0.18.16"
val Specs2Version = "4.2.0"
val LogbackVersion = "1.2.3"
val CirceVersion = "0.9.3"
val DoobieVersion = "0.5.3"
val ScalaMockVersion = "4.1.0"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    organization := "brisbane.scala",
    name := "meetup",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    scalacOptions += "-Ypartial-unification",
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server"       % Http4sVersion,
      "org.http4s"      %% "http4s-circe"              % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"                % Http4sVersion,
      "io.circe"        %% "circe-core"                % CirceVersion,
      "io.circe"        %% "circe-generic"             % CirceVersion,
      "org.tpolecat"    %% "doobie-core"               % DoobieVersion,
      "org.tpolecat"    %% "doobie-postgres"           % DoobieVersion,
      "org.tpolecat"    %% "doobie-specs2"             % DoobieVersion % "it,test",
      "org.specs2"      %% "specs2-core"               % Specs2Version % "it,test",
      "org.scalamock"   %% "scalamock"                 % ScalaMockVersion % Test,
      "ch.qos.logback"  %  "logback-classic"           % LogbackVersion
    )
  )

