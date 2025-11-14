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

package uk.gov.hmrc.incometaxsubscriptioneligibility.services

import play.api.Logging
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{AppConfig, FeatureSwitching, StubControlListEligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.EligibilityStatusConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.EligibilityStatusHttpParser._
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.ControlListDataNotFound
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits.EligibilityAuditModel
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListMessages
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatus._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.{EligibilityStatusFailureReason, EligibilityStatusSuccessResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EligibilityService @Inject()(auditService: AuditService,
                                   eligibilityStatusConnector: EligibilityStatusConnector)
                                  (val appConfig: AppConfig)
                                  (implicit ec: ExecutionContext) extends FeatureSwitching with Logging {

  def getEligibilityStatus(nino: String, utr: String, arn: Option[String])
                          (implicit request: Request[_], hc: HeaderCarrier): Future[EligibilityStatusResponse] = {
    if (isEnabled(StubControlListEligible)) {
      Future.successful(Right(EligibilityStatusSuccessResponse(Eligible, Eligible, Seq.empty, Seq.empty)))
    } else {
      eligibilityStatusConnector.getEligibilityStatus(nino) flatMap {
        case Right(success) => auditResults(utr, arn)(success).map(_ => Right(success))
        case Left(failure) => Future.successful(Left(failure))
      }
    }
  }

  private def auditResults(utr: String, arn: Option[String])
                          (eligibilityStatusSuccessResponse: EligibilityStatusSuccessResponse)
                          (implicit request: Request[_], hc: HeaderCarrier): Future[(AuditResult, AuditResult)] = {
    val currentYearAuditModel: EligibilityAuditModel = EligibilityAuditModel(
      utr = utr,
      agentReferenceNumber = arn,
      controlListCheck = "currentYear",
      reasons = eligibilityStatusSuccessResponse.currentTaxYearFailureReasons.map(failureReasonToReasonKey).toSet
    )
    val nextYearAuditModel: EligibilityAuditModel = EligibilityAuditModel(
      utr = utr,
      agentReferenceNumber = arn,
      controlListCheck = "nextYear",
      reasons = eligibilityStatusSuccessResponse.nextTaxYearFailureReasons.map(failureReasonToReasonKey).toSet
    )
    for {
      currentYearAuditResult <- auditService.audit(currentYearAuditModel)
      nextYearAuditResult <- auditService.audit(nextYearAuditModel)
    } yield (currentYearAuditResult, nextYearAuditResult)
  }

  private val failureReasonToReasonKey: EligibilityStatusFailureReason => String = {
    case NoDataFound => ControlListDataNotFound.errorMessage
    case NonResidents => ControlListMessages.nonResidentsMessage
    case NonResidentCompanyLandlord => ControlListMessages.nonResidentCompanyLandlordMessage
    case DebtManagement => ControlListMessages.existingIdmsDebtMessage
    case MinisterOfReligion => ControlListMessages.ministersOfReligionMessage
    case LloydsUnderwriter => ControlListMessages.lloydsUnderwriterMessage
    case AveragingAdjustment => ControlListMessages.averagingAdjustmentMessage
    case TrustIncome => ControlListMessages.trustIncomeMessage
    case PartnershipIncome => ControlListMessages.partnershipIncomeMessage
    case BlindPersonsAllowance => ControlListMessages.blindPersonsAllowanceMessage
    case FosterCarers => ControlListMessages.fosterCarersMessage
    case MarriedCouplesAllowance => ControlListMessages.marriedCouplesAllowanceMessage
    case ComplianceActivity => ControlListMessages.complianceMessage
    case BudgetPaymentPlan => ControlListMessages.budgetPaymentPlanMessage
    case Death => ControlListMessages.deceasedMessage
    case TimeToPay => ControlListMessages.timeToPayMessage
    case TimeToPaySelfServe => ControlListMessages.selfServeTimeToPayMessage
    case DisguisedRemunerationInvolvement => ControlListMessages.disguisedRenumerationInvlovementMessage
    case NoRepaymentSignal => ControlListMessages.noRepaymentMessage
    case OutstandingReturns => ControlListMessages.outstandingReturnsMessage
    case EnforcementSignal => ControlListMessages.enforcementMessage
    case CollectionPrioritySignal => ControlListMessages.collectionPriorityMessage
    case BankruptInsolvent => ControlListMessages.bankruptInsolvementMessage
    case BankruptVoluntaryArrangement => ControlListMessages.bankruptVoluntaryArrangementMessage
    case DigitallyExempt => DigitallyExempt.key
    case MTDExemptEnduring => MTDExemptEnduring.key
    case MTDExempt26To27 => MTDExempt26To27.key
    case MTDExempt27To28 => MTDExempt27To28.key
    case MandationInhibit26To27 => MandationInhibit26To27.key
    case MandationInhibit27To28 => MandationInhibit27To28.key
    case Other(key) => key
  }

}
