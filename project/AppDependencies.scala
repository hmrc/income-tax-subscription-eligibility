import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.13.0"
  private val domainVersion = "8.1.0-play-28"
  private val scalaTestVersion = "3.2.14"
  private val playCurrent = "2.8.17"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.4",
    "uk.gov.hmrc" %% "domain" % domainVersion
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % bootstrapVersion % "test, it",
    "com.typesafe.play" %% "play-test" % playCurrent % "test",
    "org.scalamock" %% "scalamock" % "5.2.0" % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test, it",
    "com.vladsch.flexmark" % "flexmark-all" % "0.62.2" % "test, it", // NB Added because scalatest requires it, but does not declare a dependency
    "org.mockito" % "mockito-core" % "4.8.0" % "test, it",
    "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8" % "2.32.0" % "it"

  )

}
