import sbt._
import Keys._

object MetagunBuild extends Build {
	// Declare a project in the root directory of the build with ID "root".
	// Declare an execution dependency on sub1.
	lazy val common = Project("common", file("common"))

	lazy val desktop: Project = Project("desktop", file("desktop")) dependsOn(common) settings (
		mainClass in Compile := Some("com.mojang.metagun.Main"),
		fork in run := true
	)

	//lazy val android: Project = Project("android", file("android")) dependsOn(common)
}

