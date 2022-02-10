/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks

import org.scalamock.handlers.CallHandler1
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.TaxYear
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.ConvertConfigValuesService

trait MockConvertConfigValuesService extends MockFactory {

  val mockConvertConfigValuesService: ConvertConfigValuesService = mock[ConvertConfigValuesService]

  def mockConvertConfigValues(controlListParametersCurrentYear: Set[ControlListParameter],
                              controlListParametersNextYear: Set[ControlListParameter]): Unit = {
    (mockConvertConfigValuesService.convertConfigValues _)
      .expects(TaxYear.getCurrentTaxYear())
      .returning(controlListParametersCurrentYear)
    (mockConvertConfigValuesService.convertConfigValues _)
      .expects(TaxYear.getNextTaxYear())
      .returning(controlListParametersNextYear)
  }

}
