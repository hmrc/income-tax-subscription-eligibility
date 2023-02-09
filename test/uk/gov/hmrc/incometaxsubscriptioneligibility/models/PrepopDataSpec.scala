/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class PrepopDataSpec extends PlaySpec {
  "Pre-pop Data" should {
    "deserialize UK property" in {
      val actual = Json.fromJson[PrepopData](
        Json.parse("""
        {
          "ukPropertyStartDate" : "01012018",
          "ukPropertyAccountingMethod" : "Y"
        }
        """))

      val expected = PrepopData(
        ukProperty = Some(UkProperty(
          ukPropertyStartDate = Some(Date("1", "1", "2018")),
          ukPropertyAccountingMethod = Some(Accruals)
        ))
      )

      actual mustBe JsSuccess(expected)
    }

    "deserialize overseas property" in {
      val actual = Json.fromJson[PrepopData](
        Json.parse(
          """
        {
          "overseasPropertyStartDate" : "01012018",
          "overseasPropertyAccountingMethod" : "Y"
        }
        """))

      val expected = PrepopData(
        overseasProperty = Some(OverseasProperty(
          overseasPropertyStartDate = Some(Date("1", "1", "2018")),
          overseasPropertyAccountingMethod = Some(Accruals)
        ))
      )

      actual mustBe JsSuccess(expected)
    }

    "deserialize self employments" in {
      val actual = Json.fromJson[PrepopData](
        Json.parse("""
        {
          "selfEmployments" : [
            {
               "businessName": "Test business name",
               "businessTradeName": "Test business trade name",
               "businessStartDate": "01012018",
               "businessAccountingMethod": "Y"
            }
          ]
        }
        """))

      val expected = PrepopData(
        selfEmployments = Some(Vector(
          SelfEmploymentData(
            businessName = Some("Test business name"),
            businessTradeName = Some("Test business trade name"),
            businessStartDate = Some(Date("1", "1", "2018")),
            businessAccountingMethod = Some(Accruals)
          )
        ))
      )

      actual mustBe JsSuccess(expected)
    }
  }
}
