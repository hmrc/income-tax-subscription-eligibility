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

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

case class Date(day: String, month: String, year: String)

object Date {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")

  def readOptionalString(maybeDateString: Option[String]): Option[Date] = maybeDateString.flatMap(dateString => {
    Try {
      val localDate = LocalDate.parse(dateString, formatter)
      Date(localDate.getDayOfMonth.toString, localDate.getMonthValue.toString, localDate.getYear.toString)
    }.toOption
  })

  implicit val format: OFormat[Date] = Json.format[Date]
}