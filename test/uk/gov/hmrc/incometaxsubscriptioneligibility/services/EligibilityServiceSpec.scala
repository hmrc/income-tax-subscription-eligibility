/*
 * Copyright 2023 HM Revenue & Customs
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

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{AppConfig, FeatureSwitching, StubControlListEligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.mocks.MockEligibilityStatusConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.ControlListDataNotFound
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits.EligibilityAuditModel
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListMessages
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatus.{Eligible, Ineligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason.*
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.{EligibilityStatusFailure, EligibilityStatusSuccessResponse}
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks.MockAuditService
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EligibilityServiceSpec extends PlaySpec
  with MockEligibilityStatusConnector with MockAuditService with FeatureSwitching with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    disable(StubControlListEligible)
  }

  val mockServicesConfig: ServicesConfig = mock[ServicesConfig]
  val mockConfiguration: Configuration = mock[Configuration]
  val appConfig = new AppConfig(mockServicesConfig, mockConfiguration)

  object TestEligibilityService extends EligibilityService(
    mockAuditService,
    mockEligibilityStatusConnector
  )(appConfig)

  implicit val request: Request[_] = FakeRequest()
  implicit val hc: HeaderCarrier = HeaderCarrier()

  val testNino: String = "test-nino"
  val testUTR: String = "test-utr"
  val testARN: String = "test-arn"

  "getEligibilityStatus" when {
    "the stub eligibility feature switch is enabled" should {
      "return a successful eligibility response" in {
        enable(StubControlListEligible)

        val result = TestEligibilityService.getEligibilityStatus(testNino, testUTR, None)

        await(result) mustBe Right(EligibilityStatusSuccessResponse(Eligible, Eligible, Seq.empty, Seq.empty))
      }
    }
    "the stub eligibility feature switch is enabled" should {
      "return a successful eligibility response" when {
        "a successful response is returned from the connector" which {
          "audits the full details of the response" when {
            "there is no agent reference number available" in {
              mockGetEligibilityStatus(testNino, testUTR)(Right(eligibilityStatusSuccessResponseEligible))
              mockAudit(currentYearEligibleAuditModel)(hc, global, request)(Future.successful(Success))
              mockAudit(nextYearEligibleAuditModel)(hc, global, request)(Future.successful(Success))

              val result = TestEligibilityService.getEligibilityStatus(testNino, testUTR, None)

              await(result) mustBe Right(eligibilityStatusSuccessResponseEligible)
            }
            "there is an agent reference number available" in {
              mockGetEligibilityStatus(testNino, testUTR)(Right(eligibilityStatusSuccessResponseIneligible))
              mockAudit(currentYearIneligibleAuditModel)(hc, global, request)(Future.successful(Success))
              mockAudit(nextYearIneligibleAuditModel)(hc, global, request)(Future.successful(Success))

              val result = TestEligibilityService.getEligibilityStatus(testNino, testUTR, Some(testARN))

              await(result) mustBe Right(eligibilityStatusSuccessResponseIneligible)
            }
          }
        }
      }
      "return a failure eligibility response" when {
        "a failure response is returned from the connector" in {
          mockGetEligibilityStatus(testNino, testUTR)(Left(EligibilityStatusFailure.UnexpectedStatus))

          val result = TestEligibilityService.getEligibilityStatus(testNino, testUTR, None)

          await(result) mustBe Left(EligibilityStatusFailure.UnexpectedStatus)
        }
      }
    }
  }

  lazy val eligibilityStatusSuccessResponseEligible = EligibilityStatusSuccessResponse(
    currentTaxYear = Eligible,
    nextTaxYear = Eligible,
    currentTaxYearFailureReasons = Seq.empty,
    nextTaxYearFailureReasons = Seq.empty
  )

  lazy val eligibilityStatusSuccessResponseIneligible = EligibilityStatusSuccessResponse(
    currentTaxYear = Ineligible,
    nextTaxYear = Ineligible,
    currentTaxYearFailureReasons = allFailures,
    nextTaxYearFailureReasons = allFailures
  )

  lazy val allFailures = Seq(
    NoDataFound,
    NonResidents,
    NonResidentCompanyLandlord,
    MinisterOfReligion,
    LloydsUnderwriter,
    AveragingAdjustment,
    TrustIncome,
    PartnershipIncome,
    BlindPersonsAllowance,
    FosterCarers,
    MarriedCouplesAllowance,
    ComplianceActivity,
    DebtManagement,
    BudgetPaymentPlan,
    Death,
    Capacitor,
    TimeToPay,
    TimeToPaySelfServe,
    DisguisedRemunerationInvolvement,
    NoRepaymentSignal,
    OutstandingReturns,
    EnforcementSignal,
    CollectionPrioritySignal,
    BankruptInsolvent,
    BankruptVoluntaryArrangement,
    DigitallyExempt,
    MTDExemptEnduring,
    MTDExempt26To27,
    MTDExempt27To28,
    MTDExempt28To29,
    MandationInhibit26To27,
    MandationInhibit27To28
  )

  lazy val allReasons = Set(
    ControlListDataNotFound.errorMessage,
    ControlListMessages.nonResidentsMessage,
    ControlListMessages.nonResidentCompanyLandlordMessage,
    ControlListMessages.existingIdmsDebtMessage,
    ControlListMessages.ministersOfReligionMessage,
    ControlListMessages.lloydsUnderwriterMessage,
    ControlListMessages.averagingAdjustmentMessage,
    ControlListMessages.trustIncomeMessage,
    ControlListMessages.partnershipIncomeMessage,
    ControlListMessages.blindPersonsAllowanceMessage,
    ControlListMessages.fosterCarersMessage,
    ControlListMessages.marriedCouplesAllowanceMessage,
    ControlListMessages.complianceMessage,
    ControlListMessages.budgetPaymentPlanMessage,
    ControlListMessages.deceasedMessage,
    ControlListMessages.capacitorMessage,
    ControlListMessages.timeToPayMessage,
    ControlListMessages.selfServeTimeToPayMessage,
    ControlListMessages.disguisedRenumerationInvlovementMessage,
    ControlListMessages.noRepaymentMessage,
    ControlListMessages.outstandingReturnsMessage,
    ControlListMessages.enforcementMessage,
    ControlListMessages.collectionPriorityMessage,
    ControlListMessages.bankruptInsolvementMessage,
    ControlListMessages.bankruptVoluntaryArrangementMessage,
    DigitallyExempt.key,
    MTDExemptEnduring.key,
    MTDExempt26To27.key,
    MTDExempt27To28.key,
    MTDExempt28To29.key,
    MandationInhibit26To27.key,
    MandationInhibit27To28.key
  )

  lazy val currentYearEligibleAuditModel = EligibilityAuditModel(
    utr = testUTR,
    agentReferenceNumber = None,
    controlListCheck = "currentYear",
    reasons = Set()
  )
  lazy val nextYearEligibleAuditModel = EligibilityAuditModel(
    utr = testUTR,
    agentReferenceNumber = None,
    controlListCheck = "nextYear",
    reasons = Set()
  )
  lazy val currentYearIneligibleAuditModel = EligibilityAuditModel(
    utr = testUTR,
    agentReferenceNumber = Some(testARN),
    controlListCheck = "currentYear",
    reasons = allReasons
  )
  lazy val nextYearIneligibleAuditModel = EligibilityAuditModel(
    utr = testUTR,
    agentReferenceNumber = Some(testARN),
    controlListCheck = "nextYear",
    reasons = allReasons
  )

}
