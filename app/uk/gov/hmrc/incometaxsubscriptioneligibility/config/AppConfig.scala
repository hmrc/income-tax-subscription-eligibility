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

package uk.gov.hmrc.incometaxsubscriptioneligibility.config

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig) extends FeatureSwitching {

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

  def isEligible(param: ControlListParameter): Boolean =
    loadConfigFromEnv(s"control-list.${param.configKey}.eligible") match {
      case Some(bool) => bool.toBoolean
      case _ => throw new Exception(s"Unknown eligibility config key: ${param.configKey}")
    }
}
