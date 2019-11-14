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

package uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks

import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.ControlListEligibilityService

import scala.concurrent.{ExecutionContext, Future}

trait MockControlListEligibilityService extends MockFactory {

  val mockControlListEligibilityService: ControlListEligibilityService = mock[ControlListEligibilityService]

  def mockIsEligible(sautr: String)
                    (isEligible: Future[Boolean])
                    (implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_]): CallHandler4[String, HeaderCarrier, ExecutionContext, Request[_], Future[Boolean]] = {
    (mockControlListEligibilityService.getEligibilityStatus(_: String)(_: HeaderCarrier, _: ExecutionContext, _: Request[_]))
      .expects(sautr, *, ec, request)
      .returning(isEligible)
  }

}
