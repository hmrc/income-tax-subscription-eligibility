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
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.GetControlListConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.{GetControlListResponse, GetControlListSuccessResponse}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits.EligibilityAuditModel
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.{EligibilityByYear, EligibilityStatus, PrepopData, TaxYear}
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ControlListEligibilityService @Inject()(convertConfigValuesService: ConvertConfigValuesService,
                                              getControlListConnector: GetControlListConnector,
                                              auditService: AuditService,
                                              val appConfig: AppConfig
                                             ) extends FeatureSwitching with Logging {
  def getEligibilityStatus(sautr: String, agentReferenceNumber: Option[String])
                          (implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_]): Future[EligibilityStatus] = for {
    controlResponse <- getControlListConnector.getControlList(sautr)
    (eligibilityStatus, prepopData) = handleResponse(controlResponse)
    _ <- auditControlListResults(eligibilityStatus, sautr, agentReferenceNumber)
    eligibleCurrentYear = isEligible(eligibilityStatus.current)
    eligibleNextYear = isEligible(eligibilityStatus.next)
  } yield {
    EligibilityStatus(eligible = eligibleCurrentYear, eligibleCurrentYear, eligibleNextYear, prepopData)
  }

  private def auditControlListResults(eligibilityStatus: EligibilityByYear, sautr: String, agentReferenceNumber: Option[String])
                                     (implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_]): Future[(AuditResult, AuditResult)] = {
    val currentYearAuditModel: EligibilityAuditModel = EligibilityAuditModel(
      utr = sautr,
      agentReferenceNumber = agentReferenceNumber,
      controlListCheck = "currentYear",
      reasons = eligibilityStatus.current
    )
    val nextYearAuditModel: EligibilityAuditModel = EligibilityAuditModel(
      utr = sautr,
      agentReferenceNumber = agentReferenceNumber,
      controlListCheck = "nextYear",
      reasons = eligibilityStatus.next
    )
    for {
      currentYearAuditResult <- sendAuditEvent(currentYearAuditModel)
      nextYearAuditResult <- sendAuditEvent(nextYearAuditModel)
    } yield (currentYearAuditResult, nextYearAuditResult)
  }

  private def handleResponse(controlResponse: GetControlListResponse): (EligibilityByYear, Option[PrepopData]) = controlResponse match {
    case Right(GetControlListSuccessResponse(controlList, prepopData)) => (EligibilityByYear(
      checkReasons(TaxYear.getCurrentTaxYear(), controlList),
      checkReasons(TaxYear.getNextTaxYear(), controlList)
    ), PrepopData.filterCeasedBusinesses(prepopData))
    case Left(error) => (
      EligibilityByYear(Set(error.errorMessage), Set(error.errorMessage)), None
    )
  }

  private def checkReasons(year: String, controlList: Set[ControlListParameter]): Set[String] =
    eligibilityConfigParameters(year).intersect(controlList).map(_.errorMessage)

  private def eligibilityConfigParameters(year: String) = convertConfigValuesService.convertConfigValues(year)

  private def isEligible(controlListResults: Set[String]) =
    isEnabled(StubControlListEligible) || controlListResults.isEmpty

  private def sendAuditEvent(auditModel: EligibilityAuditModel)
                            (implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_]) =
    if (isEnabled(StubControlListEligible)) {
      Future.successful(AuditResult.Disabled)
    } else {
      auditService.audit(auditModel)
    }
}
