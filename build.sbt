import com.github.retronym.SbtOneJar._

oneJarSettings

name := "gwen-gpm"

description := "Gwen Package Manager"

scalaVersion := "2.12.3"

crossPaths := false

trapExit := false

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.6.0",
  "org.apache.commons" % "commons-compress" % "1.14",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

enablePlugins(JavaAppPackaging)

mappings in (Compile, packageBin) ++= Seq(
  file("LICENSE") -> "LICENSE",
  file("NOTICE") -> "NOTICE",
  file("LICENSE-THIRDPARTY") -> "LICENSE-THIRDPARTY",
  file("CHANGELOG") -> "CHANGELOG"
)

