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
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks.MockControlListEligibilityService

import scala.concurrent.ExecutionContext.Implicits.global


class ControlListEligibilityControllerSpec extends PlaySpec
  with MockControlListEligibilityService {

  val testSautr = "1234567890"
  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  "getEligibilityStatus" should {
    "return OK with body '{eligible: true}'" in {
      mockIsEligible(testSautr)(isEligible = true)

      val controller = new ControlListEligibilityController(stubControllerComponents(), mockControlListEligibilityService)
      val result = controller.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.obj("eligible" -> true)
    }

    "return OK with body '{eligible: false}'" in {
      mockIsEligible(testSautr)(isEligible = false)

      val controller = new ControlListEligibilityController(stubControllerComponents(), mockControlListEligibilityService)
      val result = controller.getEligibilityStatus(testSautr)(fakeRequest)

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.obj("eligible" -> false)
    }
  }

}
