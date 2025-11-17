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
import org.scalatestplus.play.{MixedPlaySpec, PortNumber}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Writes
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.test.Helpers._
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.AppConfig
import play.api.libs.ws.DefaultBodyWritables._

trait ComponentSpecBase extends MixedPlaySpec with CustomMatchers
  with WiremockHelper with BeforeAndAfterAll with BeforeAndAfterEach {

  def defaultApp: Application = app(Map.empty)

  def app(extraConfig: Map[String, String]): Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config ++ extraConfig)
    .build()

  implicit def appConfig(implicit app: Application): AppConfig = app.injector.instanceOf[AppConfig]

  implicit def ws(implicit app: Application): WSClient = app.injector.instanceOf[WSClient]

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: String = WiremockHelper.wiremockPort.toString
  val mockUrl: String = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "auditing.enabled" -> "false",
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.base.host" -> mockHost,
    "microservice.services.base.port" -> mockPort,
    "microservice.services.des.url" -> mockUrl,
    "microservice.services.hip.host" -> mockHost,
    "microservice.services.hip.port" -> mockPort
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
    resetWiremock()
    super.beforeEach()
  }

  def get(uri: String)(implicit ws: WSClient, portNumber: PortNumber): WSResponse = {
    await(authorisedClient(uri).get())
  }

  def post[T](uri: String)(body: T)(implicit writes: Writes[T], ws: WSClient, portNumber: PortNumber): WSResponse = {
    await(
      authorisedClient(uri, "Content-Type" -> "application/json")
        .post(writes.writes(body).toString())
    )
  }

  def put[T](uri: String)(body: T)(implicit writes: Writes[T], ws: WSClient, portNumber: PortNumber): WSResponse = {
    await(
      authorisedClient(uri, "Content-Type" -> "application/json")
        .put(writes.writes(body).toString())
    )
  }

  val baseUrl: String = "/income-tax-subscription-eligibility"

  private def authorisedClient(path: String, extraHeaders: (String, String)*)(implicit ws: WSClient, portNumber: PortNumber) = {
    val sessionId = "X-Session-ID" -> "testSessionId"
    val authorisation = "Authorization" -> "Bearer 123"
    val headers = extraHeaders.toList :+ sessionId :+ authorisation
    ws.url(s"http://localhost:${portNumber.value}$baseUrl$path")
      .withFollowRedirects(false)
      .withHttpHeaders(headers: _*)
  }

}
