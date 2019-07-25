/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist

import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListIndices._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListMessages._

sealed trait ControlListParameter {
  val configKey: String
  val ordinal: Int
  val errorMessage: String

  override def toString: String = errorMessage
}

case object NonResidentCompanyLandlord extends ControlListParameter {
  val configKey: String = "non-resident-company-landlord"
  val ordinal: Int = NON_RESIDENT_COMPANY_LANDLORD
  val errorMessage: String = nonResidentCompanyLandlordMessage
}

case object StudentLoans extends ControlListParameter {
  val configKey: String = "student-loans"
  val ordinal: Int = STUDENT_LOANS
  val errorMessage: String = studentLoansMessage
}

case object MarriageAllowance extends ControlListParameter {
  val configKey: String = "marriage-allowance"
  val ordinal: Int = MARRIAGE_ALLOWANCE
  val errorMessage: String = marriageAllowanceMessage
}

case object EmploymentIncome extends ControlListParameter {
  val configKey: String = "employment-income"
  val ordinal: Int = EMPLOYMENT_INCOME
  val errorMessage: String = employmentIncomeMessage
}

case object Compliance extends ControlListParameter {
  val configKey: String = "compliance"
  val ordinal: Int = COMPLIANCE
  val errorMessage: String = complianceMessage
}

case object ExistingIdmsDebt extends ControlListParameter {
  val configKey: String = "existing-idms-debt"
  val ordinal: Int = EXISTING_IDMS_DEBT
  val errorMessage: String = existingIdmsDebtMessage
}

case object BudgetPaymentPlan extends ControlListParameter {
  val configKey: String = "budget-payment-plan"
  val ordinal: Int = BUDGET_PAYMENT_PLAN
  val errorMessage: String = budgetPaymentPlanMessage
}

case object Deceased extends ControlListParameter {
  val configKey: String = "deceased"
  val ordinal: Int = DECEASED
  val errorMessage: String = deceasedMessage
}

case object TimeToPay extends ControlListParameter {
  val configKey: String = "time-to-pay"
  val ordinal: Int = TIME_TO_PAY
  val errorMessage: String = timeToPayMessage
}

case object SelfServeTimeToPay extends ControlListParameter {
  val configKey: String = "self-serve-time-to-pay"
  val ordinal: Int = SELF_SERVE_TIME_TO_PAY
  val errorMessage: String = selfServeTimeToPayMessage
}

case object NoRepayment extends ControlListParameter {
  val configKey: String = "no-repayment"
  val ordinal: Int = NO_REPAYMENT
  val errorMessage: String = noRepaymentMessage
}

case object OutstandingReturns extends ControlListParameter {
  val configKey: String = "outstanding-returns"
  val ordinal: Int = OUTSTANDING_RETURNS
  val errorMessage: String = outstandingReturnsMessage
}

case object RlsCase extends ControlListParameter {
  val configKey: String = "rls-case"
  val ordinal: Int = RLS_CASE
  val errorMessage: String = rlsCaseMessage
}

case object Enforcement extends ControlListParameter {
  val configKey: String = "enforcement"
  val ordinal: Int = ENFORCEMENT
  val errorMessage: String = enforcementMessage
}

case object CollectionPriority extends ControlListParameter {
  val configKey: String = "collection-priority"
  val ordinal: Int = COLLECTION_PRIORITY
  val errorMessage: String = collectionPriorityMessage
}

case object FailureToNotify extends ControlListParameter {
  val configKey: String = "failure-to-notify"
  val ordinal: Int = FAILURE_TO_NOTIFY
  val errorMessage: String = failureToNotifyMessage
}

case object BankruptInsolvent extends ControlListParameter {
  val configKey: String = "bankrupt-insolvent"
  val ordinal: Int = BANKRUPT_INSOLVENT
  val errorMessage: String = bankruptInsolvementMessage
}

case object BankruptVoluntaryArrangement extends ControlListParameter {
  val configKey: String = "bankrupt-voluntary-arrangement"
  val ordinal: Int = BANKRUPT_VOLUNTARY_ARRANGEMENT
  val errorMessage: String = bankruptVoluntaryArrangementMessage
}

case object MultipleSelfEmployed extends ControlListParameter {
  val configKey: String = "multiple-self-employed"
  val ordinal: Int = MULTIPLE_SELF_EMPLOYED
  val errorMessage: String = multipleSelfEmployedMessage
}

case object NonResidents extends ControlListParameter {
  val configKey: String = "non-residents"
  val ordinal: Int = NON_RESIDENTS
  val errorMessage: String = nonResidentsMessage
}

case object MinistersOfReligion extends ControlListParameter {
  val configKey: String = "ministers-of-religion"
  val ordinal: Int = MINISTERS_OF_RELIGION
  val errorMessage: String = ministersOfReligionMessage
}

case object LloydsUnderwriter extends ControlListParameter {
  val configKey: String = "lloyds-underwriter"
  val ordinal: Int = LLOYDS_UNDERWRITER
  val errorMessage: String = lloydsUnderwriterMessage
}

case object ClassTwoNationalInsuranceContributionsVoluntary extends ControlListParameter {
  val configKey: String = "class-two-national-insurance-contributions-voluntary"
  val ordinal: Int = CLASS_TWO_NATIONAL_INSURANCE_CONTRIBUTIONS_VOLUNTARY
  val errorMessage: String = classTwoNationalInsuranceContributionVoluntaryMessage
}

case object BanksAndBuildingSocietyInterestForeign extends ControlListParameter {
  val configKey: String = "banks-and-building-society-interest-foreign"
  val ordinal: Int = BANKS_AND_BUILDING_SOCIETY_INTEREST_FOREIGN
  val errorMessage: String = banksAndBuildingSocietyInterestForeignMessage
}

case object DividendsForeign extends ControlListParameter {
  val configKey: String = "dividends-foreign"
  val ordinal: Int = DIVIDENDS_FOREIGN
  val errorMessage: String = dividendsForeignMessage
}

case object PensionContributions extends ControlListParameter {
  val configKey: String = "pension-contributions"
  val ordinal: Int = PENSION_CONTRIBUTIONS
  val errorMessage: String = pensionContributionsMessage
}

case object PensionIncome extends ControlListParameter {
  val configKey: String = "pension-income"
  val ordinal: Int = PENSION_INCOME
  val errorMessage: String = pensionIncomeMessage
}

case object ConstructionInductionSchemeDeductions extends ControlListParameter {
  val configKey: String = "construction-industry-scheme-deductions"
  val ordinal: Int = CONSTRUCTION_INDUSTRY_SCHEME_DEDUCTIONS
  val errorMessage: String = constructionIndustrySchemeDeductionsMessage
}

case object Sa101AdditionalInformation extends ControlListParameter {
  val configKey: String = "sa101-additional-information"
  val ordinal: Int = SA101_ADDITIONAL_INFORMATION
  val errorMessage: String = sa101AdditionalInformationMessage
}

case object AveragingAdjustment extends ControlListParameter {
  val configKey: String = "averaging-adjustment"
  val ordinal: Int = AVERAGING_ADJUSTMENT
  val errorMessage: String = averagingAdjustmentMessage
}

case object CapitalGainsTax extends ControlListParameter {
  val configKey: String = "capital-gains-tax"
  val ordinal: Int = CAPITAL_GAINS_TAX
  val errorMessage: String = capitalGainsTaxMessage
}

case object ForeignIncome extends ControlListParameter {
  val configKey: String = "foreign-income"
  val ordinal: Int = FOREIGN_INCOME
  val errorMessage: String = foreignIncomeMessage
}

case object TrustIncome extends ControlListParameter {
  val configKey: String = "trust-income"
  val ordinal: Int = TRUST_INCOME
  val errorMessage: String = trustIncomeMessage
}

case object PartnershipIncome extends ControlListParameter {
  val configKey: String = "partnership-income"
  val ordinal: Int = PARTNERSHIP_INCOME
  val errorMessage: String = partnershipIncomeMessage
}

case object HighIncomeChildBenefit extends ControlListParameter {
  val configKey: String = "high-income-child-benefit"
  val ordinal: Int = HIGH_INCOME_CHILD_BENEFIT
  val errorMessage: String = highIncomeChildBenefitMessage
}

case object BlindPersonsAllowance extends ControlListParameter {
  val configKey: String = "blind-persons-allowance"
  val ordinal: Int = BLIND_PERSONS_ALLOWANCE
  val errorMessage: String = blindPersonsAllowanceMessage
}

case object FosterCarers extends ControlListParameter {
  val configKey: String = "foster-carers"
  val ordinal: Int = FOSTER_CARERS
  val errorMessage: String = fosterCarersMessage
}

case object MarriedCouplesAllowance extends ControlListParameter {
  val configKey: String = "married-couples-allowance"
  val ordinal: Int = MARRIED_COUPLES_ALLOWANCE
  val errorMessage: String = marriedCouplesAllowanceMessage
}

case object Capacitor extends ControlListParameter {
  val configKey: String = "capacitor"
  val ordinal: Int = CAPACITOR
  val errorMessage: String = capacitorMessage
}

case object DisguisedRenumerationInvolvement extends ControlListParameter {
  val configKey: String = "disguised-renumeration-involvement"
  val ordinal: Int = DISGUISED_RENUMERATION_INVOLVEMENT
  val errorMessage: String = disguisedRenumerationInvlovementMessage
}

object ControlListParameter {
  val getParameterMap: Map[Int, ControlListParameter] =
    Map(
      NON_RESIDENT_COMPANY_LANDLORD -> NonResidentCompanyLandlord,
      STUDENT_LOANS -> StudentLoans,
      MARRIAGE_ALLOWANCE -> MarriageAllowance,
      EMPLOYMENT_INCOME -> EmploymentIncome,
      COMPLIANCE -> Compliance,
      EXISTING_IDMS_DEBT -> ExistingIdmsDebt,
      BUDGET_PAYMENT_PLAN -> BudgetPaymentPlan,
      DECEASED -> Deceased,
      TIME_TO_PAY -> TimeToPay,
      SELF_SERVE_TIME_TO_PAY -> SelfServeTimeToPay,
      NO_REPAYMENT -> NoRepayment,
      OUTSTANDING_RETURNS -> OutstandingReturns,
      RLS_CASE -> RlsCase,
      ENFORCEMENT -> Enforcement,
      COLLECTION_PRIORITY -> CollectionPriority,
      FAILURE_TO_NOTIFY -> FailureToNotify,
      BANKRUPT_INSOLVENT -> BankruptInsolvent,
      BANKRUPT_VOLUNTARY_ARRANGEMENT -> BankruptVoluntaryArrangement,
      MULTIPLE_SELF_EMPLOYED -> MultipleSelfEmployed,
      NON_RESIDENTS -> NonResidents,
      MINISTERS_OF_RELIGION -> MinistersOfReligion,
      LLOYDS_UNDERWRITER -> LloydsUnderwriter,
      CLASS_TWO_NATIONAL_INSURANCE_CONTRIBUTIONS_VOLUNTARY -> ClassTwoNationalInsuranceContributionsVoluntary,
      BANKS_AND_BUILDING_SOCIETY_INTEREST_FOREIGN -> BanksAndBuildingSocietyInterestForeign,
      DIVIDENDS_FOREIGN -> DividendsForeign,
      PENSION_CONTRIBUTIONS -> PensionContributions,
      PENSION_INCOME -> PensionIncome,
      CONSTRUCTION_INDUSTRY_SCHEME_DEDUCTIONS -> ConstructionInductionSchemeDeductions,
      SA101_ADDITIONAL_INFORMATION -> Sa101AdditionalInformation,
      AVERAGING_ADJUSTMENT -> AveragingAdjustment,
      CAPITAL_GAINS_TAX -> CapitalGainsTax,
      FOREIGN_INCOME -> ForeignIncome,
      TRUST_INCOME -> TrustIncome,
      PARTNERSHIP_INCOME -> PartnershipIncome,
      HIGH_INCOME_CHILD_BENEFIT -> HighIncomeChildBenefit,
      BLIND_PERSONS_ALLOWANCE -> BlindPersonsAllowance,
      FOSTER_CARERS -> FosterCarers,
      MARRIED_COUPLES_ALLOWANCE -> MarriedCouplesAllowance,
      CAPACITOR -> Capacitor,
      DISGUISED_RENUMERATION_INVOLVEMENT -> DisguisedRenumerationInvolvement
    )

  implicit class ControlListHelper(controlListParameters: Set[ControlListParameter]) {
    private val CONTROL_LIST_FALSE = '0'
    private val CONTROL_LIST_TRUE = '1'

    val asBinaryString: String = getParameterMap.values.map {
      case parameter if controlListParameters.contains(parameter) => CONTROL_LIST_TRUE
      case _ => CONTROL_LIST_FALSE
    }.mkString
  }

  val allControlListParameters: Set[ControlListParameter] = ControlListParameter.getParameterMap.values.toSet

}