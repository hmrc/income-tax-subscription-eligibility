
package uk.gov.hmrc.incometaxsubscriptioneligibility.services

import com.github.tomakehurst.wiremock.client.WireMock.{findAll, postRequestedFor, urlMatching}
import org.joda.time.DateTime
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
    "appName"                        -> IntegrationTestConstants.testAppName
  )

  val testAuditModel = new AuditModel {
    override val auditType = IntegrationTestConstants.Audit.testAuditType
    override val transactionName = IntegrationTestConstants.Audit.testTransactionName
    override val detail = IntegrationTestConstants.Audit.testDetail
  }

  class Setup {
    implicit val testFormatsForJodaDateTime = new Reads[DateTime] {
      override def reads(json: JsValue): JsResult[DateTime] = JsSuccess(DateTime.now)
    }
    val service = app.injector.instanceOf[AuditService]

    def dataEventMatcher(testDataEvent: DataEvent, capturedDataEvent: DataEvent):Unit = {
      testDataEvent.auditSource mustBe capturedDataEvent.auditSource
      testDataEvent.auditType mustBe capturedDataEvent.auditType
      capturedDataEvent.eventId.isEmpty mustBe false
      testDataEvent.tags mustBe capturedDataEvent.tags
      capturedDataEvent.generatedAt.toString().isEmpty mustBe false
    }
  }
  val testAuditDataEvent = DataEvent(
    auditSource = IntegrationTestConstants.testAppName,
    auditType = IntegrationTestConstants.Audit.testAuditType,
    tags =  AuditExtensions.auditHeaderCarrier(hc).toAuditTags(IntegrationTestConstants.Audit.testTransactionName, IntegrationTestConstants.testUrl),
    detail = AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(IntegrationTestConstants.Audit.testDetail.toSeq: _*)
  )

  "audit" should {
    "audit a event successfully" in new Setup {
      AuditStub.stubAudit(Status.NO_CONTENT)
      await(service.audit(testAuditModel)) mustBe AuditResult.Success

      val jsonSent = Json.parse(findAll(postRequestedFor(urlMatching(AuditStub.auditUri))).get(0).getBodyAsString)
      val dataEventSent:DataEvent = jsonSent.as[DataEvent](Json.reads[DataEvent])
      dataEventMatcher(testAuditDataEvent, dataEventSent)

    }
    "not throw an exception if the call via the connector fails" in new Setup {
      AuditStub.stubAudit(Status.INTERNAL_SERVER_ERROR, Json.obj())

      await(service.audit(testAuditModel)) mustBe AuditResult.Success
    }
  }
}
