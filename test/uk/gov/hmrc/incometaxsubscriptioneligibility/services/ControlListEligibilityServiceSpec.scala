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

import play.api.mvc.Request
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.StubControlListEligible
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.mocks.MockGetControlListConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.FeatureSwitchingSpec
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.{ControlListDataNotFound, GetControlListSuccessResponse, InvalidControlListFormat}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.{Accruals, Date, EligibilityStatus, PrepopData, SelfEmploymentData}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits.EligibilityAuditModel
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListIndices.NON_RESIDENT_COMPANY_LANDLORD
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist._
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks.{MockAuditService, MockConvertConfigValuesService}
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class ControlListEligibilityServiceSpec extends FeatureSwitchingSpec
  with MockGetControlListConnector with MockConvertConfigValuesService with MockAuditService {

  object TestControlListEligibilityService extends ControlListEligibilityService(mockConvertConfigValuesService, mockGetControlListConnector, mockAuditService)

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val request: Request[_] = FakeRequest()

  val testSautr: String = "1234567890"
  val testUserTypeAgent: String = "Agent"
  val testUserTypeIndiv: String = "Individual"
  val testAgentReferenceNumber: Option[String] = Some("123456789")

  val successfulAuditModel: EligibilityAuditModel = EligibilityAuditModel(eligibilityResult = true, testSautr, testUserTypeIndiv, None)

  "getEligibilityStatus" should {
    "return eligible as true" when {
      "StubControlListEligible feature switch is enabled" in {
        enable(StubControlListEligible)

        mockConvertConfigValues(Set(NonResidentCompanyLandlord), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord)))))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeIndiv, None))

        result.eligibleCurrentYear mustBe true
      }

      "the user's control list data has no parameters set to true" in {
        mockConvertConfigValues(Set(), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set()))))
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeIndiv, None))

        result.eligibleCurrentYear mustBe true
      }

      "the user's control list data has a parameter set to true" in {
        mockConvertConfigValues(Set(), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord)))))
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeIndiv, None))

        result.eligibleCurrentYear mustBe true
      }

      "the user's control list data has a parameter set to true but is different to the ineligible" in {
        mockConvertConfigValues(Set(StudentLoans), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord)))))
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeIndiv, None))

        result.eligibleCurrentYear mustBe true
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
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeIndiv, None))

        result.eligibleCurrentYear mustBe true
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
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeIndiv, None))

        result.eligibleCurrentYear mustBe true
      }
    }

    "return eligible as false" when {
      "the user's control list data is ineligible" in {
        mockConvertConfigValues(Set(NonResidentCompanyLandlord), Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord)))))

        val auditModel: EligibilityAuditModel = EligibilityAuditModel(
          eligibilityResult = false,
          testSautr,
          testUserTypeIndiv,
          None,
          Seq(ControlListParameter.getParameterMap(NON_RESIDENT_COMPANY_LANDLORD).toString)
        )
        mockAudit(auditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeIndiv, None))

        result.eligibleCurrentYear mustBe false
      }

      "the user's control list data is not found" in {
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Left(ControlListDataNotFound)))

        val auditModel: EligibilityAuditModel = EligibilityAuditModel(eligibilityResult = false, testSautr, testUserTypeAgent, testAgentReferenceNumber,
          Seq("No control list data for specified UTR"))
        mockAudit(auditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeAgent, testAgentReferenceNumber))

        result.eligibleCurrentYear mustBe false
      }

      "the user's control list data is in an incorrect format" in {
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Left(InvalidControlListFormat)))

        val auditModel: EligibilityAuditModel = EligibilityAuditModel(eligibilityResult = false, testSautr, testUserTypeAgent, testAgentReferenceNumber,
          Seq("Incorrectly formatted control list"))
        mockAudit(auditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeAgent, testAgentReferenceNumber))

        result.eligibleCurrentYear mustBe false
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
      mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

      val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, testUserTypeIndiv, None))

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
    }
  }
}
