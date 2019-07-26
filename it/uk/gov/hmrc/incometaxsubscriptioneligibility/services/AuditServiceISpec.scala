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

package uk.gov.hmrc.incometaxsubscriptioneligibility.services

import com.github.tomakehurst.wiremock.client.WireMock.{findAll, postRequestedFor, urlMatching}
import play.api.Application
import play.api.http.Status
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.AuditStub
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.{ComponentSpecBase, IntegrationTestConstants}
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import uk.gov.hmrc.play.audit.model.DataEvent
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global

class AuditServiceISpec extends ComponentSpecBase {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val fr: Request[AnyContent] = FakeRequest(POST, IntegrationTestConstants.testUrl)

  override def config: Map[String, String] = Map(
    "auditing.enabled" -> "true",
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
    "microservice.services.base.host" -> mockHost,
    "microservice.services.base.port" -> mockPort,
    "microservice.services.des.url" -> mockUrl,
    "auditing.consumer.baseUri.host" -> mockHost,
    "auditing.consumer.baseUri.port" -> mockPort,
    "appName" -> IntegrationTestConstants.testAppName
  )

  val testAuditModel: AuditModel = new AuditModel {
    override val auditType: String = IntegrationTestConstants.Audit.testAuditType
    override val transactionName: String = IntegrationTestConstants.Audit.testTransactionName
    override val detail: Map[String, String] = IntegrationTestConstants.Audit.testDetail
  }

  implicit val testFormatsForJodaDateTime = new Reads[DateTime] {
    override def reads(json: JsValue): JsResult[DateTime] = JsSuccess(DateTime.now)
  }

  def auditService(implicit app: Application): AuditService = app.injector.instanceOf[AuditService]

  def dataEventMatcher(testDataEvent: DataEvent, capturedDataEvent: DataEvent): Unit = {
    testDataEvent.auditSource mustBe capturedDataEvent.auditSource
    testDataEvent.auditType mustBe capturedDataEvent.auditType
    capturedDataEvent.eventId.isEmpty mustBe false
    testDataEvent.tags mustBe capturedDataEvent.tags
    capturedDataEvent.generatedAt.toString().isEmpty mustBe false
  }

  val testAuditDataEvent = DataEvent(
    auditSource = IntegrationTestConstants.testAppName,
    auditType = IntegrationTestConstants.Audit.testAuditType,
    tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(IntegrationTestConstants.Audit.testTransactionName, IntegrationTestConstants.testUrl),
    detail = AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(IntegrationTestConstants.Audit.testDetail.toSeq: _*)
  )

  "audit" should {
    "audit a event successfully" in new App(defaultApp) {
      AuditStub.stubAudit(Status.NO_CONTENT)
      await(auditService.audit(testAuditModel)) mustBe AuditResult.Success

      val jsonSent: JsValue = Json.parse(findAll(postRequestedFor(urlMatching(AuditStub.auditUri))).get(0).getBodyAsString)
      val dataEventSent: DataEvent = jsonSent.as[DataEvent](Json.reads[DataEvent])
      dataEventMatcher(testAuditDataEvent, dataEventSent)

    }
    "not throw an exception if the call via the connector fails" in new App(defaultApp) {
      AuditStub.stubAudit(Status.INTERNAL_SERVER_ERROR, Json.obj())

      await(auditService.audit(testAuditModel)) mustBe AuditResult.Success
    }
  }
}
