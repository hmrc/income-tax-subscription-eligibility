/*
 * Copyright 2018 HM Revenue & Customs
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

import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.{ComponentSpecBase, ControlListConfigTestHelper}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.{ControlListParameter, StudentLoans}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter.allControlListParameters

class ConvertConfigValuesServicesISpec extends ComponentSpecBase with ControlListConfigTestHelper {

  "convertConfigValues" should {
    "return an empty set" when {
      "all config values are true" in new Server(app(extraConfig = toConfigList(testAllTrue))) {
        val convertConfigValuesService: ConvertConfigValuesService = app.injector.instanceOf[ConvertConfigValuesService]

        val result: Set[ControlListParameter] = convertConfigValuesService.convertConfigValues()

        result mustBe Set()
      }
    }

    "return a set with a value" when {
      "Student Loans is false" in new Server(app(extraConfig = toConfigList(testAllTrue.updated(StudentLoans, false)))) {
        val convertConfigValuesService: ConvertConfigValuesService = app.injector.instanceOf[ConvertConfigValuesService]

        val result: Set[ControlListParameter] = convertConfigValuesService.convertConfigValues()

        result mustBe Set(StudentLoans)
      }
    }

    "return a set of values with all the control list parameters" when {
      "all config values are false" in new Server(app(toConfigList(testAllFalse))) {
        val convertConfigValuesService: ConvertConfigValuesService = app.injector.instanceOf[ConvertConfigValuesService]

        val result: Set[ControlListParameter] = convertConfigValuesService.convertConfigValues()

        result mustBe allControlListParameters
      }
    }

    "returns an empty set and ignores the incorrect parameter" when {
      "a control list parameter with all values as true and one an incorrect parameter" in
        new Server(app(extraConfig = toConfigList(testAllTrue).updated("control-list.some-incorrect-param.eligible", "false"))) {

        val convertConfigValuesService: ConvertConfigValuesService = app.injector.instanceOf[ConvertConfigValuesService]

        val result: Set[ControlListParameter] = convertConfigValuesService.convertConfigValues()

        result mustBe Set()
      }
    }

    "throws an exception for incorrect value" when {
      "a control list parameter are true but one has an incorrect value" in
        new Server(app(extraConfig = toConfigList(testAllTrue).updated(s"control-list.${StudentLoans.configKey}.eligible", "ineligible"))) {

        val convertConfigValuesService: ConvertConfigValuesService = app.injector.instanceOf[ConvertConfigValuesService]

        intercept[IllegalArgumentException] {
          convertConfigValuesService.convertConfigValues()
        }
      }
    }
  }

}
