import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val domainVersion = "6.2.0-play-28"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % "5.16.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.5",  // NB Added because the default build produces a mix of Jackson 2.11 and 2.12 
    "uk.gov.hmrc" %% "domain" % domainVersion
  )

  val test = Seq(
    "com.typesafe.play" %% "play-test" % current % "test",
    "org.scalamock" %% "scalamock" % "5.1.0" % "test",
    "org.pegdown" % "pegdown" % "1.6.0" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test, it",
    "com.vladsch.flexmark" % "flexmark-all" % "0.35.10" % "test, it", // NB Added because scalatest 3.1.1 requires it, but does not declare a dependency
    "com.github.tomakehurst" % "wiremock-jre8" % "2.31.0" % "it"
  )

  val overrides: Set[ModuleID] = {
    val jettyFromWiremockVersion = "9.2.24.v20180105"
    Set(
      "org.eclipse.jetty" % "jetty-client" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-continuation" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-http" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-io" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-security" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-server" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-servlet" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-servlets" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-util" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-webapp" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty" % "jetty-xml" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty.websocket" % "websocket-api" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty.websocket" % "websocket-client" % jettyFromWiremockVersion % "it",
      "org.eclipse.jetty.websocket" % "websocket-common" % jettyFromWiremockVersion % "it"
    )
  }
}
