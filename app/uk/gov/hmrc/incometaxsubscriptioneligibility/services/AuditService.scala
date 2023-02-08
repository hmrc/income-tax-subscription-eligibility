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

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.DataEvent

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuditService @Inject()(configuration: Configuration, auditConnector: AuditConnector) {

  private lazy val appName: String = configuration.get[String]("appName")

  def audit(dataSource: AuditModel)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_]): Future[AuditResult] = {

    def toDataEvent(auditModel: AuditModel)(implicit hc: HeaderCarrier): DataEvent = {
      DataEvent(
        auditSource = appName,
        auditType = auditModel.auditType,
        tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(auditModel.transactionName, request.path),
        detail = AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(auditModel.detail.toSeq: _*)
      )
    }

    auditConnector.sendEvent(toDataEvent(dataSource))
  }
}

trait AuditModel {
  val auditType: String
  val transactionName: String
  val detail: Map[String, String]
}
