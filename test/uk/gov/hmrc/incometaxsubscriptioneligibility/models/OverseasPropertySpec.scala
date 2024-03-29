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

class OverseasPropertySpec extends PlaySpec {
  "Overseas property" should {
    "deserialize UK property" when {
      "the accounting method is set to 'Y'" in {
        val actual = Json.fromJson[OverseasProperty](
          Json.parse("""
          {
            "overseasPropertyStartDate" : "01012018",
            "overseasPropertyAccountingMethod" : "Y"
          }
          """))

        val expected = OverseasProperty(
          overseasPropertyStartDate = Some(Date("1", "1", "2018")),
          overseasPropertyAccountingMethod = Some(Accruals)
        )

        actual mustBe JsSuccess(expected)
      }

      "the accounting method is set to 'N'" in {
        val actual = Json.fromJson[OverseasProperty](
          Json.parse("""
          {
            "overseasPropertyStartDate" : "01012018",
            "overseasPropertyAccountingMethod" : "N"
          }
          """))

        val expected = OverseasProperty(
          overseasPropertyStartDate = Some(Date("1", "1", "2018")),
          overseasPropertyAccountingMethod = Some(Cash)
        )

        actual mustBe JsSuccess(expected)
      }

      "the accounting method is not provided" in {
        val actual = Json.fromJson[OverseasProperty](
          Json.parse("""
          {
            "overseasPropertyStartDate" : "01012018"
          }
          """))

        val expected = OverseasProperty(
          overseasPropertyStartDate = Some(Date("1", "1", "2018"))
        )

        actual mustBe JsSuccess(expected)
      }

      "the start date is not provided" in {
        val actual = Json.fromJson[OverseasProperty](
          Json.parse("""
          {
            "overseasPropertyAccountingMethod" : "Y"
          }
          """))

        val expected = OverseasProperty(
          overseasPropertyAccountingMethod = Some(Accruals)
        )

        actual mustBe JsSuccess(expected)
      }

      "the start date and the accounting method are not provided" in {
        val actual = Json.fromJson[OverseasProperty](
          Json.parse("""
          {}
          """))

        val expected = OverseasProperty()

        actual mustBe JsSuccess(expected)
      }
    }
  }
}
