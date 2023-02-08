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

class SelfEmploymentDataSpec extends PlaySpec {
  "Self employment data" should {
    "deserialize self employment with complete information" in {
      val actual = Json.fromJson[PrepopData](
        Json.parse("""
        {
          "selfEmployments" : [
            {
               "businessName": "Test business name",
               "businessTradeName": "Test business trade name",
               "businessAddressFirstLine" : "Buckingham Palace",
               "businessAddressPostCode": "SW1A 1AA",
               "businessStartDate": "01012018",
               "businessAccountingMethod": "Y",
               "businessCeasedDate": "20180101"
            }
          ]
        }
        """))

      val expected = PrepopData(
        selfEmployments = Some(Seq(
          SelfEmploymentData(
            businessName = Some("Test business name"),
            businessTradeName = Some("Test business trade name"),
            businessAddressFirstLine = Some("Buckingham Palace"),
            businessAddressPostCode = Some("SW1A 1AA"),
            businessStartDate = Some(Date("1", "1", "2018")),
            businessAccountingMethod = Some(Accruals),
            businessCeasedDate = Some("20180101"),
          )
        ))
      )

      actual mustBe JsSuccess(expected)
    }

    "deserialize self employment with missing business address information" in {
      val actual = Json.fromJson[PrepopData](
        Json.parse("""
        {
          "selfEmployments" : [
            {
               "businessName": "Test business name",
               "businessTradeName": "Test business trade name",
               "businessStartDate": "01012018",
               "businessAccountingMethod": "Y",
               "businessCeasedDate": "20180101"
            }
          ]
        }
        """))

      val expected = PrepopData(
        selfEmployments = Some(Seq(
          SelfEmploymentData(
            businessName = Some("Test business name"),
            businessTradeName = Some("Test business trade name"),
            businessStartDate = Some(Date("1", "1", "2018")),
            businessAccountingMethod = Some(Accruals),
            businessCeasedDate = Some("20180101"),
          )
        ))
      )

      actual mustBe JsSuccess(expected)
    }

    "deserialize self employment with missing business start date" in {
      val actual = Json.fromJson[PrepopData](
        Json.parse("""
        {
          "selfEmployments" : [
            {
               "businessName": "Test business name",
               "businessTradeName": "Test business trade name",
               "businessAddressFirstLine" : "Buckingham Palace",
               "businessAddressPostCode": "SW1A 1AA",
               "businessAccountingMethod": "Y",
               "businessCeasedDate": "20180101"
            }
          ]
        }
        """))

      val expected = PrepopData(
        selfEmployments = Some(Seq(
          SelfEmploymentData(
            businessName = Some("Test business name"),
            businessTradeName = Some("Test business trade name"),
            businessAddressFirstLine = Some("Buckingham Palace"),
            businessAddressPostCode = Some("SW1A 1AA"),
            businessAccountingMethod = Some(Accruals),
            businessCeasedDate = Some("20180101"),
          )
        ))
      )

      actual mustBe JsSuccess(expected)
    }

    "deserialize self employment with missing business accounting method" in {
      val actual = Json.fromJson[PrepopData](
        Json.parse("""
        {
          "selfEmployments" : [
            {
               "businessName": "Test business name",
               "businessTradeName": "Test business trade name",
               "businessAddressFirstLine" : "Buckingham Palace",
               "businessAddressPostCode": "SW1A 1AA",
               "businessStartDate": "01012018",
               "businessCeasedDate": "20180101"
            }
          ]
        }
        """))

      val expected = PrepopData(
        selfEmployments = Some(Seq(
          SelfEmploymentData(
            businessName = Some("Test business name"),
            businessTradeName = Some("Test business trade name"),
            businessAddressFirstLine = Some("Buckingham Palace"),
            businessAddressPostCode = Some("SW1A 1AA"),
            businessStartDate = Some(Date("1", "1", "2018")),
            businessCeasedDate = Some("20180101"),
          )
        ))
      )

      actual mustBe JsSuccess(expected)
    }
  }
}
