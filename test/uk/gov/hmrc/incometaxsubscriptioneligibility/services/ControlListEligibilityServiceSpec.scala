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

import play.api.Configuration
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{AppConfig, StubControlListEligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.mocks.MockGetControlListConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.FeatureSwitchingSpec
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits.EligibilityAuditModel
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListIndices.NON_RESIDENT_COMPANY_LANDLORD
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist._
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks.{MockAuditService, MockConvertConfigValuesService}
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class ControlListEligibilityServiceSpec extends FeatureSwitchingSpec
  with MockGetControlListConnector with MockConvertConfigValuesService with MockAuditService {

  val mockServicesConfig: ServicesConfig = mock[ServicesConfig]
  val mockConfiguration: Configuration = mock[Configuration]
  val appConfig = new AppConfig(mockServicesConfig, mockConfiguration)

  object TestControlListEligibilityService extends ControlListEligibilityService(
    mockConvertConfigValuesService,
    mockGetControlListConnector,
    mockAuditService,
    appConfig
  )

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val request: Request[_] = FakeRequest()

  val testSautr: String = "1234567890"
  val testUserTypeAgent: String = "Agent"
  val testUserTypeIndiv: String = "Individual"
  val testAgentReferenceNumber: Option[String] = Some("123456789")

  val successfulCurrentYearAuditModel: EligibilityAuditModel = EligibilityAuditModel(
    utr = testSautr,
    agentReferenceNumber = None,
    controlListCheck = "currentYear"
  )
  val successfulNextYearAuditModel: EligibilityAuditModel = EligibilityAuditModel(
    utr = testSautr,
    agentReferenceNumber = None,
    controlListCheck = "nextYear"
  )

  "getEligibilityStatus" should {
    "return eligible as true" when {
      "StubControlListEligible feature switch is enabled" in {
        enable(StubControlListEligible)

        mockConvertConfigValues(Set(NonResidentCompanyLandlord), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord)))))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, None))

        result.eligibleCurrentYear mustBe true
      }

      "the user's control list data has no parameters set to true" in {
        mockConvertConfigValues(Set(), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set()))))
        mockAudit(successfulCurrentYearAuditModel)(hc, ec, request)(Future.successful(Success))
        mockAudit(successfulNextYearAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, None))

        result.eligibleCurrentYear mustBe true

        verifyAudit(successfulCurrentYearAuditModel)
        verifyAudit(successfulNextYearAuditModel)
      }

      "the user's control list data has a parameter set to true" in {
        mockConvertConfigValues(Set(), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord)))))
        mockAudit(successfulCurrentYearAuditModel)(hc, ec, request)(Future.successful(Success))
        mockAudit(successfulNextYearAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, None))

        result.eligibleCurrentYear mustBe true

        verifyAudit(successfulCurrentYearAuditModel)
        verifyAudit(successfulNextYearAuditModel)
      }

      "the user's control list data has a parameter set to true but is different to the ineligible" in {
        mockConvertConfigValues(Set(StudentLoans), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord)))))
        mockAudit(successfulCurrentYearAuditModel)(hc, ec, request)(Future.successful(Success))
        mockAudit(successfulNextYearAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, None))

        result.eligibleCurrentYear mustBe true

        verifyAudit(successfulCurrentYearAuditModel)
        verifyAudit(successfulNextYearAuditModel)
      }

      "the user's control list data has several parameters set to true" in {
        mockConvertConfigValues(Set(), Set())
        mockGetControlList(testSautr)(hc, ec)(
          Future.successful(Right(GetControlListSuccessResponse(Set(
            NonResidentCompanyLandlord,
            StudentLoans,
            HighIncomeChildBenefit,
            MarriageAllowance
          ))))
        )

        mockAudit(successfulCurrentYearAuditModel)(hc, ec, request)(Future.successful(Success))
        mockAudit(successfulNextYearAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, None))

        result.eligibleCurrentYear mustBe true

        verifyAudit(successfulCurrentYearAuditModel)
        verifyAudit(successfulNextYearAuditModel)
      }

      "the user's control list data has several parameters set to true which are different to the ineligible config values" in {
        mockConvertConfigValues(Set(MinistersOfReligion, FosterCarers, ForeignIncome, Deceased), Set())
        mockGetControlList(testSautr)(hc, ec)(
          Future.successful(Right(GetControlListSuccessResponse(Set(
            NonResidentCompanyLandlord,
            StudentLoans,
            HighIncomeChildBenefit,
            MarriageAllowance
          ))))
        )
        mockAudit(successfulCurrentYearAuditModel)(hc, ec, request)(Future.successful(Success))
        mockAudit(successfulNextYearAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, None))

        result.eligibleCurrentYear mustBe true

        verifyAudit(successfulCurrentYearAuditModel)
        verifyAudit(successfulNextYearAuditModel)
      }
    }

    "return eligible as false" when {
      "the user's control list data is ineligible" in {
        mockConvertConfigValues(Set(NonResidentCompanyLandlord), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord)))))

        val currentYearAuditModel: EligibilityAuditModel = EligibilityAuditModel(
          utr = testSautr,
          agentReferenceNumber = None,
          controlListCheck = "currentYear",
          reasons = Set(ControlListParameter.getParameterMap(NON_RESIDENT_COMPANY_LANDLORD).toString)
        )
        val nextYearAuditModel: EligibilityAuditModel = EligibilityAuditModel(
          utr = testSautr,
          agentReferenceNumber = None,
          controlListCheck = "nextYear",
          reasons = Set.empty[String]
        )

        mockAudit(currentYearAuditModel)(hc, ec, request)(Future.successful(Success))
        mockAudit(nextYearAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, None))

        result.eligibleCurrentYear mustBe false

        verifyAudit(currentYearAuditModel)
        verifyAudit(nextYearAuditModel)
      }

      "the user's control list data is not found" in {
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Left(ControlListDataNotFound)))

        val currentYearAuditModel = EligibilityAuditModel(
          utr = testSautr,
          agentReferenceNumber = testAgentReferenceNumber,
          controlListCheck = "currentYear",
          reasons = Set("No control list data for specified UTR")
        )
        val nextYearAuditModel = EligibilityAuditModel(
          utr = testSautr,
          agentReferenceNumber = testAgentReferenceNumber,
          controlListCheck = "nextYear",
          reasons = Set("No control list data for specified UTR")
        )
        mockAudit(currentYearAuditModel)(hc, ec, request)(Future.successful(Success))
        mockAudit(nextYearAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testAgentReferenceNumber))

        result.eligibleCurrentYear mustBe false

        verifyAudit(currentYearAuditModel)
        verifyAudit(nextYearAuditModel)
      }

      "the user's control list data is in an incorrect format" in {
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Left(InvalidControlListFormat)))

        val currentYearAuditModel = EligibilityAuditModel(
          utr = testSautr,
          agentReferenceNumber = testAgentReferenceNumber,
          controlListCheck = "currentYear",
          reasons = Set("Incorrectly formatted control list")
        )
        val nextYearAuditModel = EligibilityAuditModel(
          utr = testSautr,
          agentReferenceNumber = testAgentReferenceNumber,
          controlListCheck = "nextYear",
          reasons = Set("Incorrectly formatted control list")
        )
        mockAudit(currentYearAuditModel)(hc, ec, request)(Future.successful(Success))
        mockAudit(nextYearAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testAgentReferenceNumber))

        result.eligibleCurrentYear mustBe false

        verifyAudit(currentYearAuditModel)
        verifyAudit(nextYearAuditModel)
      }
    }

    "return pre-pop data without ceased businesses" in {
      mockConvertConfigValues(Set(), Set())
      mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(
        GetControlListSuccessResponse(
          Set(),
          Some(PrepopData(
            selfEmployments = Some(Seq(
              SelfEmploymentData(
                businessName = Some("Test business name"),
                businessTradeName = Some("Test business trade name"),
                businessStartDate = Some(Date("01", "01", "2018")),
                businessAccountingMethod = Some(Accruals)
              ),
              SelfEmploymentData(
                businessName = Some("Test business name 2"),
                businessTradeName = Some("Test business trade name"),
                businessStartDate = Some(Date("01", "01", "2018")),
                businessAccountingMethod = Some(Accruals),
                businessCeasedDate = Some("01012018")
              )
            )),
          ))
        )
      )))

      mockAudit(successfulCurrentYearAuditModel)(hc, ec, request)(Future.successful(Success))
      mockAudit(successfulNextYearAuditModel)(hc, ec, request)(Future.successful(Success))

      val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, None))

      result mustBe EligibilityStatus(
        eligible = true,
        eligibleCurrentYear = true,
        eligibleNextYear = true,
        prepopData = Some(PrepopData(
          selfEmployments = Some(Seq(
            SelfEmploymentData(
              businessName = Some("Test business name"),
              businessTradeName = Some("Test business trade name"),
              businessStartDate = Some(Date("01", "01", "2018")),
              businessAccountingMethod = Some(Accruals)
            )
          )),
        ))
      )

      verifyAudit(successfulCurrentYearAuditModel)
      verifyAudit(successfulNextYearAuditModel)
    }
  }
}
