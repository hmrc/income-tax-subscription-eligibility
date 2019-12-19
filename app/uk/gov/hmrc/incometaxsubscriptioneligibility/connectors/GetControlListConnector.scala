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

package uk.gov.hmrc.incometaxsubscriptioneligibility.connectors

import javax.inject.Inject
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.AppConfig
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.GetControlListResponse
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class GetControlListConnector @Inject()(http: HttpClient, appConfig: AppConfig) {

  def getControlList(sautr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[GetControlListResponse] = {
    val headerCarrier = hc
      .withExtraHeaders(appConfig.desEnvironmentHeader)
      .copy(authorization = Some(Authorization(appConfig.desAuthorisationToken)))

    http.GET(url = s"${appConfig.desUrl}/income-tax/controlList/$sautr")(
      implicitly[HttpReads[GetControlListResponse]],
      headerCarrier,
      implicitly[ExecutionContext]
    )
  }
}
