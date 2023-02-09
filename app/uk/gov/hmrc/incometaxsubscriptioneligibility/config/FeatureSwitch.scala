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

package uk.gov.hmrc.incometaxsubscriptioneligibility.config

sealed trait FeatureSwitch {
  val name: String
  val displayName: String
}

object FeatureSwitch {

  val prefix: String = "feature-switch"
  val switches: Set[FeatureSwitch] =
    Set(
      StubControlListEligible,
      UseStubForDesConnection
    )

  def apply(switchName: String): FeatureSwitch = switches find (_.name == switchName) match {
    case Some(switch) => switch
    case _ => throw new NoSuchElementException(s"Invalid feature switch $switchName")
  }

  def apply(setting: FeatureSwitchSetting): FeatureSwitch = switches find (_.displayName == setting.feature) match {
    case Some(switch) => switch
    case _ => throw new NoSuchElementException(s"Invalid feature switch ${setting.feature}")
  }

}

case object StubControlListEligible extends FeatureSwitch {
  override val name: String = s"${FeatureSwitch.prefix}.control-list-eligible"
  override val displayName: String = "Stub eligibility response to always return Eligible"
}

case object UseStubForDesConnection extends FeatureSwitch {
  override val name: String = s"${FeatureSwitch.prefix}.use-stub-for-des-connection"
  override val displayName: String = "Use stub for DES connection"
}
