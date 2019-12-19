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

import java.util.UUID

import play.api.Application
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.ComponentSpecBase
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.DesControlListApiStub.stubGetControlList
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.ControlListDataNotFound
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.NonResidentCompanyLandlord

import scala.concurrent.ExecutionContext.Implicits.global


class GetControlListConnectorISpec extends ComponentSpecBase {

  def connector(implicit app: Application): GetControlListConnector = app.injector.instanceOf[GetControlListConnector]

  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  val testSautr: String = UUID.randomUUID().toString

  "getControlList" should {
    "return a Right(Set[ControlListParameters])" when {
      "DES returns a valid control list string" in new App(defaultApp) {
        val testControlListString: String = ControlListHelper(Set(NonResidentCompanyLandlord)).asBinaryString
        val testJson: JsObject = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString
        )

        stubGetControlList(testSautr)(status = OK, body = testJson)

        val res = connector.getControlList(testSautr)

        await(res) mustBe Right(Set(NonResidentCompanyLandlord))
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
