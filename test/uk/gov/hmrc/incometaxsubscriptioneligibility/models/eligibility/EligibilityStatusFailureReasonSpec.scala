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
import play.api.libs.json.{JsError, JsString, JsSuccess, Json}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason._

class EligibilityStatusFailureReasonSpec extends PlaySpec {

  "EligibilityStatusFailureResponse" must {
    "read from json successfully" when {
      readStringToFailureReason foreach { case (key, reason) =>
        s"the read json has the value '$key'" in {
          Json.fromJson[EligibilityStatusFailureReason](JsString(key)) mustBe JsSuccess(reason)
        }
      }
    }
    "fail to read the json successfully" when {
      "the read json does not have a recognised value" in {
        Json.fromJson[EligibilityStatusFailureReason](JsString("Other")) mustBe JsError("Unexpected eligibility failure reason: Other")
      }
    }
  }

  lazy val readStringToFailureReason: Map[String, EligibilityStatusFailureReason] = Map(
    "No Data Found" -> NoDataFound,
    "Non-residents" -> NonResidents,
    "Non-resident Company Landlord" -> NonResidentCompanyLandlord,
    "Debt Management" -> DebtManagement,
    "Minister of Religion" -> MinisterOfReligion,
    "Lloyds Underwriter" -> LloydsUnderwriter,
    "Averaging Adjustment" -> AveragingAdjustment,
    "Trust Income" -> TrustIncome,
    "Partnership Income" -> PartnershipIncome,
    "Blind Person's Allowance" -> BlindPersonsAllowance,
    "Foster Carers" -> FosterCarers,
    "Married Couple's Allowance" -> MarriedCouplesAllowance,
    "Compliance Activity" -> ComplianceActivity,
    "Budget Payment Plan" -> BudgetPaymentPlan,
    "Death" -> Death,
    "Time To Pay" -> TimeToPay,
    "Time To Pay (Self Serve)" -> TimeToPaySelfServe,
    "Disguised Renumeration Involvement" -> DisguisedRemunerationInvolvement,
    "No Repayment Signal" -> NoRepaymentSignal,
    "Outstanding Returns" -> OutstandingReturns,
    "Enforcement Signal" -> EnforcementSignal,
    "Collection Priority Signal" -> CollectionPrioritySignal,
    "Bankrupt - Insolvent" -> BankruptInsolvent,
    "Bankrupt - Voluntary Arrangement" -> BankruptVoluntaryArrangement,
    "Digitally Exempt" -> DigitallyExempt,
    "MTD Exempt (Enduring)" -> MTDExceptEnduring,
    "MTD Exempt 26/27" -> MTDExcept26To27,
    "MTD Exempt 27/28" -> MTDExcept27To28,
    "Mandation Inhibit 26/27" -> MandationInhibit26To27,
    "Mandation Inhibit 27/28" -> MandationInhibit27To28,
  )

}
