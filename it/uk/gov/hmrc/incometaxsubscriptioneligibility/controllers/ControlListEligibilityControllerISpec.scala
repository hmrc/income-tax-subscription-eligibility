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

import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{AppConfig, StubControlListEligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.AuthStub.stubAuth
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.DesControlListApiStub.stubGetControlList
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.{ComponentSpecBase, ControlListConfigTestHelper}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist._

class ControlListEligibilityControllerISpec extends ComponentSpecBase with ControlListConfigTestHelper {

  override val appConfig: AppConfig = mock[AppConfig]
  val testSautr = "1234567890"
  private val year = "2021-2022"

  def testJson(eligibleCurrent: Boolean, eligibleNext: Boolean): JsObject = Json.obj(
    "eligible" -> eligibleCurrent,
    "eligibleCurrentYear" -> eligibleCurrent,
    "eligibleNextYear" -> eligibleNext
  )

  def testJsonWithPrepopData(eligibleCurrent: Boolean, eligibleNext: Boolean): JsObject = Json.obj(
    "eligible" -> eligibleCurrent,
    "eligibleCurrentYear" -> eligibleCurrent,
    "eligibleNextYear" -> eligibleNext,
    "prepopData" -> Json.obj(
      "ukProperty" -> Json.obj(
        "ukPropertyStartDate" -> Json.obj(
          "day" -> "1",
          "month" -> "1",
          "year" -> "2018"
        ),
        "ukPropertyAccountingMethod" -> "Accruals"
      ),
      "overseasProperty" -> Json.obj(
        "overseasPropertyStartDate" -> Json.obj(
          "day" -> "1",
          "month" -> "1",
          "year" -> "2018"
        )
      ),
      "selfEmployments" -> Json.arr(
        Json.obj(
          "businessName" -> "Test business name",
          "businessTradeName" -> "Test business trade name",
          "businessAddressFirstLine" -> "Buckingham Palace",
          "businessAddressPostCode" -> "SW1A 1AA",
          "businessStartDate" -> Json.obj(
            "day" -> "1",
            "month" -> "1",
            "year" -> "2018"
          ),
          "businessAccountingMethod" -> "Accruals"
        )
      )
    )
  )

  s"A GET request on '/eligibility/$testSautr' route" should {
    "return an OK with '{eligibleCurrent: true}'" when {
      "the feature switch is enabled" in new Server(defaultApp) {
        val testControlListString: String = ControlListHelper(Set()).asBinaryString

        val testDesJson: JsObject = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString
        )

        stubAuth(OK, Json.obj())
        stubGetControlList(testSautr)(OK, testDesJson)
        enable(StubControlListEligible)

        val result: WSResponse = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligibleCurrent = true, eligibleNext = true))
        )
      }
    }

    "return an OK with '{eligibleCurrent: true}'" when {
      "the feature switch is disabled and the returned control list has no parameters set to true" in new Server(defaultApp) {
        val testControlListString: String = ControlListHelper(Set()).asBinaryString

        val testDesJson: JsObject = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString
        )

        stubAuth(OK, Json.obj())
        stubGetControlList(testSautr)(OK, testDesJson)

        val result: WSResponse = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligibleCurrent = true, eligibleNext = true))
        )
      }
    }

    "return an OK with '{eligibleCurrent: true}'" when {
      "the feature switch is disabled and the returned control list has no parameters set to true and all config values are set to ineligible" in
        new Server(app(extraConfig = toConfigList(testAllFalse, year))) {

          val testControlListString: String = ControlListHelper(Set()).asBinaryString

          val testDesJson: JsObject = Json.obj(
            "nino" -> "AA123456A",
            "year" -> "2019",
            "controlListInformation" -> testControlListString
          )

          stubAuth(OK, Json.obj())
          stubGetControlList(testSautr)(OK, testDesJson)

          val result: WSResponse = get(s"/eligibility/$testSautr")

          result must have(
            httpStatus(OK),
            jsonBodyAs(testJson(eligibleCurrent = true, eligibleNext = true))
          )
        }
    }

    "return an OK with '{eligible: false}'" when {
      "the feature switch is disabled and the returned control list has one parameter set to true and one config values is ineligible" in
        new Server(app(extraConfig = toConfigList(Map(NonResidentCompanyLandlord -> false), year))) {

          val testControlListString: String = ControlListHelper(Set(NonResidentCompanyLandlord)).asBinaryString

          val testDesJson: JsObject = Json.obj(
            "nino" -> "AA123456A",
            "year" -> "2019",
            "controlListInformation" -> testControlListString
          )

          stubAuth(OK, Json.obj())
          stubGetControlList(testSautr)(OK, testDesJson)

          val result: WSResponse = get(s"/eligibility/$testSautr")

          result must have(
            httpStatus(OK),
            jsonBodyAs(testJson(eligibleCurrent = false, eligibleNext = false))
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
            DividendsForeign -> false),
          year
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

          stubAuth(OK, Json.obj())
          stubGetControlList(testSautr)(OK, testDesJson)

          val result: WSResponse = get(s"/eligibility/$testSautr")

          result must have(
            httpStatus(OK),
            jsonBodyAs(testJson(eligibleCurrent = false, eligibleNext = false))
          )
        }
    }

    "return an OK with '{eligible: false}'" when {
      "the feature switch is disabled and no control list was found" in new Server(defaultApp) {
        stubAuth(OK, Json.obj())
        stubGetControlList(testSautr)(NOT_FOUND)

        val result: WSResponse = get(s"/eligibility/$testSautr")

        result must have(
          httpStatus(OK),
          jsonBodyAs(testJson(eligibleCurrent = false, eligibleNext = false))
        )
      }
    }

    "return an OK with '{eligible: true}' and pre-pop data" in new Server(defaultApp) {
      val testControlListString: String = ControlListHelper(Set()).asBinaryString

      val testDesJson: JsObject = Json.obj(
        "nino" -> "AA123456A",
        "year" -> "2019",
        "controlListInformation" -> testControlListString,
        "prepopData" -> Json.obj(
          "ukPropertyStartDate" -> "01012018",
          "ukPropertyAccountingMethod" -> "Y",
          "overseasPropertyStartDate" -> "01012018",
          "selfEmployments" -> Json.arr(
            Json.obj(
              "businessName" -> "Test business name",
              "businessTradeName" -> "Test business trade name",
              "businessAddressFirstLine" -> "Buckingham Palace",
              "businessAddressPostCode" -> "SW1A 1AA",
              "businessStartDate" -> "01012018",
              "businessAccountingMethod" -> "Y"
            ),
            Json.obj(
              "businessName" -> "Test business name",
              "businessTradeName" -> "Test business trade name",
              "businessStartDate" -> "01012018",
              "businessAccountingMethod" -> "Y",
              "businessCeasedDate" -> "20180101"
            )
          )
        )
      )

      stubAuth(OK, Json.obj())
      stubGetControlList(testSautr)(OK, testDesJson)
      enable(StubControlListEligible)

      val result: WSResponse = get(s"/eligibility/$testSautr")

      result must have(
        httpStatus(OK),
        jsonBodyAs(testJsonWithPrepopData(eligibleCurrent = true, eligibleNext = true))
      )
    }
  }
}
