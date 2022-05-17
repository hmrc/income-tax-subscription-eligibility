/*
 * Copyright 2022 HM Revenue & Customs
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

import org.scalamock.matchers.ArgCapture._
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{ConfigLoader, Configuration}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.AppConfig
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.{FeatureSwitchingSpec, TestConstants}
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.DataEvent
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuditServiceSpec extends FeatureSwitchingSpec with MockFactory with OneInstancePerTest {
  val mockServicesConfig: ServicesConfig = mock[ServicesConfig]
  val mockConfiguration: Configuration = mock[Configuration]
  val appConfig = new AppConfig(mockServicesConfig, mockConfiguration)
  val mockAuditConnector = mock[AuditConnector]

  lazy val testAuditService = new AuditService(mockConfiguration, mockAuditConnector)

  private implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request = FakeRequest(POST, TestConstants.testUrl)

  val testAuditModel = new AuditModel {
    override val auditType = TestConstants.Audit.testAuditType
    override val transactionName = TestConstants.Audit.testTransactionName
    override val detail = TestConstants.Audit.testDetail
  }
  val testAuditDataEvent = DataEvent(
    auditSource = TestConstants.testAppName,
    auditType = TestConstants.Audit.testAuditType,
    tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(TestConstants.Audit.testTransactionName, TestConstants.testUrl),
    detail = AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(TestConstants.Audit.testDetail.toSeq: _*)
  )

  "audit" when {
    "given a AuditModel" should {
      s"extract the data and pass it into the AuditConnector and return ${AuditResult.Success} verifying audit event sent" in {
        (mockConfiguration.get[String](_: String)(_: ConfigLoader[String]))
          .expects(*, *)
          .returns(TestConstants.testAppName).noMoreThanOnce()
        val captureOfAuditDataEvent = CaptureOne[DataEvent]()

        (mockAuditConnector.sendEvent(_: DataEvent)(_: HeaderCarrier, _: ExecutionContext))
          .expects(new CaptureMatcher(captureOfAuditDataEvent), *, *)
          .returns(Future.successful(AuditResult.Success))

        val resOfAuditCall = await(testAuditService.audit(testAuditModel))
        dataEventMatcher(testAuditDataEvent, captureOfAuditDataEvent.value)
        resOfAuditCall mustBe AuditResult.Success
      }
      s"return a ${AuditResult.Failure} if the AuditConnector returns a ${AuditResult.Failure}" in {
        val failure = AuditResult.Failure(TestConstants.randomStringWithNoMeaning, Some(new Exception()))

        (mockConfiguration.get[String](_: String)(_: ConfigLoader[String]))
          .expects(*, *)
          .returns(TestConstants.testAppName).noMoreThanOnce()

        (mockAuditConnector.sendEvent(_: DataEvent)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(
            Future.successful(failure)
          )

        val resOfAuditCall = await(testAuditService.audit(testAuditModel))

        resOfAuditCall mustBe failure
      }
    }
  }

  def dataEventMatcher(testDataEvent: DataEvent, capturedDataEvent: DataEvent): Unit = {
    testDataEvent.auditSource mustBe capturedDataEvent.auditSource
    testDataEvent.auditType mustBe capturedDataEvent.auditType
    capturedDataEvent.eventId.isEmpty mustBe false
    testDataEvent.tags mustBe capturedDataEvent.tags
    capturedDataEvent.generatedAt.toString().isEmpty mustBe false
  }
}
