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

import play.api.libs.json.{JsSuccess, Reads, __}

sealed trait EligibilityStatusFailureReason {
  val key: String
  def message: String = key
}

object EligibilityStatusFailureReason {

  case object NoDataFound extends EligibilityStatusFailureReason {
    val key: String = "No Data Found"
    override val message: String = "No control list data for specified UTR"
  }

  case object NonResidents extends EligibilityStatusFailureReason {
    val key: String = "Non-residents"
    override val message: String = "Declared on SA109"
  }

  case object NonResidentCompanyLandlord extends EligibilityStatusFailureReason {
    val key: String = "Non-resident Company Landlord"
    override val message: String = "Non Resident Company Landlord"
  }

  case object MinisterOfReligion extends EligibilityStatusFailureReason {
    val key: String = "Minister of Religion"
    override val message: String = "Minister Of Religion"
  }

  case object LloydsUnderwriter extends EligibilityStatusFailureReason {
    val key: String = "Lloyds Underwriter"
    override val message: String = "Lloyds Underwriter"
  }

  case object AveragingAdjustment extends EligibilityStatusFailureReason {
    val key: String = "Averaging Adjustment"
    override val message: String = "Claims Everything"
  }

  case object TrustIncome extends EligibilityStatusFailureReason {
    val key: String = "Trust Income"
    override val message: String = "Declares Income From Trust"
  }

  case object PartnershipIncome extends EligibilityStatusFailureReason {
    val key: String = "Partnership Income"
    override val message: String = "Declares Partnership Profits"
  }

  case object BlindPersonsAllowance extends EligibilityStatusFailureReason {
    val key: String = "Blind Person's Allowance"
    override val message: String = "Receives Blind Person's Allowance"
  }

  case object FosterCarers extends EligibilityStatusFailureReason {
    val key: String = "Foster Carers"
    override val message: String = "Foster Carer"
  }

  case object MarriedCouplesAllowance extends EligibilityStatusFailureReason {
    val key: String = "Married Couple's Allowance"
    override val message: String = "Receives Married Couple's Allowance"
  }

  case object ComplianceActivity extends EligibilityStatusFailureReason {
    val key: String = "Compliance Activity"
    override val message: String = "Open Enquiries"
  }

  case object DebtManagement extends EligibilityStatusFailureReason {
    val key: String = "Debt Management"
    override val message: String = "Existing debt passed to IDMS"
  }

  case object BudgetPaymentPlan extends EligibilityStatusFailureReason {
    val key: String = "Budget Payment Plan"
    override val message: String = "Budget Payment Plan"
  }

  case object Death extends EligibilityStatusFailureReason {
    val key: String = "Death"
    override val message: String = "Deceased"
  }

  case object Capacitor extends EligibilityStatusFailureReason {
    val key: String = "Capacitor"
    override val message: String = "Capacitor"
  }

  case object TimeToPay extends EligibilityStatusFailureReason {
    val key: String = "Time To Pay"
    override val message: String = "Time To Pay Arrangement"
  }

  case object TimeToPaySelfServe extends EligibilityStatusFailureReason {
    val key: String = "Time To Pay (Self Serve)"
    override val message: String = "Self Serve Time To Pay Arrangement"
  }

  case object DisguisedRemunerationInvolvement extends EligibilityStatusFailureReason {
    val key: String = "Disguised Renumeration Involvement"
    override val message: String = "In A Disguised Renumeration Scheme"
  }

  case object NoRepaymentSignal extends EligibilityStatusFailureReason {
    val key: String = "No Repayment Signal"
    override val message: String = "No Repayment"
  }

  case object OutstandingReturns extends EligibilityStatusFailureReason {
    val key: String = "Outstanding Returns"
    override val message: String = "Outstanding Returns"
  }

  case object EnforcementSignal extends EligibilityStatusFailureReason {
    val key: String = "Enforcement Signal"
    override val message: String = "Enforcement"
  }

  case object CollectionPrioritySignal extends EligibilityStatusFailureReason {
    val key: String = "Collection Priority Signal"
    override val message: String = "Collection Priority"
  }

  case object BankruptInsolvent extends EligibilityStatusFailureReason {
    val key: String = "Bankrupt - Insolvent"
    override val message: String = "Insolvent"
  }

  case object BankruptVoluntaryArrangement extends EligibilityStatusFailureReason {
    val key: String = "Bankrupt - Voluntary Arrangement"
    override val message: String = "Voluntary Arrangement"
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

  case object MTDExempt28To29 extends EligibilityStatusFailureReason {
    val key: String = "MTD Exempt 28/29"
  }

  case object MandationInhibit26To27 extends EligibilityStatusFailureReason {
    val key: String = "Mandation Inhibit 26/27"
  }

  case object MandationInhibit27To28 extends EligibilityStatusFailureReason {
    val key: String = "Mandation Inhibit 27/28"
  }

  case class Other(key: String) extends EligibilityStatusFailureReason

  implicit val reads: Reads[EligibilityStatusFailureReason] = __.read[String].flatMapResult {
    case NoDataFound.key => JsSuccess(NoDataFound)
    case NonResidents.key => JsSuccess(NonResidents)
    case NonResidentCompanyLandlord.key => JsSuccess(NonResidentCompanyLandlord)
    case MinisterOfReligion.key => JsSuccess(MinisterOfReligion)
    case LloydsUnderwriter.key => JsSuccess(LloydsUnderwriter)
    case AveragingAdjustment.key => JsSuccess(AveragingAdjustment)
    case TrustIncome.key => JsSuccess(TrustIncome)
    case PartnershipIncome.key => JsSuccess(PartnershipIncome)
    case BlindPersonsAllowance.key => JsSuccess(BlindPersonsAllowance)
    case FosterCarers.key => JsSuccess(FosterCarers)
    case MarriedCouplesAllowance.key => JsSuccess(MarriedCouplesAllowance)
    case ComplianceActivity.key => JsSuccess(ComplianceActivity)
    case DebtManagement.key => JsSuccess(DebtManagement)
    case BudgetPaymentPlan.key => JsSuccess(BudgetPaymentPlan)
    case Death.key => JsSuccess(Death)
    case Capacitor.key => JsSuccess(Capacitor)
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
    case MTDExempt28To29.key => JsSuccess(MTDExempt28To29)
    case MandationInhibit26To27.key => JsSuccess(MandationInhibit26To27)
    case MandationInhibit27To28.key => JsSuccess(MandationInhibit27To28)
    case otherKey => JsSuccess(Other(otherKey))
  }
}

