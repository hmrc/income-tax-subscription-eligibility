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
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HttpResponse, InternalServerException}
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.GetControlListReads.read
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.NonResidentCompanyLandlord
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.{Accruals, Date, OverseasProperty, PrepopData, SelfEmploymentData, UkProperty}

class GetControlListHttpParserSpec extends PlaySpec {

  val testDate = "01012018"

  "GetControlListHttpReads" should {
    "parse ControlListParameters" when {
      "the controlList string in the correct format" in {
        val testControlListString = "1000000000000000000000000000000000000000"
        val testJson = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString
        )
        val testHttpResponse = HttpResponse(status = OK, json = testJson, headers = Map.empty)

        val res = read(method = "GET", url = "/", response = testHttpResponse)

        res mustBe Right(GetControlListSuccessResponse(Set(NonResidentCompanyLandlord)))
      }
    }

    "parse prepopData" when {
      "the prepopData contains a UK property" in {
        val testControlListString = "1000000000000000000000000000000000000000"
        val testJson = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString,
          "prepopData" -> Json.obj(
            "ukPropertyStartDate" -> testDate,
            "ukPropertyAccountingMethod" -> "Y"
          )
        )
        val testHttpResponse = HttpResponse(status = OK, json = testJson, headers = Map.empty)

        val res = read(method = "GET", url = "/", response = testHttpResponse)

        res mustBe Right(
          GetControlListSuccessResponse(
            controlList = Set(NonResidentCompanyLandlord),
            prepopData = Some(PrepopData(
              ukProperty = Some(UkProperty(
                ukPropertyStartDate = Some(Date("1", "1", "2018")),
                ukPropertyAccountingMethod = Some(Accruals)
              ))
            ))
          )
        )
      }

      "the prepopData contains an overseas property" in {
        val testControlListString = "1000000000000000000000000000000000000000"
        val testJson = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString,
          "prepopData" -> Json.obj(
            "overseasPropertyStartDate" -> testDate,
            "overseasPropertyAccountingMethod" -> "Y"
          )
        )
        val testHttpResponse = HttpResponse(status = OK, json = testJson, headers = Map.empty)

        val res = read(method = "GET", url = "/", response = testHttpResponse)

        res mustBe Right(
          GetControlListSuccessResponse(
            controlList = Set(NonResidentCompanyLandlord),
            prepopData = Some(PrepopData(
              overseasProperty = Some(OverseasProperty(
                overseasPropertyStartDate = Some(Date("1", "1", "2018")),
                overseasPropertyAccountingMethod = Some(Accruals)
              ))
            ))
          )
        )
      }

      "the prepopData contains a self employments" in {
        val testControlListString = "1000000000000000000000000000000000000000"
        val testJson = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString,
          "prepopData" -> Json.obj(
            "selfEmployments" -> Json.arr(
              Json.obj(
                "businessName" -> "Test business name",
                "businessTradeName" -> "Test business trade name",
                "businessStartDate" -> testDate,
                "businessAccountingMethod" -> "Y"
              )
            )
          )
        )
        val testHttpResponse = HttpResponse(status = OK, json = testJson, headers = Map.empty)

        val res = read(method = "GET", url = "/", response = testHttpResponse)

        res mustBe Right(
          GetControlListSuccessResponse(
            controlList = Set(NonResidentCompanyLandlord),
            prepopData = Some(PrepopData(
              selfEmployments = Some(Vector(
                SelfEmploymentData(
                  businessName = Some("Test business name"),
                  businessTradeName = Some("Test business trade name"),
                  businessStartDate = Some(Date("1", "1", "2018")),
                  businessAccountingMethod = Some(Accruals)
                )
              ))
            ))
          )
        )
      }
    }

    "fail" when {
      "the controlList string in an incorrect format" in {
        val testControlListString = "100000000000000000000000000000000000000"
        val testJson = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString
        )
        val testHttpResponse = HttpResponse(status = OK, json = testJson, headers = Map.empty)

        val res = read(method = "GET", url = "/", response = testHttpResponse)

        res mustBe Left(InvalidControlListFormat)
      }

      "NOT_FOUND is returned" in {
        val testHttpResponse = HttpResponse(status = NOT_FOUND, json = Json.obj(), headers = Map.empty)

        val res = read(method = "GET", url = "/", response = testHttpResponse)

        res mustBe Left(ControlListDataNotFound)
      }
    }

    "throw exceptions" when {
      "invalid json is returned" in {
        val testHttpResponse = HttpResponse(status = OK, json = Json.obj(), headers = Map.empty)

        intercept[InternalServerException] {
          read(method = "GET", url = "/", response = testHttpResponse)
        }
      }
      "BAD_REQUEST is returned" in {

        val testHttpResponse = HttpResponse(status = BAD_REQUEST, json = Json.obj(), headers = Map.empty)

        intercept[InternalServerException] {
          read(method = "GET", url = "/", response = testHttpResponse)
        }
      }

      "INTERNAL_SERVER_ERROR is returned" in {
        val testHttpResponse = HttpResponse(status = INTERNAL_SERVER_ERROR, json = Json.obj(), headers = Map.empty)

        intercept[InternalServerException] {
          read(method = "GET", url = "/", response = testHttpResponse)
        }
      }

      "SERVICE_UNAVAILABLE is returned" in {
        val testHttpResponse = HttpResponse(status = SERVICE_UNAVAILABLE, json = Json.obj(), headers = Map.empty)

        intercept[InternalServerException] {
          read(method = "GET", url = "/", response = testHttpResponse)
        }
      }
    }
  }
}
