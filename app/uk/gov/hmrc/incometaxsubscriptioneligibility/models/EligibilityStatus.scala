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

import play.api.libs.json.{JsObject, Json, OWrites}
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.ControlListDataNotFound
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.*

case class EligibilityStatus(eligible: Boolean,
                             eligibleCurrentYear: Boolean,
                             eligibleNextYear: Boolean,
                             nextYearFailureReasons: Set[String],
                             prepopData: Option[PrepopData] = None)

object EligibilityStatus {
  implicit val writes: OWrites[EligibilityStatus] = OWrites { eligibilityStatus =>

    val eligibilityStatusJson = Json.obj(
      "eligibleCurrentYear" -> eligibilityStatus.eligibleCurrentYear,
      "eligibleNextYear" -> eligibilityStatus.eligibleNextYear
    )

    val exemptionReason: Option[String] = {

      val mtdExemptEnduringReason: Option[String] = eligibilityStatus.nextYearFailureReasons.collectFirst {
        case MarriedCouplesAllowance.errorMessage |
             MinistersOfReligion.errorMessage |
             LloydsUnderwriter.errorMessage |
             BlindPersonsAllowance.errorMessage => "MTD Exempt Enduring"
      }

      val mtdExempt26To27Reason: Option[String] = eligibilityStatus.nextYearFailureReasons.collectFirst {
        case AveragingAdjustment.errorMessage |
             TrustIncome.errorMessage |
             FosterCarers.errorMessage |
             NonResidents.errorMessage => "MTD Exempt 26/27"
      }

      val noDataReason: Option[String] = eligibilityStatus.nextYearFailureReasons.collectFirst {
        case ControlListDataNotFound.errorMessage |
             Deceased.errorMessage |
             NonResidentCompanyLandlord.errorMessage |
             BankruptInsolvent.errorMessage |
             BankruptVoluntaryArrangement.errorMessage |
             Capacitor.errorMessage => "No Data"
      }

      mtdExemptEnduringReason orElse mtdExempt26To27Reason orElse noDataReason
    }

    val exemptionReasonJson: JsObject = exemptionReason
      .map(reason => Json.obj("exemptionReason" -> reason))
      .getOrElse(Json.obj())

    eligibilityStatusJson ++ exemptionReasonJson

  }
}

case class EligibilityByYear(current: Set[String], next: Set[String])
