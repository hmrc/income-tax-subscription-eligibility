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

import play.api.libs.json._

sealed trait EligibilityStatus

object EligibilityStatus {

  case object Eligible extends EligibilityStatus

  case object Ineligible extends EligibilityStatus

  implicit val reads: Reads[EligibilityStatus] = __.read[String].flatMapResult {
    case "Yes" => JsSuccess(Eligible)
    case "No" => JsSuccess(Ineligible)
    case other => JsError(s"Unsupported eligibility status: $other")
  }

  implicit val writes: Writes[EligibilityStatus] = Writes[EligibilityStatus] {
    case Eligible => JsBoolean(true)
    case Ineligible => JsBoolean(false)
  }

}
