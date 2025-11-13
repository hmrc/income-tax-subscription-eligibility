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
  }

  lazy val readStringToFailureReason: Map[String, EligibilityStatusFailureReason] = Map(
    NoDataFound.key -> NoDataFound,
    NonResidents.key -> NonResidents,
    NonResidentCompanyLandlord.key -> NonResidentCompanyLandlord,
    DebtManagement.key -> DebtManagement,
    MinisterOfReligion.key -> MinisterOfReligion,
    LloydsUnderwriter.key -> LloydsUnderwriter,
    AveragingAdjustment.key -> AveragingAdjustment,
    TrustIncome.key -> TrustIncome,
    PartnershipIncome.key -> PartnershipIncome,
    BlindPersonsAllowance.key -> BlindPersonsAllowance,
    FosterCarers.key -> FosterCarers,
    MarriedCouplesAllowance.key -> MarriedCouplesAllowance,
    ComplianceActivity.key -> ComplianceActivity,
    BudgetPaymentPlan.key -> BudgetPaymentPlan,
    Death.key -> Death,
    TimeToPay.key -> TimeToPay,
    TimeToPaySelfServe.key -> TimeToPaySelfServe,
    DisguisedRemunerationInvolvement.key -> DisguisedRemunerationInvolvement,
    NoRepaymentSignal.key -> NoRepaymentSignal,
    OutstandingReturns.key -> OutstandingReturns,
    EnforcementSignal.key -> EnforcementSignal,
    CollectionPrioritySignal.key -> CollectionPrioritySignal,
    BankruptInsolvent.key -> BankruptInsolvent,
    BankruptVoluntaryArrangement.key -> BankruptVoluntaryArrangement,
    DigitallyExempt.key -> DigitallyExempt,
    MTDExemptEnduring.key -> MTDExemptEnduring,
    MTDExempt26To27.key -> MTDExempt26To27,
    MTDExempt27To28.key -> MTDExempt27To28,
    MandationInhibit26To27.key -> MandationInhibit26To27,
    MandationInhibit27To28.key -> MandationInhibit27To28,
    "other" -> Other("other")
  )

}
