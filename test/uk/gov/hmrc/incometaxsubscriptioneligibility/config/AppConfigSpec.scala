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

package uk.gov.hmrc.incometaxsubscriptioneligibility.config

import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.TaxYear
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig


class AppConfigSpec extends PlaySpec with MockitoSugar {

  val mockServicesConfig: ServicesConfig = mock[ServicesConfig]
  val mockConfiguration: Configuration = mock[Configuration]
  val appConfig = new AppConfig(mockServicesConfig, mockConfiguration)

  val currentYear = TaxYear.getCurrentTaxYear()
  val nextYear = TaxYear.getNextTaxYear()
  val studentLoansKeyCurrentYear = s"control-list.$currentYear.student-loans"
  val studentLoansKeyNextYear = s"control-list.$nextYear.student-loans"

  "config check" should {

    val invalid = false
    val valid = true

    val checked = Some(false)
    val notChecked = Some(true)
    val missing = None

    def setup(key: String, returnVal: Option[Boolean]): Unit = {
      returnVal match {
        case Some(value) =>
          when(mockServicesConfig.getString(ArgumentMatchers.eq(key))).thenReturn(value.toString)
        case None =>
          when(mockServicesConfig.getString(ArgumentMatchers.eq(key))).thenThrow(new RuntimeException("whoops"))
      }
    }

    def doTest(curYearVal: Option[Boolean], nextYearVal: Option[Boolean], result: Boolean):Unit = {
      setup(studentLoansKeyCurrentYear, curYearVal)
      setup(studentLoansKeyNextYear, nextYearVal)
      appConfig.isControlListConfigurationValid() mustBe (result)
    }

    // Truth table. NB False means do check, true & missing means don't
    //  Cur \ Next | checked | notChecked  | missing
    //     checked | valid   | valid       | valid
    //  notChecked | invalid | valid       | valid
    //     missing | invalid | valid       | valid

    "return valid when checked both years" in {
      doTest(checked, checked, valid)
    }

    "return valid when checked current year but not next year" in {
      doTest(checked, notChecked, valid)
    }

    "return valid when checked current year but missing next year" in {
      doTest(checked, missing, valid)
    }


    "return invalid when checked next year but not current year" in {
      doTest(notChecked, checked, invalid)
    }

    "return valid when not checked either year" in {
      doTest(notChecked, notChecked, valid)
    }

    "return valid when not checked current year and missing next year" in {
      doTest(notChecked, missing, valid)
    }


    "return invalid when checked next year but not present current year" in {
      doTest(missing, checked, invalid)
    }

    "return valid when not checked next year but not present current year" in {
      doTest(missing, notChecked, valid)
    }

    "return valid when not present both years" in {
      doTest(missing, missing, valid)
    }

  }
}
