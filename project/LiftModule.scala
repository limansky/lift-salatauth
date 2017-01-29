import sbt._

object LiftModule {
  val liftVersion = SettingKey[String]("liftVersion", "Full version number of the Lift Web Framework")
  val liftEdition = SettingKey[String]("liftEdition", "Lift Edition (short version number to append to artifact name)")
}
