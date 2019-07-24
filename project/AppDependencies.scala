import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-play-26" % "0.41.0"
  )

  val test = Seq(
    "com.typesafe.play" %% "play-test" % current % "test",
    "org.scalamock" %% "scalamock" % "4.3.0" % "test",
    "org.pegdown" % "pegdown" % "1.6.0" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8" % "2.23.2" % "it"
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
