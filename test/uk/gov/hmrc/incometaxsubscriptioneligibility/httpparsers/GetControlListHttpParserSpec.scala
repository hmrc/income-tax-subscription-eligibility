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

package uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HttpResponse, InternalServerException}
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.GetControlListReads.read
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.NonResidentCompanyLandlord

class GetControlListHttpParserSpec extends PlaySpec {

  "GetControlListHttpReads" should {
    "return Right(Set[ControlListParameters]))" when {
      "the controlList string in the correct format" in {
        val testControlListString = "1000000000000000000000000000000000000000"
        val testJson = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString
        )
        val testHttpResponse = HttpResponse(responseStatus = OK, responseJson = Some(testJson))

        val res = read(method = "GET", url = "/", response = testHttpResponse)

        res mustBe Right(Set(NonResidentCompanyLandlord))
      }
    }
    "return Left(InvalidControlListFormat)" when {
      "the controlList string in an incorrect format" in {
        val testControlListString = "100000000000000000000000000000000000000"
        val testJson = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString
        )
        val testHttpResponse = HttpResponse(responseStatus = OK, responseJson = Some(testJson))

        val res = read(method = "GET", url = "/", response = testHttpResponse)

        res mustBe Left(InvalidControlListFormat)
      }
    }
    "return Left(ControlListDataNotFound)" when {
      "NOT_FOUND is returned" in {
        val testHttpResponse = HttpResponse(responseStatus = NOT_FOUND, responseJson = Some(Json.obj()))

        val res = read(method = "GET", url = "/", response = testHttpResponse)

        res mustBe Left(ControlListDataNotFound)
      }
    }
    "throw exceptions" when {
      "invalid json is returned" in {
        val testHttpResponse = HttpResponse(responseStatus = OK, responseJson = Some(Json.obj()))

        intercept[InternalServerException] {
          read(method = "GET", url = "/", response = testHttpResponse)
        }
      }
      "BAD_REQUEST is returned" in {

        val testHttpResponse = HttpResponse(responseStatus = BAD_REQUEST, responseJson = Some(Json.obj()))

        intercept[InternalServerException] {
          read(method = "GET", url = "/", response = testHttpResponse)
        }
      }
      "INTERNAL_SERVER_ERROR is returned" in {
        val testHttpResponse = HttpResponse(responseStatus = INTERNAL_SERVER_ERROR, responseJson = Some(Json.obj()))

        intercept[InternalServerException] {
          read(method = "GET", url = "/", response = testHttpResponse)
        }
      }
      "SERVICE_UNAVAILABLE is returned" in {
        val testHttpResponse = HttpResponse(responseStatus = SERVICE_UNAVAILABLE, responseJson = Some(Json.obj()))

        intercept[InternalServerException] {
          read(method = "GET", url = "/", response = testHttpResponse)
        }
      }
    }
  }

}
