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

import play.api.libs.json.{JsError, JsSuccess, Reads, __}

sealed trait EligibilityStatusFailureReason

object EligibilityStatusFailureReason {

  case object NoDataFound extends EligibilityStatusFailureReason

  case object NonResidents extends EligibilityStatusFailureReason

  case object NonResidentCompanyLandlord extends EligibilityStatusFailureReason

  case object DebtManagement extends EligibilityStatusFailureReason

  case object MinisterOfReligion extends EligibilityStatusFailureReason

  case object LloydsUnderwriter extends EligibilityStatusFailureReason

  case object AveragingAdjustment extends EligibilityStatusFailureReason

  case object TrustIncome extends EligibilityStatusFailureReason

  case object PartnershipIncome extends EligibilityStatusFailureReason

  case object BlindPersonsAllowance extends EligibilityStatusFailureReason

  case object FosterCarers extends EligibilityStatusFailureReason

  case object MarriedCouplesAllowance extends EligibilityStatusFailureReason

  case object ComplianceActivity extends EligibilityStatusFailureReason

  case object BudgetPaymentPlan extends EligibilityStatusFailureReason

  case object Death extends EligibilityStatusFailureReason

  case object TimeToPay extends EligibilityStatusFailureReason

  case object TimeToPaySelfServe extends EligibilityStatusFailureReason

  case object DisguisedRemunerationInvolvement extends EligibilityStatusFailureReason

  case object NoRepaymentSignal extends EligibilityStatusFailureReason

  case object OutstandingReturns extends EligibilityStatusFailureReason

  case object EnforcementSignal extends EligibilityStatusFailureReason

  case object CollectionPrioritySignal extends EligibilityStatusFailureReason

  case object BankruptInsolvent extends EligibilityStatusFailureReason

  case object BankruptVoluntaryArrangement extends EligibilityStatusFailureReason

  case object DigitallyExempt extends EligibilityStatusFailureReason

  case object MTDExceptEnduring extends EligibilityStatusFailureReason

  case object MTDExcept26To27 extends EligibilityStatusFailureReason

  case object MTDExcept27To28 extends EligibilityStatusFailureReason

  case object MandationInhibit26To27 extends EligibilityStatusFailureReason

  case object MandationInhibit27To28 extends EligibilityStatusFailureReason

  implicit val reads: Reads[EligibilityStatusFailureReason] = __.read[String].flatMapResult {
    case "No Data Found" => JsSuccess(NoDataFound)
    case "Non-residents" => JsSuccess(NonResidents)
    case "Non-resident Company Landlord" => JsSuccess(NonResidentCompanyLandlord)
    case "Debt Management" => JsSuccess(DebtManagement)
    case "Minister of Religion" => JsSuccess(MinisterOfReligion)
    case "Lloyds Underwriter" => JsSuccess(LloydsUnderwriter)
    case "Averaging Adjustment" => JsSuccess(AveragingAdjustment)
    case "Trust Income" => JsSuccess(TrustIncome)
    case "Partnership Income" => JsSuccess(PartnershipIncome)
    case "Blind Person's Allowance" => JsSuccess(BlindPersonsAllowance)
    case "Foster Carers" => JsSuccess(FosterCarers)
    case "Married Couple's Allowance" => JsSuccess(MarriedCouplesAllowance)
    case "Compliance Activity" => JsSuccess(ComplianceActivity)
    case "Budget Payment Plan" => JsSuccess(BudgetPaymentPlan)
    case "Death" => JsSuccess(Death)
    case "Time To Pay" => JsSuccess(TimeToPay)
    case "Time To Pay (Self Serve)" => JsSuccess(TimeToPaySelfServe)
    case "Disguised Renumeration Involvement" => JsSuccess(DisguisedRemunerationInvolvement)
    case "No Repayment Signal" => JsSuccess(NoRepaymentSignal)
    case "Outstanding Returns" => JsSuccess(OutstandingReturns)
    case "Enforcement Signal" => JsSuccess(EnforcementSignal)
    case "Collection Priority Signal" => JsSuccess(CollectionPrioritySignal)
    case "Bankrupt - Insolvent" => JsSuccess(BankruptInsolvent)
    case "Bankrupt - Voluntary Arrangement" => JsSuccess(BankruptVoluntaryArrangement)
    case "Digitally Exempt" => JsSuccess(DigitallyExempt)
    case "MTD Exempt (Enduring)" => JsSuccess(MTDExceptEnduring)
    case "MTD Exempt 26/27" => JsSuccess(MTDExcept26To27)
    case "MTD Exempt 27/28" => JsSuccess(MTDExcept27To28)
    case "Mandation Inhibit 26/27" => JsSuccess(MandationInhibit26To27)
    case "Mandation Inhibit 27/28" => JsSuccess(MandationInhibit27To28)
    case other => JsError(s"Unexpected eligibility failure reason: $other")
  }
}
