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

package uk.gov.hmrc.incometaxsubscriptioneligibility.services

import uk.gov.hmrc.incometaxsubscriptioneligibility.config.AppConfig
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter

import javax.inject.{Inject, Singleton}


@Singleton
class ConvertConfigValuesService @Inject()(appConfig: AppConfig) {

  def convertConfigValues(year: String): Set[ControlListParameter] =
    ControlListParameter.getParameterMap.values.filterNot(appConfig.isEligible(year, _)).toSet

}
