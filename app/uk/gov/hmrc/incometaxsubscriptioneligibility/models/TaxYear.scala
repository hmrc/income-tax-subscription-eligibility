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

import java.time.LocalDate.now
import java.time.{LocalDate, Month}

case object TaxYear {
  private val START_DAY = 6
  private val START_MONTH = Month.APRIL

  def getCurrentTaxYear(): String = {
    val endYear = taxYearEnd()
    s"${endYear - 1}-$endYear"
  }

  def getNextTaxYear(): String = {
    val endYear = taxYearEnd()
    s"${endYear}-${endYear + 1}"
  }

  private def taxYearEnd() = {
    val date = now()
    if (date.isBefore(LocalDate.of(date.getYear, TaxYear.START_MONTH, TaxYear.START_DAY))) date.getYear
    else date.getYear + 1
  }
}

