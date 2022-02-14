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
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.EligibilityByYear
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

  "getEligibilityStatus" should {
    val eligibleBoth = Future.successful(EligibilityByYear(current = true, next = true))
    "return OK with body 'eligible: true'" in {
      mockIsEligible(testSautr, testUserTypeIndiv, None)(isEligible = eligibleBoth)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligible -> true, eligibleCurrent -> true, eligibleNext -> true)
    }

    "return OK with body 'eligible: false'" in {
      val notEligibleCurrentYear = Future.successful(EligibilityByYear(current = false, next = true))
      mockIsEligible(testSautr, testUserTypeIndiv, None)(isEligible = notEligibleCurrentYear)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligible -> false, eligibleCurrent -> false, eligibleNext -> true)
    }

    "return OK with body 'eligible: true' with an Agent" in {
      mockIsEligible(testSautr, testUserTypeAgent, testAgentReferenceNumber)(isEligible = eligibleBoth)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set(
        Enrolment("HMRC-AS-AGENT", Seq(EnrolmentIdentifier("AgentReferenceNumber", "123456789")), "Activated")))))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligible -> true, eligibleCurrent -> true, eligibleNext -> true)
    }
  }
}
