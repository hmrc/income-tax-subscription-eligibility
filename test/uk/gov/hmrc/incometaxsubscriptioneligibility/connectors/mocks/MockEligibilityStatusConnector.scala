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

package uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.mocks

import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.EligibilityStatusConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.EligibilityStatusHttpParser.EligibilityStatusResponse

import scala.concurrent.Future

trait MockEligibilityStatusConnector extends MockFactory {
  suite: TestSuite =>

  val mockEligibilityStatusConnector: EligibilityStatusConnector = mock[EligibilityStatusConnector]

  def mockGetEligibilityStatus(nino: String)
                              (response: EligibilityStatusResponse): CallHandler2[String, HeaderCarrier, Future[EligibilityStatusResponse]] = {
    (mockEligibilityStatusConnector.getEligibilityStatus(_: String)(_: HeaderCarrier))
      .expects(nino, *)
      .returning(Future.successful(response))
  }

}
