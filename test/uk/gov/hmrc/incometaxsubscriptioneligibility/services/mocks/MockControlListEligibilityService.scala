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

package uk.gov.hmrc.incometaxsubscriptioneligibility.services.mocks

import org.scalamock.scalatest.MockFactory
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.EligibilityStatus
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.ControlListEligibilityService

import scala.concurrent.{ExecutionContext, Future}

trait MockControlListEligibilityService extends MockFactory {

  val mockControlListEligibilityService: ControlListEligibilityService = mock[ControlListEligibilityService]

  def mockIsEligible(sautr: String, agentReferenceNumber: Option[String])
                    (isEligible: Future[EligibilityStatus])
                    (implicit ec: ExecutionContext, request: Request[_]): Unit = {
    (mockControlListEligibilityService.getEligibilityStatus(_: String, _: Option[String])(_: HeaderCarrier, _: ExecutionContext, _: Request[_]))
      .expects(sautr, agentReferenceNumber, *, ec, request)
      .returning(isEligible)
  }
  
}
