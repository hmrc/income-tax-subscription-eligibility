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

import play.api.mvc.Request
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.StubControlListEligible
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.mocks.MockGetControlListConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.FeatureSwitchingSpec
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.ControlListDataNotFound
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

  val successfulAuditModel: EligibilityAuditModel = EligibilityAuditModel(true, testSautr, false)

  "getEligibilityStatus" should {
    "return true" when {
      "feature switch is enabled" in {
        enable(StubControlListEligible)

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, false))

        result mustBe true
      }
    }

    "return true" when {
      "feature switch is disabled and the user's control list data has no parameters set to true" in {
        mockConvertConfigValues(Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(Set())))
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))


        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, false))

        result mustBe true
      }
    }

    "return true" when {
      "feature switch is disabled and the user's control list data has a parameter set to true" in {
        mockConvertConfigValues(Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(Set(NonResidentCompanyLandlord))))
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, false))

        result mustBe true
      }
    }

    "return true" when {
      "feature switch is disabled and the user's control list data has a parameter set to true but is different to the ineligible" in {
        mockConvertConfigValues(Set(StudentLoans))
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(Set(NonResidentCompanyLandlord))))
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, false))

        result mustBe true
      }
    }

    "return true" when {
      "feature switch is disabled and the user's control list data has several parameters set to true" in {
        mockConvertConfigValues(Set())
        mockGetControlList(testSautr)(hc, ec)(
          Future.successful(Right(Set(
            NonResidentCompanyLandlord,
            StudentLoans,
            HighIncomeChildBenefit,
            MarriageAllowance
          )))
        )
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, false))

        result mustBe true
      }
    }

    "return true" when {
      "feature switch is disabled and the user's control list data has several parameters set to true which are different to the ineligible config values" in {
        mockConvertConfigValues(Set(
          MinistersOfReligion,
          FosterCarers,
          ForeignIncome,
          Deceased
        ))
        mockGetControlList(testSautr)(hc, ec)(
          Future.successful(Right(Set(
            NonResidentCompanyLandlord,
            StudentLoans,
            HighIncomeChildBenefit,
            MarriageAllowance
          )))
        )
        mockAudit(successfulAuditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, false))

        result mustBe true
      }
    }

    "return false" when {
      "feature switch is disabled and the user's control list data is ineligible" in {
        mockConvertConfigValues(Set(NonResidentCompanyLandlord))
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Right(Set(NonResidentCompanyLandlord))))

        val auditModel: EligibilityAuditModel = EligibilityAuditModel(false, testSautr, false, Seq(ControlListParameter.getParameterMap(NON_RESIDENT_COMPANY_LANDLORD)))
        mockAudit(auditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, false))

        result mustBe false
      }
    }

    "return false" when {
      "feature switch is disabled and the user's control list data is not found" in {
        mockConvertConfigValues(Set())
        mockGetControlList(testSautr)(hc, ec)(Future.successful(Left(ControlListDataNotFound)))

        val auditModel: EligibilityAuditModel = EligibilityAuditModel(false, testSautr, true)
        mockAudit(auditModel)(hc, ec, request)(Future.successful(Success))

        val result = await(TestControlListEligibilityService.getEligibilityStatus(testSautr, true))

        result mustBe false
      }
    }
  }

}
