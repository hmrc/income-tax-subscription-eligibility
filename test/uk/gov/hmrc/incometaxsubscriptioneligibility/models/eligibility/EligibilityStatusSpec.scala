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
import play.api.libs.json._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatus.{Eligible, Ineligible}

class EligibilityStatusSpec extends PlaySpec {

  "EligibilityStatus" must {
    "read from json successfully" when {
      "the read json has the value 'Yes'" in {
        Json.fromJson[EligibilityStatus](JsString("Yes")) mustBe JsSuccess(Eligible)
      }
      "the read json has the value 'No'" in {
        Json.fromJson[EligibilityStatus](JsString("No")) mustBe JsSuccess(Ineligible)
      }
    }
    "fail to read from json" when {
      "the read json is not a valid option" in {
        Json.fromJson[EligibilityStatus](JsString("Other")) mustBe JsError("Unsupported eligibility status: Other")
      }
    }

    "write to json successfully" when {
      "the value is 'Eligible'" in {
        Json.toJson[EligibilityStatus](Eligible) mustBe JsBoolean(true)
      }
      "the value is 'Ineligible'" in {
        Json.toJson[EligibilityStatus](Ineligible) mustBe JsBoolean(false)
      }
    }
  }

}
