name := "salatauth"

description := "Lift Salat authentication and authorization module"

organization := "net.liftmodules"

version := "1.2"

licenses += ("Apache 2.0 License", url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("http://github.com/limansky/lift-salatauth"))

liftVersion <<= liftVersion ?? "2.6.3"

liftEdition <<= liftVersion apply { _.substring(0,3) }

moduleName <<= (name, liftEdition) { (n, e) => n + "_" + e }

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.6")

resolvers += "CB Central Mirror" at "http://repo.cloudbees.com/content/groups/public"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

libraryDependencies ++= Seq(
  "net.liftweb"      %% "lift-webkit"    % liftVersion.value    % "provided",
  "org.mindrot"      %  "jbcrypt"        % "0.3m"               % "compile",
  "com.github.salat" %% "salat"          % "1.10.0"             % "provided",
  "org.scalatest"    %% "scalatest"      % "3.0.1"              % "test"
)

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
