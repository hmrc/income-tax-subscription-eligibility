import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "0.41.0"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.8"                 % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.mockito"             %  "mockito-core"             % "2.28.2"                % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.2"                 % "test, it",
    "com.github.tomakehurst"  %  "wiremock"                 % "2.23.2"                % "it"
  )

}
