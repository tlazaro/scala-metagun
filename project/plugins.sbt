addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.7.2")

resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-snapshots"))(sbt.Resolver.ivyStylePatterns)

addSbtPlugin("org.scala-tools.sbt" %% "sbt-android-plugin" % "0.6.2-SNAPSHOT")

