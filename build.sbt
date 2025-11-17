import uk.gov.hmrc.DefaultBuildSettings

val appName = "income-tax-subscription-eligibility"

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  val exclusionsList = List(
    "<empty>",
    "Reverse.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    "core.config.*",
    "com.*",
    "testonly.*",
    "business.*",
    "testOnlyDoNotUseInAppConf.*",
    "uk.gov.hmrc.incometaxsubscriptioneligibility.testonly.*"
  )

  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := exclusionsList.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}

ThisBuild / majorVersion := 1
ThisBuild / scalaVersion := "3.3.6"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scoverageSettings
  )
  .settings(PlayKeys.playDefaultPort := 9588)
  .settings(
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:msg=Flag.*repeatedly:s",
  )

Test / Keys.fork := true
Test / javaOptions += "-Dlogger.resource=logback-test.xml"
Test / parallelExecution := true

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.itTest)
  .settings(javaOptions += "-Dlogger.resource=logback-test.xml")
