import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.3.0"
  private val domainVersion = "11.0.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc" %% "domain-play-30" % domainVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % "test",
    "org.scalamock" %% "scalamock" % "7.5.0" % "test",
  )

  val itTest: Seq[ModuleID] = Seq()

}
