enablePlugins(JavaAppPackaging)

val packageZip = taskKey[File]("package-zip")

packageZip := (baseDirectory in Compile).value / "target" / "universal" / (name.value + "-" + version.value + ".zip")

artifact in (Universal, packageZip) ~= { (art:Artifact) => art.copy(`type` = "zip", extension = "zip") }

addArtifact(artifact in (Universal, packageZip), packageZip in Universal)

mappings in Universal += file("LICENSE") -> "LICENSE"

mappings in Universal += file("NOTICE") -> "NOTICE"

mappings in Universal += file("LICENSE-THIRDPARTY") -> "LICENSE-THIRDPARTY"

mappings in Universal += file("CHANGELOG") -> "CHANGELOG"

mappings in (Compile, packageBin) ++= Seq(
  file("LICENSE") -> "LICENSE",
  file("NOTICE") -> "NOTICE",
  file("LICENSE-THIRDPARTY") -> "LICENSE-THIRDPARTY",
  file("CHANGELOG") -> "CHANGELOG"
)
