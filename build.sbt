name := "salatauth"

description := "Lift Salat authentication and authorization module"

organization := "net.liftmodules"

version := "1.2-SNAPSHOT"

licenses += ("Apache 2.0 License", url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("http://github.com/limansky/lift-salatauth"))

liftVersion <<= liftVersion ?? "2.6"

liftEdition <<= liftVersion apply { _.substring(0,3) }

moduleName <<= (name, liftEdition) { (n, e) => n + "_" + e }

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.10.5", "2.9.2")

resolvers += "CB Central Mirror" at "http://repo.cloudbees.com/content/groups/public"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

libraryDependencies <+= liftVersion { v =>
  "net.liftweb"     %% "lift-webkit"    % v         % "provided"
}

libraryDependencies += "org.mindrot"     %  "jbcrypt"        % "0.3m"    % "compile"

libraryDependencies <++= scalaVersion { sv =>
  val salatV = if (sv == "2.9.2") "1.9.5" else "1.9.9"
  val scalatestV = if (sv == "2.9.2") "1.9.2" else "2.2.4"
  Seq("com.novus"       %% "salat"          % salatV      % "provided",
      "org.scalatest"   %% "scalatest"      % scalatestV  % "test"
  )
}

scalariformSettings

publishMavenStyle := true

publishArtifact in Test := false

scmInfo := Some(
  ScmInfo(
    url("https://github.com/limansky/lift-salatauth"),
    "scm:git:https://github.com/limansky/lift-salatauth.git",
    Some("scm:git:git@github.com:limansky/lift-salatauth.git")
  )
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := (
  <developers>
    <developer>
      <id>limansky</id>
      <name>Mike Limansky</name>
      <url>http://github.com/limansky</url>
    </developer>
  </developers>)
