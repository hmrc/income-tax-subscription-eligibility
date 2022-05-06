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

import play.api.Logging
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.TaxYear

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.util.Try

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig) extends FeatureSwitching with Logging {

  private def loadConfig(key: String) = servicesConfig.getString(key) //throws RuntimeException(s"Could not find config key '$key'") if key not found

  def desUrl: String =
    loadConfig(
      if (isEnabled(UseStubForDesConnection))
        "microservice.services.des.stub-url"
      else
        "microservice.services.des.url"
    )

  lazy val desAuthorisationToken: String = s"Bearer ${loadConfig("microservice.services.des.authorisation-token")}"

  lazy val desEnvironmentHeader: (String, String) = "Environment" -> loadConfig("microservice.services.des.environment")

  def loadConfigFromEnv(key: String): Option[String] = {
    sys.props.get(key) match {
      case r@Some(result) if result.nonEmpty => r
      case _ => Some(servicesConfig.getString(key))
    }
  }

  def isEligible(year: String, param: ControlListParameter): Boolean =
    loadConfigFromEnv(s"control-list.${year}.${param.configKey}") match {
      case Some(bool) => bool.toBoolean
      case _ => throw new Exception(s"Unknown eligibility config key: ${param.configKey}")
    }

  def isControlListConfigurationValid(): Boolean = {
    val currentYear = TaxYear.getCurrentTaxYear()
    val nextYear = TaxYear.getNextTaxYear()

    ControlListParameter.getParameterMap.values.foldLeft(true)((accumulator, param) => {
      // False in the config means "do the check", true means "don't do the check"
      val doCheck = false
      val dontCheck = true

      // We choose to make "missing" mean "don't check"
      // If a missing control list item is later requested, we will throw an request specific error (see above).

      val checkCurrentYear = Try(isEligible(currentYear, param)).toOption.getOrElse(dontCheck) == doCheck
      val checkNextYear = Try(isEligible(nextYear, param)).toOption.getOrElse(dontCheck) == doCheck
      if (!checkCurrentYear && checkNextYear) {
        logger.info(s"Control list param $param is not checked in the current year and checked in the following year")
        false
      } else
        accumulator
    })
  }

  if (!isControlListConfigurationValid())
    logger.error(s"Control list params are not valid")
}
