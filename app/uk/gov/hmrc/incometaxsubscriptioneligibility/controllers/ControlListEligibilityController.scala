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

package uk.gov.hmrc.incometaxsubscriptioneligibility.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.ControlListEligibilityService
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton()
class ControlListEligibilityController @Inject()(cc: ControllerComponents,
                                                 controlListEligibilityService: ControlListEligibilityService
                                                )(implicit ec: ExecutionContext) extends BackendController(cc) {

  def getEligibilityStatus(sautr: String): Action[AnyContent] = Action {
    implicit request =>

      val key: String = "eligible"
      val eligible: Boolean = controlListEligibilityService.getEligibilityStatus(sautr)

      Ok(Json.obj(key -> eligible))
  }

}