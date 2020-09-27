lazy val projectSettings = Seq(
  name := "gwen-gpm",
  description := "Gwen Package Manager",
  scalaVersion := "2.13.3",
  organization := "org.gweninterpreter",
  homepage := Some(url("https://github.com/gwen-interpreter/gwen-gpm")),
  organizationHomepage := Some(url("http://gweninterpreter.org")),
  startYear := Some(2017),
  licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"),
  trapExit := false,
  crossPaths := false,
  scalacOptions ++= Seq(
    "-feature",
    "-language:postfixOps",
    "-deprecation",
    "-target:8",
    "-Xlint:_,-missing-interpolator"
  ),
  initialize := {
    val _ = initialize.value
    val javaVersion = sys.props("java.specification.version")
    if (javaVersion != "1.8")
      sys.error(s"JDK 8 is required to build this project. Found $javaVersion instead")
  }
)

lazy val mainDependencies = {
  val scopt = "3.7.1"
  val commonsCompress = "1.20"
  val commonsCodec = "1.15"

  Seq(
    "com.github.scopt" %% "scopt" % scopt,
    "org.apache.commons" % "commons-compress" % commonsCompress,
    "commons-codec" % "commons-codec" % commonsCodec,
  )
}

lazy val testDependencies = {
  val scalaTest = "3.0.9"

  Seq(
    "org.scalatest" %% "scalatest" % scalaTest
  ).map(_ % Test)
}

lazy val root = (project in file("."))
  .settings(
    projectSettings,
    libraryDependencies ++= mainDependencies ++ testDependencies
  )

mappings in(Compile, packageBin) ++= Seq(
  file("README.md") -> "README.txt",
  file("LICENSE") -> "LICENSE.txt",
  file("NOTICE") -> "NOTICE.txt",
  file("LICENSE-THIRDPARTY") -> "LICENSE-THIRDPARTY.txt",
  file("CHANGELOG") -> "CHANGELOG.txt"
)
