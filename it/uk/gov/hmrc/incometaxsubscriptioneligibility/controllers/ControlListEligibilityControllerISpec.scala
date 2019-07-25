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

import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.StubControlListEligible
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.ComponentSpecBase

class ControlListEligibilityControllerISpec extends ComponentSpecBase {

  val testSautr = "1234567890"

  def testJson(eligibility: Boolean): JsObject = Json.obj("eligible" -> eligibility)

  s"A GET request on '/eligibility/$testSautr' route" should {
    "return an OK with '{eligible: true}'" when {
      "the feature switch is on" in new Server(defaultApp) {
        enable(StubControlListEligible)

        val result = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligibility = true))
        )
      }
    }
    "return an OK with '{eligible: false}'" when {
      "the feature switch is off" in new Server(defaultApp) {
        // Don't need to disable feature switches as they are disabled by an overridden method in ComponentSpecBase
        val result = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligibility = false))
        )
      }
    }
  }

}