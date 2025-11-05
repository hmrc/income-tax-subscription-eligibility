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

sealed trait EligibilityStatusFailureReason {
  val key: String
}

object EligibilityStatusFailureReason {

  case object NoDataFound extends EligibilityStatusFailureReason {
    val key: String = "No Data Found"
  }

  case object NonResidents extends EligibilityStatusFailureReason {
    val key: String = "Non-residents"
  }

  case object NonResidentCompanyLandlord extends EligibilityStatusFailureReason {
    val key: String = "Non-resident Company Landlord"
  }

  case object DebtManagement extends EligibilityStatusFailureReason {
    val key: String = "Debt Management"
  }

  case object MinisterOfReligion extends EligibilityStatusFailureReason {
    val key: String = "Minister of Religion"
  }

  case object LloydsUnderwriter extends EligibilityStatusFailureReason {
    val key: String = "Lloyds Underwriter"
  }

  case object AveragingAdjustment extends EligibilityStatusFailureReason {
    val key: String = "Averaging Adjustment"
  }

  case object TrustIncome extends EligibilityStatusFailureReason {
    val key: String = "Trust Income"
  }

  case object PartnershipIncome extends EligibilityStatusFailureReason {
    val key: String = "Partnership Income"
  }

  case object BlindPersonsAllowance extends EligibilityStatusFailureReason {
    val key: String = "Blind Person's Allowance"
  }

  case object FosterCarers extends EligibilityStatusFailureReason {
    val key: String = "Foster Carers"
  }

  case object MarriedCouplesAllowance extends EligibilityStatusFailureReason {
    val key: String = "Married Couple's Allowance"
  }

  case object ComplianceActivity extends EligibilityStatusFailureReason {
    val key: String = "Compliance Activity"
  }

  case object BudgetPaymentPlan extends EligibilityStatusFailureReason {
    val key: String = "Budget Payment Plan"
  }

  case object Death extends EligibilityStatusFailureReason {
    val key: String = "Death"
  }

  case object TimeToPay extends EligibilityStatusFailureReason {
    val key: String = "Time To Pay"
  }

  case object TimeToPaySelfServe extends EligibilityStatusFailureReason {
    val key: String = "Time To Pay (Self Serve)"
  }

  case object DisguisedRemunerationInvolvement extends EligibilityStatusFailureReason {
    val key: String = "Disguised Renumeration Involvement"
  }

  case object NoRepaymentSignal extends EligibilityStatusFailureReason {
    val key: String = "No Repayment Signal"
  }

  case object OutstandingReturns extends EligibilityStatusFailureReason {
    val key: String = "Outstanding Returns"
  }

  case object EnforcementSignal extends EligibilityStatusFailureReason {
    val key: String = "Enforcement Signal"
  }

  case object CollectionPrioritySignal extends EligibilityStatusFailureReason {
    val key: String = "Collection Priority Signal"
  }

  case object BankruptInsolvent extends EligibilityStatusFailureReason {
    val key: String = "Bankrupt - Insolvent"
  }

  case object BankruptVoluntaryArrangement extends EligibilityStatusFailureReason {
    val key: String = "Bankrupt - Voluntary Arrangement"
  }

  case object DigitallyExempt extends EligibilityStatusFailureReason {
    val key: String = "Digitally Exempt"
  }

  case object MTDExemptEnduring extends EligibilityStatusFailureReason {
    val key: String = "MTD Exempt (Enduring)"
  }

  case object MTDExempt26To27 extends EligibilityStatusFailureReason {
    val key: String = "MTD Exempt 26/27"
  }

  case object MTDExempt27To28 extends EligibilityStatusFailureReason {
    val key: String = "MTD Exempt 27/28"
  }

  case object MandationInhibit26To27 extends EligibilityStatusFailureReason {
    val key: String = "Mandation Inhibit 26/27"
  }

  case object MandationInhibit27To28 extends EligibilityStatusFailureReason {
    val key: String = "Mandation Inhibit 27/28"
  }

  implicit val reads: Reads[EligibilityStatusFailureReason] = __.read[String].flatMapResult {
    case NoDataFound.key => JsSuccess(NoDataFound)
    case NonResidents.key => JsSuccess(NonResidents)
    case NonResidentCompanyLandlord.key => JsSuccess(NonResidentCompanyLandlord)
    case DebtManagement.key => JsSuccess(DebtManagement)
    case MinisterOfReligion.key => JsSuccess(MinisterOfReligion)
    case LloydsUnderwriter.key => JsSuccess(LloydsUnderwriter)
    case AveragingAdjustment.key => JsSuccess(AveragingAdjustment)
    case TrustIncome.key => JsSuccess(TrustIncome)
    case PartnershipIncome.key => JsSuccess(PartnershipIncome)
    case BlindPersonsAllowance.key => JsSuccess(BlindPersonsAllowance)
    case FosterCarers.key => JsSuccess(FosterCarers)
    case MarriedCouplesAllowance.key => JsSuccess(MarriedCouplesAllowance)
    case ComplianceActivity.key => JsSuccess(ComplianceActivity)
    case BudgetPaymentPlan.key => JsSuccess(BudgetPaymentPlan)
    case Death.key => JsSuccess(Death)
    case TimeToPay.key => JsSuccess(TimeToPay)
    case TimeToPaySelfServe.key => JsSuccess(TimeToPaySelfServe)
    case DisguisedRemunerationInvolvement.key => JsSuccess(DisguisedRemunerationInvolvement)
    case NoRepaymentSignal.key => JsSuccess(NoRepaymentSignal)
    case OutstandingReturns.key => JsSuccess(OutstandingReturns)
    case EnforcementSignal.key => JsSuccess(EnforcementSignal)
    case CollectionPrioritySignal.key => JsSuccess(CollectionPrioritySignal)
    case BankruptInsolvent.key => JsSuccess(BankruptInsolvent)
    case BankruptVoluntaryArrangement.key => JsSuccess(BankruptVoluntaryArrangement)
    case DigitallyExempt.key => JsSuccess(DigitallyExempt)
    case MTDExemptEnduring.key => JsSuccess(MTDExemptEnduring)
    case MTDExempt26To27.key => JsSuccess(MTDExempt26To27)
    case MTDExempt27To28.key => JsSuccess(MTDExempt27To28)
    case MandationInhibit26To27.key => JsSuccess(MandationInhibit26To27)
    case MandationInhibit27To28.key => JsSuccess(MandationInhibit27To28)
    case other => JsError(s"Unexpected eligibility failure reason: $other")
  }
}
