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
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks.MockControlListEligibilityService

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}


class ControlListEligibilityControllerSpec extends PlaySpec with MockControlListEligibilityService {

  object TestControlListEligibilityController extends ControlListEligibilityController(stubControllerComponents(), mockControlListEligibilityService)

  val testSautr = "1234567890"
  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  val eligibleKey: String = "eligible"

  "getEligibilityStatus" should {
    "return OK with body '{eligible: true}'" in {
      mockIsEligible(testSautr)(hc, ec)(isEligible = Future.successful(true))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligibleKey -> true)
    }

    "return OK with body '{eligible: false}'" in {
      mockIsEligible(testSautr)(hc, ec)(isEligible = Future.successful(false))

      val result = TestControlListEligibilityController.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj(eligibleKey -> false)
    }
  }

}
