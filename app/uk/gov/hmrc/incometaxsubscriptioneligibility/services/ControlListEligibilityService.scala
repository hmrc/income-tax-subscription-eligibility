/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{FeatureSwitching, StubControlListEligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.GetControlListConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.GetControlListResponse
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.TaxYear
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits.EligibilityAuditModel

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ControlListEligibilityService @Inject()(convertConfigValuesService: ConvertConfigValuesService,
                                              getControlListConnector: GetControlListConnector,
                                              auditService: AuditService
                                             ) extends FeatureSwitching {

  private def eligibilityConfigParameters(year: String) = convertConfigValuesService.convertConfigValues(year)

  def getEligibilityStatus(sautr: String, userType: String, agentReferenceNumber: Option[String])
                          (implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_]): Future[EligibilityByYear] =
    if (isEnabled(StubControlListEligible)) {
      Future.successful(EligibilityByYear(current = true, next = true))
    } else {
      getEligibilityStatusForReal(sautr, userType, agentReferenceNumber)
    }

  private def getEligibilityStatusForReal(sautr: String, userType: String, agentReferenceNumber: Option[String])
                                         (implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_]): Future[EligibilityByYear] = for {
    controlResponse <- getControlListConnector.getControlList(sautr)
    errorReasonsCurrent = checkReasons(TaxYear.getCurrentTaxYear(), controlResponse)
    errorReasonsNext = checkReasons(TaxYear.getNextTaxYear(), controlResponse)
    // Alex Rimmer:
    // We will keep audit model the same for now until we have talked to the TxM / CIP team
    // about the audit requirements now it’s changing
    // Success value should just relate to the current year check
    auditModel = EligibilityAuditModel(eligibilityResult = errorReasonsCurrent.isEmpty, sautr, userType, agentReferenceNumber, errorReasonsCurrent.toSeq)
    _ <- auditService.audit(auditModel)
  } yield EligibilityByYear(errorReasonsCurrent.isEmpty, errorReasonsNext.isEmpty)

  private def checkReasons(year: String, controlResponse: GetControlListResponse)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext) = (controlResponse match {
    case Right(controlListParameters) => eligibilityConfigParameters(year).intersect(controlListParameters) // empty == OK
    case Left(error) => Set(error)
  }).map(_.errorMessage)

}
case class EligibilityByYear(current: Boolean, next: Boolean)

