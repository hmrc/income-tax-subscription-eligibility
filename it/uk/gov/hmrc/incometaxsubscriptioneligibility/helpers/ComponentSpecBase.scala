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

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Writes
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.Helpers._
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{AppConfig, FeatureSwitch, FeatureSwitching}


trait ComponentSpecBase extends PlaySpec with GuiceOneServerPerSuite with CustomMatchers
  with WiremockHelper with BeforeAndAfterAll with BeforeAndAfterEach with FeatureSwitching {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build

  lazy val ws: WSClient = app.injector.instanceOf[WSClient]

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: String = WiremockHelper.wiremockPort.toString
  val mockUrl: String = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "auditing.enabled" -> "false",
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
    "microservice.services.base.host" -> mockHost,
    "microservice.services.base.port" -> mockPort,
    "microservice.services.des.url" -> mockUrl
  )

  override def beforeAll(): Unit = {
    startWiremock()
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    FeatureSwitch.switches foreach disable
    resetWiremock()
    super.beforeEach()
  }

  def get[T](uri: String): WSResponse = {
    await(buildClient(uri).get)
  }

  def post[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    await(
      buildClient(uri)
        .withHttpHeaders("Content-Type" -> "application/json")
        .post(writes.writes(body).toString())
    )
  }

  def put[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    await(
      buildClient(uri)
        .withHttpHeaders("Content-Type" -> "application/json")
        .put(writes.writes(body).toString())
    )
  }

  val baseUrl: String = "/income-tax-subscription-eligibility"

  def buildClient(path: String): WSRequest = ws.url(s"http://localhost:$port$baseUrl$path").withFollowRedirects(false)

  protected lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

}
