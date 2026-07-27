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

package uk.gov.hmrc.incometaxsubscriptioneligibility.connectors

import com.typesafe.config.Config
import org.apache.pekko.actor.ActorSystem
import play.api.http.Status.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, StringContextOps}
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.AppConfig
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.EligibilityStatusHttpParser.*
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.{EligibilityStatusFailure, EligibilityStatusSuccessResponse}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EligibilityStatusConnector @Inject()(http: HttpClientV2, appConfig: AppConfig, val configuration: Config,
                                           val actorSystem: ActorSystem)
                                          (implicit ec: ExecutionContext) extends ConnectorRetries {

  def getEligibilityStatus(nino: String, utr: String)(implicit hc: HeaderCarrier): Future[EligibilityStatusResponse] = {

    retryFor[EligibilityStatusResponse](EligibilityStatusReads.apiNumber, EligibilityStatusReads.apiName, logError) {
      case Left(EligibilityStatusFailure.UnexpectedStatus(SERVICE_UNAVAILABLE)) => true
      case Left(EligibilityStatusFailure.UnexpectedStatus(BAD_GATEWAY)) => true
      case Left(EligibilityStatusFailure.UnexpectedStatus(INTERNAL_SERVER_ERROR)) => true
    } {
      http
        .get(url"${appConfig.hipBaseUrl}/personal-tax/income-tax-self-assessment/signUpEligibility?nino=$nino&utr=$utr")
        .setHeader(HeaderNames.authorisation -> appConfig.hipAuthorizationToken)
        .setHeader("CorrelationId" -> UUID.randomUUID().toString)
        .execute[EligibilityStatusResponse]
    }
  }
  
  private def logError(result: EligibilityStatusResponse) = {
    result match {
      case Left(EligibilityStatusFailure.UnexpectedStatus(status)) =>
        error(status, "Unexpected Status")
      case Left(EligibilityStatusFailure.InvalidJson) =>
        error(OK, "Invalid Json")
      case _ => {}
    }
  }
  
  private def error(status: Int,
                    message: String) =
    logger.error(Seq(
      s"API #${EligibilityStatusReads.apiNumber}: ${EligibilityStatusReads.apiName}",
      s"Status: $status",
      s"Message: $message"
    ).mkString(", "))
}
