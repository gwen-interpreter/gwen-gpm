import com.github.retronym.SbtOneJar._

oneJarSettings

name := "gwen-gpm"

description := "Gwen Package Manager"

organization := "org.gweninterpreter"

organizationHomepage := Some(url("http://gweninterpreter.org"))

startYear := Some(2017)

scalaVersion := "2.12.8"

crossPaths := false

trapExit := false

scalacOptions += "-feature"

scalacOptions += "-language:postfixOps"

scalacOptions += "-deprecation"

scalacOptions += "-target:jvm-1.8"

licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")

homepage := Some(url("https://github.com/gwen-interpreter/gwen-web"))

javaSource in Compile := baseDirectory.value / "src/main/scala"

javaSource in Test := baseDirectory.value / "src/test/scala"

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.7.0",
  "org.apache.commons" % "commons-compress" % "1.15",
  "commons-codec" % "commons-codec" % "1.11",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

