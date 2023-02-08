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

case class PrepopData(
    selfEmployments: Option[Seq[SelfEmploymentData]] = None,
    ukProperty: Option[UkProperty] = None,
    overseasProperty: Option[OverseasProperty] = None
)

object PrepopData {
  def filterCeasedBusinesses(maybePrepopData: Option[PrepopData]): Option[PrepopData] = {
    maybePrepopData.map { prepopData =>
      prepopData.copy(
        selfEmployments = prepopData.selfEmployments.map(_.filter(_.businessCeasedDate.isEmpty))
      )
    }
  }

  implicit val reads: Reads[PrepopData] = (
    (JsPath \ "selfEmployments").readNullable[Seq[SelfEmploymentData]] and
      (JsPath).readNullable[UkProperty].map {
        case Some(UkProperty(None, None)) => None
        case ukProperty => ukProperty
      } and
      (JsPath).readNullable[OverseasProperty].map {
        case Some(OverseasProperty(None, None)) => None
        case overseasProperty => overseasProperty
      }
    )(PrepopData.apply _)

  implicit val writes: Writes[PrepopData] = Json.writes[PrepopData]
  implicit val format: Format[PrepopData] = Format[PrepopData](reads, writes)
}
