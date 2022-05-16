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

package uk.gov.hmrc.incometaxsubscriptioneligibility.connectors

import play.api.Application
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.AppConfig
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.ComponentSpecBase
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.DesControlListApiStub.stubGetControlList
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.{ControlListDataNotFound, GetControlListSuccessResponse}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.NonResidentCompanyLandlord
import uk.gov.hmrc.incometaxsubscriptioneligibility.models._

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global


class GetControlListConnectorISpec extends ComponentSpecBase {

  override val appConfig: AppConfig = appConfig
  def connector(implicit app: Application): GetControlListConnector = app.injector.instanceOf[GetControlListConnector]

  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  val testSautr: String = UUID.randomUUID().toString

  "getControlList" should {
    "return a Right(Set[ControlListParameters])" when {
      "DES returns a valid control list string without pre-pop data" in new App(defaultApp) {
        val testControlListString: String = ControlListHelper(Set(NonResidentCompanyLandlord)).asBinaryString
        val testJson: JsObject = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString
        )

        stubGetControlList(testSautr)(status = OK, body = testJson)

        val res = connector.getControlList(testSautr)

        await(res) mustBe Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord), None))
      }

      "DES returns a valid control list string with pre-pop data" in new App(defaultApp) {
        val testControlListString: String = ControlListHelper(Set(NonResidentCompanyLandlord)).asBinaryString
        val testJson: JsObject = Json.obj(
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
                "businessStartDate" -> "01012018",
                "businessAccountingMethod" -> "Y"
              )
            )
          )
        )

        stubGetControlList(testSautr)(status = OK, body = testJson)

        val res = connector.getControlList(testSautr)

        await(res) mustBe Right(GetControlListSuccessResponse(
          controlList = Set(NonResidentCompanyLandlord),
          prepopData = Some(PrepopData(
            selfEmployments = Some(Vector(
              SelfEmploymentData(
                businessName = Some("Test business name"),
                businessTradeName = Some("Test business trade name"),
                businessStartDate = Some(Date("1", "1", "2018")),
                businessAccountingMethod = Some(Accruals)
              )
            )),
            ukProperty = Some(UkProperty(
              ukPropertyStartDate = Some(Date("1", "1", "2018")),
              ukPropertyAccountingMethod = Some(Accruals)
            )),
            overseasProperty = Some(OverseasProperty(
              overseasPropertyStartDate = Some(Date("1", "1", "2018"))
            ))
          ))
        ))
      }
    }

    "return a Left(ControlListDataNotFound)" when {
      "DES returns a NOT_FOUND" in new App(defaultApp) {
        stubGetControlList(testSautr)(status = NOT_FOUND)

        val res = connector.getControlList(testSautr)

        await(res) mustBe Left(ControlListDataNotFound)
      }
    }
  }
}
