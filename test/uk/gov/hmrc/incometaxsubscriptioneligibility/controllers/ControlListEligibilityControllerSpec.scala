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

package uk.gov.hmrc.incometaxsubscriptioneligibility.controllers

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.ControlListDataNotFound
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.*
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.{AveragingAdjustment, BankruptInsolvent, BankruptVoluntaryArrangement, BlindPersonsAllowance, Capacitor, Deceased, FosterCarers, LloydsUnderwriter, MarriedCouplesAllowance, MinistersOfReligion, NonResidentCompanyLandlord, NonResidents, TrustIncome}
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks.MockControlListEligibilityService

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}


class ControlListEligibilityControllerSpec extends PlaySpec with MockControlListEligibilityService with MockAuthConnector {

  object TestControlListEligibilityController
    extends ControlListEligibilityController(stubControllerComponents(), mockControlListEligibilityService, mockAuthConnector)

  val testSautr = "1234567890"
  val testUserTypeIndiv = "individual"
  val testUserTypeAgent = "agent"
  val testAgentReferenceNumber: Option[String] = Some("123456789")
  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  val eligibleCurrent: String = "eligibleCurrentYear"
  val eligibleNext: String = "eligibleNextYear"

  "getEligibilityStatus" should {
    val eligibleBoth = Future.successful(EligibilityStatus(eligible = true, eligibleCurrentYear = true, nextYearFailureReasons = Set.empty, eligibleNextYear = true))
    "return OK with body 'eligible: true'" in {
      mockIsEligible(testSautr, None)(isEligible = eligibleBoth)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligibleCurrent -> true, eligibleNext -> true)
    }

    "return OK with body 'eligible: false'" when {
      "including a exemption reason of MTD Exempt Enduring" when {
        "next year includes Married Couples Allowance as a failure reason" in {
          val failureReasons: Set[String] = Set(MarriedCouplesAllowance.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "MTD Exempt Enduring")
        }
        "next year includes Ministers Of Religion as a failure reason" in {
          val failureReasons: Set[String] = Set(MinistersOfReligion.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "MTD Exempt Enduring")
        }
        "next year includes Lloyds Underwriter as a failure reason" in {
          val failureReasons: Set[String] = Set(LloydsUnderwriter.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "MTD Exempt Enduring")
        }
        "next year includes Blind Persons Allowance as a failure reason" in {
          val failureReasons: Set[String] = Set(BlindPersonsAllowance.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "MTD Exempt Enduring")
        }
      }
      "including a exemption reason of MTD Exempt 26/27" when {
        "next year includes Averaging Adjustment as a failure reason" in {
          val failureReasons: Set[String] = Set(AveragingAdjustment.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "MTD Exempt 26/27")
        }
        "next year includes Trust Income as a failure reason" in {
          val failureReasons: Set[String] = Set(TrustIncome.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "MTD Exempt 26/27")
        }
        "next year includes Foster Carers as a failure reason" in {
          val failureReasons: Set[String] = Set(FosterCarers.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "MTD Exempt 26/27")
        }
        "next year includes Non Residents as a failure reason" in {
          val failureReasons: Set[String] = Set(NonResidents.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "MTD Exempt 26/27")
        }
      }
      "including a exemption reason of No Data" when {
        "next year includes Control List Data Not Found as a failure reason" in {
          val failureReasons: Set[String] = Set(ControlListDataNotFound.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "No Data")
        }
        "next year includes Deceased as a failure reason" in {
          val failureReasons: Set[String] = Set(Deceased.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "No Data")
        }
        "next year includes Non Resident Company Landlord as a failure reason" in {
          val failureReasons: Set[String] = Set(NonResidentCompanyLandlord.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "No Data")
        }
        "next year includes Bankrupt Insolvent as a failure reason" in {
          val failureReasons: Set[String] = Set(BankruptInsolvent.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "No Data")
        }
        "next year includes Bankrupt Voluntary Arrangement as a failure reason" in {
          val failureReasons: Set[String] = Set(BankruptVoluntaryArrangement.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "No Data")
        }
        "next year includes Capacitor as a failure reason" in {
          val failureReasons: Set[String] = Set(Capacitor.errorMessage)
          val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = failureReasons, eligibleNextYear = true))
          mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
          mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

          val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

          status(result) mustBe OK
          contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true, "exemptionReason" -> "No Data")
        }
      }
      "not including an exemption reason" in {
        val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, nextYearFailureReasons = Set.empty, eligibleNextYear = true))
        mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
        mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

        val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.obj(eligibleCurrent -> false, eligibleNext -> true)
      }
    }

    "return OK with body 'eligible: true' with an Agent" in {
      mockIsEligible(testSautr, testAgentReferenceNumber)(isEligible = eligibleBoth)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set(
        Enrolment("HMRC-AS-AGENT", Seq(EnrolmentIdentifier("AgentReferenceNumber", "123456789")), "Activated")))))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligibleCurrent -> true, eligibleNext -> true)
    }

    "return OK with body 'eligible: true' with pre-pop data" in {
      val notEligibleCurrentYear = Future.successful(
        EligibilityStatus(
          eligible = false,
          eligibleCurrentYear = false,
          eligibleNextYear = true,
          nextYearFailureReasons = Set.empty,
          prepopData = Some(PrepopData(
            selfEmployments = Some(Seq(
              SelfEmploymentData(
                businessName = Some("Test business name"),
                businessTradeName = Some("Test business trade name"),
                businessStartDate = Some(Date("01", "01", "2018")),
                businessAccountingMethod = Some(Accruals)
              )
            )),
            ukProperty = Some(UkProperty(
              ukPropertyStartDate = Some(Date("01", "01", "2018")),
              ukPropertyAccountingMethod = Some(Accruals)
            )),
            overseasProperty = Some(OverseasProperty(
              overseasPropertyStartDate = Some(Date("01", "01", "2018")),
              overseasPropertyAccountingMethod = Some(Accruals)
            ))
          ))
        )
      )
      mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(
        eligibleCurrent -> false,
        eligibleNext -> true
      )
    }
  }
}
