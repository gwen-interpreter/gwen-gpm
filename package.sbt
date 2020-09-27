enablePlugins(JavaAppPackaging)
enablePlugins(ClasspathJarPlugin)

// add assembly JAR to published artifacts
artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.withClassifier(Some("assembly"))
}
addArtifact(artifact in (Compile, assembly), assembly)

// add general files to universal zip
mappings in Universal += file("README.md") -> "README.txt"
mappings in Universal += file("LICENSE") -> "LICENSE.txt"
mappings in Universal += file("NOTICE") -> "NOTICE.txt"
mappings in Universal += file("LICENSE-THIRDPARTY") -> "LICENSE-THIRDPARTY.txt"
mappings in Universal += file("CHANGELOG") -> "CHANGELOG.txt"

// add universal zip to published artifacts
val packageZip = taskKey[File]("package-zip")
packageZip := (baseDirectory in Compile).value / "target" / "universal" / (name.value + "-" + version.value + ".zip")
artifact in (Universal, packageZip) ~= { (art:Artifact) => art.withType("zip").withExtension("zip") }
addArtifact(artifact in (Universal, packageZip), packageZip in Universal)
publish := ((publish) dependsOn (packageBin in Universal)).value
publishM2 := ((publishM2) dependsOn (packageBin in Universal)).value
publishLocal := ((publishLocal) dependsOn (packageBin in Universal)).value
PgpKeys.publishSigned := ((PgpKeys.publishSigned) dependsOn (packageBin in Universal)).value
