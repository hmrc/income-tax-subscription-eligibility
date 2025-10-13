/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsObject, JsSuccess, Json, __}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatus.{Eligible, Ineligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason.DigitallyExempt

class EligibilityStatusSuccessResponseSpec extends PlaySpec {

  val fullReadJson: JsObject = Json.obj(
    "CY" -> "No",
    "CY1" -> "Yes",
    "ReasonKeyCY" -> Json.arr(
      "Digitally Exempt"
    ),
    "ReasonKeyCY1" -> Json.arr()
  )

  val fullModel: EligibilityStatusSuccessResponse = EligibilityStatusSuccessResponse(
    currentTaxYear = Ineligible,
    nextTaxYear = Eligible,
    currentTaxYearFailureReasons = Seq(DigitallyExempt),
    nextTaxYearFailureReasons = Seq.empty
  )

  val fullWriteJson: JsObject = Json.obj(
    "eligibleCurrentYear" -> false,
    "eligibleNextYear" -> true
  )

  "EligibilityStatusSuccessResponse" must {
    "read from json successfully" when {
      "all fields are present in the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson) mustBe JsSuccess(fullModel)
      }
    }
    "fail to read from json" when {
      "'CY' is missing from the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson - "CY") mustBe JsError(__ \ "CY", "error.path.missing")
      }
      "'CY1' is missing from the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson - "CY1") mustBe JsError(__ \ "CY1", "error.path.missing")
      }
      "'ReasonKeyCY' is missing from the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson - "ReasonKeyCY") mustBe JsError(__ \ "ReasonKeyCY", "error.path.missing")
      }
      "'ReasonKeyCY1' is missing from the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson - "ReasonKeyCY1") mustBe JsError(__ \ "ReasonKeyCY1", "error.path.missing")
      }
    }

    "write to json successfully" in {
      Json.toJson[EligibilityStatusSuccessResponse](fullModel) mustBe fullWriteJson
    }
  }

}
