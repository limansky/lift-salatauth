name := "salatauth"

organization := "net.liftmodules"

version := "1.0-SNAPSHOT"

liftVersion <<= liftVersion ?? "2.6-SNAPSHOT"

liftEdition <<= liftVersion apply { _.substring(0,3) }

moduleName <<= (name, liftEdition) { (n, e) => n + "_" + e }

scalaVersion := "2.10.3"

crossScalaVersions := Seq("2.10.0", "2.9.2")

resolvers += "CB Central Mirror" at "http://repo.cloudbees.com/content/groups/public"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

libraryDependencies <+= liftVersion { v =>
  "net.liftweb"     %% "lift-webkit"    % v         % "provided"
}

libraryDependencies ++= Seq(
  "org.mindrot"     %  "jbcrypt"        % "0.3m"    % "compile",
  "com.novus"       %% "salat"          % "1.9.4"   % "compile",
  "org.scalatest"   %% "scalatest"      % "1.9.2"   % "test"
)

scalariformSettings
