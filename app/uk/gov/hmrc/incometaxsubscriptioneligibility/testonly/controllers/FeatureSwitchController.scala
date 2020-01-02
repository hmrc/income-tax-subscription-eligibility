/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.testonly.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.FeatureSwitch.switches
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{FeatureSwitch, FeatureSwitchSetting, FeatureSwitching}
import uk.gov.hmrc.play.bootstrap.controller.BackendController

@Singleton
class FeatureSwitchController @Inject()(override val controllerComponents: ControllerComponents)
  extends BackendController(controllerComponents) with FeatureSwitching {

  private def getCurrentSettings: Result = {
    val featureSwitches = switches map (switch => FeatureSwitchSetting(switch.displayName, isEnabled(switch)))
    Ok(Json.toJson(featureSwitches))
  }

  def get: Action[AnyContent] = Action {
    getCurrentSettings
  }

  def update: Action[List[FeatureSwitchSetting]] = Action(parse.json[List[FeatureSwitchSetting]]) { request =>
    request.body foreach { setting =>
      val featureSwitch = FeatureSwitch(setting)

      if (setting.enable) enable(featureSwitch)
      else disable(featureSwitch)
    }
    getCurrentSettings
  }

}
