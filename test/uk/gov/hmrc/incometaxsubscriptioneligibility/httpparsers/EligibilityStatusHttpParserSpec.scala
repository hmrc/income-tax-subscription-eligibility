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

package uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers

import org.scalatestplus.play.PlaySpec
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.EligibilityStatusHttpParser.{EligibilityStatusReads, EligibilityStatusResponse}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatus.Eligible
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.{EligibilityStatusFailure, EligibilityStatusSuccessResponse}

class EligibilityStatusHttpParserSpec extends PlaySpec {

  "EligibilityStatusReads.read" must {
    "return an eligibility status success response" when {
      "the response has a status of OK and a valid json body" in {
        val result: EligibilityStatusResponse = read(
          status = OK,
          json = Json.obj(
            "CY" -> "Yes",
            "CY1" -> "Yes",
            "ReasonKeyCY" -> Json.arr(),
            "ReasonKeyCY1" -> Json.arr()
          )
        )

        result mustBe Right(EligibilityStatusSuccessResponse(
          currentTaxYear = Eligible,
          nextTaxYear = Eligible,
          currentTaxYearFailureReasons = Seq.empty,
          nextTaxYearFailureReasons = Seq.empty
        ))
      }
    }
    "return an eligibility status failure response" when {
      "the response has a status of OK and an invalid json body" in {
        val result: EligibilityStatusResponse = read(
          status = OK,
          json = Json.obj()
        )

        result mustBe Left(EligibilityStatusFailure.InvalidJson)
      }
      "the response has an unexpected status" in {
        val result: EligibilityStatusResponse = read(
          status = INTERNAL_SERVER_ERROR,
          json = Json.obj()
        )

        result mustBe Left(EligibilityStatusFailure.UnexpectedStatus)
      }
    }
  }

  def read(status: Int, json: JsValue): EligibilityStatusResponse = {
    EligibilityStatusReads.read("", "", HttpResponse(status, json, Map.empty))
  }

}
