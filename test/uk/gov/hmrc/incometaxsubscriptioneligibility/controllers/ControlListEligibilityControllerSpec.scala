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

package uk.gov.hmrc.incometaxsubscriptioneligibility.controllers

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.models._
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
  val eligible: String = "eligible"
  val eligibleCurrent: String = "eligibleCurrentYear"
  val eligibleNext: String = "eligibleNextYear"
  val prepopData: String = "prepopData"

  "getEligibilityStatus" should {
    val eligibleBoth = Future.successful(EligibilityStatus(eligible = true, eligibleCurrentYear = true, eligibleNextYear = true))
    "return OK with body 'eligible: true'" in {
      mockIsEligible(testSautr, None)(isEligible = eligibleBoth)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligible -> true, eligibleCurrent -> true, eligibleNext -> true)
    }

    "return OK with body 'eligible: false'" in {
      val notEligibleCurrentYear = Future.successful(EligibilityStatus(eligible = false, eligibleCurrentYear = false, eligibleNextYear = true))
      mockIsEligible(testSautr, None)(isEligible = notEligibleCurrentYear)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligible -> false, eligibleCurrent -> false, eligibleNext -> true)
    }

    "return OK with body 'eligible: true' with an Agent" in {
      mockIsEligible(testSautr, testAgentReferenceNumber)(isEligible = eligibleBoth)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set(
        Enrolment("HMRC-AS-AGENT", Seq(EnrolmentIdentifier("AgentReferenceNumber", "123456789")), "Activated")))))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligible -> true, eligibleCurrent -> true, eligibleNext -> true)
    }

    "return OK with body 'eligible: true' with pre-pop data" in {
      val notEligibleCurrentYear = Future.successful(
        EligibilityStatus(
          eligible = false,
          eligibleCurrentYear = false,
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
        eligible -> false,
        eligibleCurrent -> false,
        eligibleNext -> true,
        prepopData -> Json.obj(
          "ukProperty" -> Json.obj(
            "ukPropertyStartDate" -> Json.obj(
              "day" -> "01",
              "month" -> "01",
              "year" -> "2018"
            ),
            "ukPropertyAccountingMethod" -> "Accruals"
          ),
          "overseasProperty" -> Json.obj(
            "overseasPropertyStartDate" -> Json.obj(
              "day" -> "01",
              "month" -> "01",
              "year" -> "2018"
            ),
            "overseasPropertyAccountingMethod" -> "Accruals",
          ),
          "selfEmployments" -> Json.arr(
            Json.obj(
              "businessName" -> "Test business name",
              "businessTradeName" -> "Test business trade name",
              "businessStartDate" -> Json.obj(
                "day" -> "01",
                "month" -> "01",
                "year" -> "2018"
              ),
              "businessAccountingMethod" -> "Accruals"
            )
          )
        )
      )
    }
  }
}
