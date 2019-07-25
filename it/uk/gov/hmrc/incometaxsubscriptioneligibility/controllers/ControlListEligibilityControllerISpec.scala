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
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.StubControlListEligible
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.DesControlListApiStub.stubGetControlList
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.{ComponentSpecBase, ControlListConfigTestHelper}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist._

class ControlListEligibilityControllerISpec extends ComponentSpecBase with ControlListConfigTestHelper {

  val testSautr = "1234567890"

  def testJson(eligible: Boolean): JsObject = Json.obj("eligible" -> eligible)

  s"A GET request on '/eligibility/$testSautr' route" should {
    "return an OK with '{eligible: true}'" when {
      "the feature switch is enabled" in new Server(defaultApp) {
        enable(StubControlListEligible)

        val result: WSResponse = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligible = true))
        )
      }
    }

    "return an OK with '{eligible: true}'" when {
      "the feature switch is disabled and the returned control list has no parameters set to true" in new Server(defaultApp) {
        val testControlListString: String = ControlListHelper(Set()).asBinaryString

        val testDesJson: JsObject = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
        "controlListInformation" -> testControlListString
        )

        stubGetControlList(testSautr)(OK, testDesJson)

        val result: WSResponse = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligible = true))
        )
      }
    }

    "return an OK with '{eligible: true}'" when {
      "the feature switch is disabled and the returned control list has no parameters set to true and all config values are set to ineligible" in
        new Server(app(extraConfig = toConfigList(testAllFalse))) {

        val testControlListString: String = ControlListHelper(Set()).asBinaryString

        val testDesJson: JsObject = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
        "controlListInformation" -> testControlListString
        )

        stubGetControlList(testSautr)(OK, testDesJson)

        val result: WSResponse = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligible = true))
        )
      }
    }

    "return an OK with '{eligible: false}'" when {
      "the feature switch is disabled and the returned control list has one parameter set to true and one config values is ineligible" in
        new Server(app(extraConfig = toConfigList(Map(NonResidentCompanyLandlord -> false)))) {

        val testControlListString: String = ControlListHelper(Set(NonResidentCompanyLandlord)).asBinaryString

        val testDesJson: JsObject = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
        "controlListInformation" -> testControlListString
        )

        stubGetControlList(testSautr)(OK, testDesJson)

        val result: WSResponse = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligible = false))
        )
      }
    }

    "return an OK with '{eligible: false}'" when {
      "the feature switch is disabled and the returned control list has several parameters set to true and the matching config values are ineligible" in
        new Server(app(extraConfig = toConfigList(
            Map(
              NonResidentCompanyLandlord -> false,
              StudentLoans -> false,
              MinistersOfReligion -> false,
              DividendsForeign -> false)
        ))) {

        val testControlListString: String = ControlListHelper(Set(
          NonResidentCompanyLandlord,
          StudentLoans,
          MinistersOfReligion,
          DividendsForeign
        )).asBinaryString

        val testDesJson: JsObject = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
        "controlListInformation" -> testControlListString
        )

        stubGetControlList(testSautr)(OK, testDesJson)

        val result: WSResponse = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligible = false))
        )
      }
    }

    "return an OK with '{eligible: false}'" when {
      "the feature switch is disabled and no control list was found" in new Server(defaultApp) {
        stubGetControlList(testSautr)(NOT_FOUND)

        val result: WSResponse = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligible = false))
        )
      }
    }

  }

}