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

package uk.gov.hmrc.incometaxsubscriptioneligibility.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class UkProperty(
                       ukPropertyStartDate: Option[Date] = None,
                       ukPropertyAccountingMethod: Option[AccountingMethod] = None
                     )

object UkProperty {
  implicit val reads: Reads[UkProperty] = (
    (JsPath \ "ukPropertyStartDate").readNullable[String].map(Date.readOptionalString) and
      (JsPath \ "ukPropertyAccountingMethod").readNullable[AccountingMethod]
    )(UkProperty.apply _)

  implicit val writes: Writes[UkProperty] = Json.writes[UkProperty]

  implicit val format: Format[UkProperty] = Format[UkProperty](reads, writes)
}
