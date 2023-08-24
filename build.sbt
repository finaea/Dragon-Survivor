name :="Dragon Survivor"
version :="1.0"
scalaVersion := "2.12.17"

// https://mvnrepository.com/artifact/org.scalafx/scalafx
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.192-R14"

// https://mvnrepository.com/artifact/org.scalafx/scalafxml-core-sfx8
libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.5"

// https://mvnrepository.com/artifact/org.scalamacros/paradise
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full)

fork := true //run two program at the same time