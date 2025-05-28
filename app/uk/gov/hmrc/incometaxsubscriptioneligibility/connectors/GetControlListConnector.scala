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

import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, StringContextOps}
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{AppConfig, FeatureSwitching, UseStubForDesConnection}
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.GetControlListResponse

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetControlListConnector @Inject()(http: HttpClientV2, val appConfig: AppConfig) extends FeatureSwitching {

  def getControlList(sautr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[GetControlListResponse] = {

    val desHeaders: Seq[(String, String)] = Seq(
      HeaderNames.authorisation -> appConfig.desAuthorisationToken,
      appConfig.desEnvironmentHeader
    )

    http
      .get(url"${appConfig.desUrl(isEnabled(UseStubForDesConnection))}/income-tax/controlList/$sautr")
      .setHeader(desHeaders: _*)
      .execute[GetControlListResponse]

  }
}
