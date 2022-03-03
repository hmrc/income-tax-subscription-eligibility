/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class SelfEmploymentData(
                               businessName: Option[String] = None,
                               businessTradeName: Option[String] = None,
                               businessAddressFirstLine: Option[String] = None,
                               businessAddressPostCode: Option[String] = None,
                               businessStartDate: Option[Date] = None,
                               businessAccountingMethod: Option[AccountingMethod] = None,
                               businessCeasedDate: Option[String] = None
                             )

object SelfEmploymentData {
  implicit val reads: Reads[SelfEmploymentData] = (
    (JsPath \ "businessName").readNullable[String] and
      (JsPath \ "businessTradeName").readNullable[String] and
      (JsPath \ "businessAddressFirstLine").readNullable[String] and
      (JsPath \ "businessAddressPostCode").readNullable[String] and
      (JsPath \ "businessStartDate").readNullable[String].map(Date.readOptionalString) and
      (JsPath \ "businessAccountingMethod").readNullable[AccountingMethod] and
      (JsPath \ "businessCeasedDate").readNullable[String]
    )(SelfEmploymentData.apply _)


  implicit val writes: Writes[SelfEmploymentData] = Json.writes[SelfEmploymentData]

  implicit val format: Format[SelfEmploymentData] = Format[SelfEmploymentData](reads, writes)
}
