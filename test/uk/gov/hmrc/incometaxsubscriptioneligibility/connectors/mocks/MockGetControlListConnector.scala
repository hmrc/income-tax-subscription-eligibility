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

import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.connectors.GetControlListConnector
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.GetControlListResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockGetControlListConnector extends MockFactory {
  suite: TestSuite =>

  val mockGetControlListConnector: GetControlListConnector = mock[GetControlListConnector]

  def mockGetControlList(sautr: String)
                        (hc: HeaderCarrier, ec: ExecutionContext)
                        (response: Future[GetControlListResponse]): CallHandler3[String, HeaderCarrier, ExecutionContext, Future[GetControlListResponse]] = {
    (mockGetControlListConnector.getControlList(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(sautr, hc, ec)
      .returning(response)
  }

}
