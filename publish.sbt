publishMavenStyle := true
pomIncludeRepository := { _ => false }
publishArtifact in Test := false

pomExtra := <developers>
    <developer>
      <id>bjuric</id>
      <name>Branko Juric</name>
      <url>https://github.com/bjuric</url>
    </developer>
  </developers>
