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
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.EligibilityStatusHttpParser.*
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits.EligibilityAuditModel
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatus.*
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason.*
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
      eligibilityStatusConnector.getEligibilityStatus(nino, utr) flatMap {
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
    case NoDataFound => NoDataFound.key
    case NonResidents => NonResidents.key
    case NonResidentCompanyLandlord => NonResidentCompanyLandlord.key
    case MinisterOfReligion => MinisterOfReligion.key
    case LloydsUnderwriter => LloydsUnderwriter.key
    case AveragingAdjustment => AveragingAdjustment.key
    case TrustIncome => TrustIncome.key
    case PartnershipIncome => PartnershipIncome.key
    case BlindPersonsAllowance => BlindPersonsAllowance.key
    case FosterCarers => FosterCarers.key
    case MarriedCouplesAllowance => MarriedCouplesAllowance.key
    case ComplianceActivity => ComplianceActivity.key
    case DebtManagement => DebtManagement.key
    case BudgetPaymentPlan => BudgetPaymentPlan.key
    case Death => Death.key
    case Capacitor => Capacitor.key
    case TimeToPay => TimeToPay.key
    case TimeToPaySelfServe => TimeToPaySelfServe.key
    case DisguisedRemunerationInvolvement => DisguisedRemunerationInvolvement.key
    case NoRepaymentSignal => NoRepaymentSignal.key
    case OutstandingReturns => OutstandingReturns.key
    case EnforcementSignal => EnforcementSignal.key
    case CollectionPrioritySignal => CollectionPrioritySignal.key
    case BankruptInsolvent =>BankruptInsolvent.key
    case BankruptVoluntaryArrangement => BankruptVoluntaryArrangement.key
    case DigitallyExempt => DigitallyExempt.key
    case MTDExemptEnduring => MTDExemptEnduring.key
    case MTDExempt26To27 => MTDExempt26To27.key
    case MTDExempt27To28 => MTDExempt27To28.key
    case MTDExempt28To29 => MTDExempt28To29.key
    case MandationInhibit26To27 => MandationInhibit26To27.key
    case MandationInhibit27To28 => MandationInhibit27To28.key
    case Other(key) => key
  }

}
