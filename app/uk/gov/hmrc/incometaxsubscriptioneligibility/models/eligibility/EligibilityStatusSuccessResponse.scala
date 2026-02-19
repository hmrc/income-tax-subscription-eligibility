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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason._

case class EligibilityStatusSuccessResponse(currentTaxYear: EligibilityStatus,
                                            nextTaxYear: EligibilityStatus,
                                            currentTaxYearFailureReasons: Seq[EligibilityStatusFailureReason],
                                            nextTaxYearFailureReasons: Seq[EligibilityStatusFailureReason])

object EligibilityStatusSuccessResponse {
  implicit val reads: Reads[EligibilityStatusSuccessResponse] = (
    (__ \ "CY").read[EligibilityStatus] and
      (__ \ "CY1").read[EligibilityStatus] and
      (__ \ "ReasonKeyCY").read[Seq[EligibilityStatusFailureReason]] and
      (__ \ "ReasonKeyCY1").read[Seq[EligibilityStatusFailureReason]]
    )(EligibilityStatusSuccessResponse.apply _)

  implicit val writes: OWrites[EligibilityStatusSuccessResponse] = OWrites[EligibilityStatusSuccessResponse] { eligibility =>

    val eligibilityStatusJson: JsObject = Json.obj(
      "eligibleCurrentYear" -> eligibility.currentTaxYear,
      "eligibleNextYear" -> eligibility.nextTaxYear
    )

    val exemptionReason: Option[String] = {

      val digitallyExemptReason: Option[String] = eligibility.nextTaxYearFailureReasons.collectFirst {
        case DigitallyExempt => "Digitally Exempt"
      }

      val mtdExemptEnduringReason: Option[String] = eligibility.nextTaxYearFailureReasons.collectFirst {
        case MTDExemptEnduring |
             MTDExempt28to29 |
             MTDExempt27To28 |
             MarriedCouplesAllowance |
             MinisterOfReligion |
             LloydsUnderwriter |
             BlindPersonsAllowance => "MTD Exempt Enduring"
      }

      val mtdExempt26To27Reason: Option[String] = eligibility.nextTaxYearFailureReasons.collectFirst {
        case MTDExempt26To27 |
             AveragingAdjustment |
             TrustIncome |
             FosterCarers |
             NonResidents => "MTD Exempt 26/27"
      }

      val noDataReason: Option[String] = eligibility.nextTaxYearFailureReasons.collectFirst {
        case NoDataFound |
             Death |
             NonResidentCompanyLandlord |
             BankruptInsolvent |
             BankruptVoluntaryArrangement |
             MandationInhibit26To27 |
             MandationInhibit27To28 => "No Data"
      }

      digitallyExemptReason orElse mtdExemptEnduringReason orElse mtdExempt26To27Reason orElse noDataReason
    }

    val exemptionReasonJson: JsObject = exemptionReason
      .map(reason => Json.obj("exemptionReason" -> reason))
      .getOrElse(Json.obj())

    eligibilityStatusJson ++ exemptionReasonJson

  }
}
