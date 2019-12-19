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

package uk.gov.hmrc.incometaxsubscriptioneligibility.controllers

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.mocks.MockAuthConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks.MockControlListEligibilityService

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}


class ControlListEligibilityControllerSpec extends PlaySpec with MockControlListEligibilityService with MockAuthConnector {

  object TestControlListEligibilityController
    extends ControlListEligibilityController(stubControllerComponents(), mockControlListEligibilityService, mockAuthConnector)

  val testSautr = "1234567890"
  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  val eligibleKey: String = "eligible"

  "getEligibilityStatus" should {
    "return OK with body 'eligible: true'" in {
      mockIsEligible(testSautr)(isEligible = Future.successful(true), isAgent = false)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligibleKey -> true)
    }

    "return OK with body 'eligible: false'" in {
      mockIsEligible(testSautr)(isEligible = Future.successful(false), isAgent = false)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set())))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligibleKey -> false)
    }

    "return OK with body 'eligible: true' with an Agent" in {
      mockIsEligible(testSautr)(isEligible = Future.successful(true), isAgent = true)
      mockAuthorise(EmptyPredicate, Retrievals.allEnrolments)(Future.successful(Enrolments(Set(Enrolment("HMRC-AS-AGENT")))))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligibleKey -> true)
    }
  }
}
