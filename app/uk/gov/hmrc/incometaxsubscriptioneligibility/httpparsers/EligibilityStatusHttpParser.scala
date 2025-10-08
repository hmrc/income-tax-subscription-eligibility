/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers

import play.api.Logging
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.{EligibilityStatusFailure, EligibilityStatusSuccessResponse}

object EligibilityStatusHttpParser extends Logging {

  type EligibilityStatusResponse = Either[EligibilityStatusFailure, EligibilityStatusSuccessResponse]

  implicit object EligibilityStatusReads extends HttpReads[EligibilityStatusResponse] {
    override def read(method: String, url: String, response: HttpResponse): EligibilityStatusResponse = {
      response.status match {
        case OK =>
          response.json.validate[EligibilityStatusSuccessResponse] match {
            case JsSuccess(value, _) =>
              Right(value)
            case JsError(errors) =>
              logger.error(s"[EligibilityStatusHttpParser] - Unable to parse json. Errors: $errors")
              Left(EligibilityStatusFailure.InvalidJson)
          }
        case status =>
          logger.error(s"[EligibilityStatusHttpParser] - Unexpected status returned from API. Status: $status")
          Left(EligibilityStatusFailure.UnexpectedStatus)
      }
    }
  }

}
