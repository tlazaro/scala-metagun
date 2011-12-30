import sbt._
import Keys._
import AndroidKeys._

object MetagunBuild extends Build {
	// Declare a project in the root directory of the build with ID "root".
	// Declare an execution dependency on sub1.
	lazy val common = Project("common", file("common"))

	lazy val desktop: Project = Project("desktop", file("desktop")) dependsOn(common) settings (
		mainClass in Compile := Some("com.mojang.metagun.Main"),
		fork in run := true
	)

    val options = """-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}"""

	lazy val android: Project = Project("android", file("android"),
          settings = AndroidGeneral.fullAndroidSettings ++ Seq (
            proguardOption in Android := options,
            mainAssetsPath in Android := file("common/src/main/resources"),
            javaHome := Some(file("/usr/lib/jvm/java-6-sun/")) // to avoid java 7            
          )
        ) dependsOn(common) 

}
 
object AndroidGeneral {
  val settings = Defaults.defaultSettings ++ Seq (
    name := "Scala-Metagun Android",
    version := "0.1",
    platformName in Android := "android-10"
  )

  lazy val fullAndroidSettings =
    AndroidGeneral.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "change-me",
      libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"
    )
}

