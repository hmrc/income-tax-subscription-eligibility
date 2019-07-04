/*
 * Copyright 2019 HM Revenue & Customs
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

  val prefix = "feature-switch"
  val switches: Set[FeatureSwitch] = Set(StubControlListEligible)

  def apply(switchName: String): FeatureSwitch = switches find (_.name equals switchName) match {
    case Some(switch) => switch
    case _ => throw new NoSuchElementException(s"Invalid feature switch $switchName")
  }

  def apply(setting: FeatureSwitchSetting): FeatureSwitch = switches find (_.displayName equals setting.name) match {
    case Some(switch) => switch
    case _ => throw new NoSuchElementException(s"Invalid feature switch ${setting.name}")
  }

}

object StubControlListEligible extends FeatureSwitch {
  override val name = s"${FeatureSwitch.prefix}-control-list-eligible"
  override val displayName: String = "Stub eligibility response to always return Eligible"
}
