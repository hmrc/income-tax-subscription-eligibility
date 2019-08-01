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

package uk.gov.hmrc.incometaxsubscriptioneligibility.services

import javax.inject.{Inject, Singleton}

import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{FeatureSwitching, StubControlListEligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.GetControlListConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.ControlListDataNotFound
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits.{EligibilityAuditModel, SuccessfulEligibilityAuditModel}
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ControlListEligibilityService @Inject()(convertConfigValuesService: ConvertConfigValuesService,
                                              getControlListConnector: GetControlListConnector,
                                              auditService: AuditService
                                             ) extends FeatureSwitching {

  def getEligibilityStatus(sautr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_]): Future[Boolean] = {

    def auditEligibilityFailureReason(reasons: Seq[String]) : Future[AuditResult] = {
      val audit: EligibilityAuditModel = EligibilityAuditModel(sautr, reasons)
      auditService.audit(audit)
    }

    def auditEligibilitySuccessful(sautr: String):Future[AuditResult] = {
      val audit: SuccessfulEligibilityAuditModel= SuccessfulEligibilityAuditModel(sautr)
      auditService.audit(audit)
    }

    if (isEnabled(StubControlListEligible)) {
      Future.successful(true)
    } else {
      val eligibilityConfigParameters = convertConfigValuesService.convertConfigValues()
      getControlListConnector.getControlList(sautr) flatMap {
        case Right(controlListParameters) =>
          eligibilityConfigParameters.intersect(controlListParameters).toSeq match {
            case Nil => auditEligibilitySuccessful(sautr).map(_ => true)
            case reasons => auditEligibilityFailureReason(reasons.map(_.errorMessage)).map(_ => false)
          }
        case Left(ControlListDataNotFound) => auditEligibilityFailureReason(Seq("Control list data not found")).map(_ => false)
      }
    }
  }

}
