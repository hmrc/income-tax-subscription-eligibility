/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.incometaxsubscriptioneligibility.helpers

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpecLike}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Writes
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{AppConfig, FeatureSwitch, FeatureSwitching}

import scala.concurrent.Await
import scala.concurrent.duration._

trait ComponentSpecBase extends WordSpecLike with Matchers with GuiceOneServerPerSuite with CustomMatchers
  with WiremockHelper with BeforeAndAfterAll with BeforeAndAfterEach with FeatureSwitching {

  lazy val ws = app.injector.instanceOf[WSClient]

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build
  val mockHost = WiremockHelper.wiremockHost
  val mockPort = WiremockHelper.wiremockPort.toString
  val mockUrl = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "auditing.enabled" -> "false",
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck"
    //"" -> mockHost, TODO Add services
    //"" -> mockPort
  )

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    FeatureSwitch.switches foreach disable
    resetWiremock()
  }

  implicit val timeout = 5 seconds

  def get[T](uri: String): WSResponse = {
    Await.result(
      awaitable = buildClient(uri).get,
      atMost = timeout
    )
  }

  def post[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    Await.result(
      awaitable =
        buildClient(uri)
          .withHttpHeaders("Content-Type" -> "application/json")
          .post(writes.writes(body).toString()),
      atMost = timeout
    )
  }

  def put[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    Await.result(
      awaitable =
        buildClient(uri)
          .withHttpHeaders("Content-Type" -> "application/json")
          .put(writes.writes(body).toString()),
      atMost = timeout
    )
  }

  val baseUrl: String = "/income-tax-subscription-eligibility"

  def buildClient(path: String) = ws.url(s"http://localhost:$port$baseUrl$path").withFollowRedirects(false)

  protected lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

}
