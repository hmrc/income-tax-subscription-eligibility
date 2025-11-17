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

import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.incometaxsubscriptioneligibility.config.{AppConfig, FeatureSwitching, StubControlListEligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.AuthStub.stubAuth
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.EligibilityStatusAPIStub.{stubGetEligibilityStatus, verifyGetEligibilityStatus}
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.{ComponentSpecBase, ControlListConfigTestHelper}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason._

class EligibilityStatusControllerISpec extends ComponentSpecBase with ControlListConfigTestHelper with FeatureSwitching {

  override def beforeEach(): Unit = {
    super.beforeEach()
    disable(StubControlListEligible)
  }

  val appConfig: AppConfig = mock[AppConfig]

  val testNino = "test-nino"
  val testUtr = "test-utr"

  val eligibilityStatusSuccessfulAPIJson: JsObject = Json.obj(
    "CY" -> "No",
    "CY1" -> "Yes",
    "ReasonKeyCY" -> Json.arr("Digitally Exempt"),
    "ReasonKeyCY1" -> Json.arr()
  )

  def eligibilityStatusSuccessfulBothYearReasons(reason: EligibilityStatusFailureReason): JsObject = Json.obj(
    "CY" -> "No",
    "CY1" -> "No",
    "ReasonKeyCY" -> Json.arr(reason.key),
    "ReasonKeyCY1" -> Json.arr(reason.key)
  )

  val eligibilityStatusSuccessfulControllerJson: JsObject = Json.obj(
    "eligibleCurrentYear" -> false,
    "eligibleNextYear" -> true
  )

  def eligibilityStatusSuccessfulWithReasonJson(reason: String): JsObject = Json.obj(
    "eligibleCurrentYear" -> false,
    "eligibleNextYear" -> false,
    "exemptionReason" -> reason
  )

  s"GET ${routes.EligibilityStatusController.getEligibilityStatus(nino = testNino, utr = testUtr).url}" must {
    "return an OK with the eligibility status for current and next tax year" when {
      "the stub eligibility feature switch is disabled" when {
        "the connector returns a successful response" in new Server(defaultApp) {
          override def running(): Unit = {
            stubAuth(OK, Json.obj())
            stubGetEligibilityStatus(testNino)(
              status = OK,
<<<<<<< HEAD
              body = eligibilityStatusSuccessfulAPIJson
=======
              body = eligibilityStatusSuccessfulBothYearReasons(OutstandingReturns)
>>>>>>> 331ec70fee5f0c939dffb62e58bf3bc5fd6aacf5
            )

            val result: WSResponse = get(s"/eligibility/nino/$testNino/utr/$testUtr")

            result must have(
              httpStatus(OK),
              jsonBodyAs(eligibilityStatusSuccessfulControllerJson)
            )

            verifyGetEligibilityStatus(testNino)
          }
        }
        "the connector returns failure reasons in the next year" which {
          "have non exception reasons" in new Server(defaultApp) {
            override def running(): Unit = {
              stubAuth(OK, Json.obj())
              stubGetEligibilityStatus(testNino)(
                status = OK,
                body = eligibilityStatusSuccessfulBothYearReasons(NonResidentCompanyLandlord)
              )

              val result: WSResponse = get(s"/eligibility/nino/$testNino/utr/$testUtr")

              result must have(
                httpStatus(OK),
                jsonBodyAs(
                  Json.obj(
                    "eligibleCurrentYear" -> false,
                    "eligibleNextYear" -> false
                  )
                )
              )

              verifyGetEligibilityStatus(testNino)
            }
          }
          "have an exception reason" which {
            Seq(
              DigitallyExempt -> "Digitally Exempt",
              MTDExemptEnduring -> "MTD Exempt Enduring",
              MTDExempt26To27 -> "MTD Exempt 26/27",
              NoDataFound -> "No Data"
            ) foreach { case (reason, reasonResultKey) =>
              s"is the $reason reason" in new Server(defaultApp) {
                override def running(): Unit = {
                  stubAuth(OK, Json.obj())
                  stubGetEligibilityStatus(testNino)(
                    status = OK,
                    body = eligibilityStatusSuccessfulBothYearReasons(reason)
                  )

                  val result: WSResponse = get(s"/eligibility/nino/$testNino/utr/$testUtr")

<<<<<<< HEAD
                  result must have(
                    httpStatus(OK),
                    jsonBodyAs(eligibilityStatusSuccessfulWithReasonJson(reason))
                  )
=======
                result must have(
                  httpStatus(OK),
                  jsonBodyAs(eligibilityStatusSuccessfulWithReasonJson(reasonResultKey))
                )
>>>>>>> 331ec70fee5f0c939dffb62e58bf3bc5fd6aacf5

                  verifyGetEligibilityStatus(testNino)
                }
              }
            }
          }
        }
      }
      "the stub eligibility feature switch is enabled" in new Server(defaultApp) {
        override def running(): Unit = {
          enable(StubControlListEligible)
          stubAuth(OK, Json.obj())

          val result: WSResponse = get(s"/eligibility/nino/$testNino/utr/$testUtr")

          result must have(
            httpStatus(OK),
            jsonBodyAs(
              Json.obj(
                "eligibleCurrentYear" -> true,
                "eligibleNextYear" -> true
              )
            )
          )

          verifyGetEligibilityStatus(testNino, count = 0)
        }
      }
    }
    "return an internal server error" when {
      "the connector returns invalid json" in new Server(defaultApp) {
        override def running(): Unit = {
          stubAuth(OK, Json.obj())
          stubGetEligibilityStatus(testNino)(
            status = OK,
            body = Json.obj()
          )

          val result: WSResponse = get(s"/eligibility/nino/$testNino/utr/$testUtr")

          result must have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )

          verifyGetEligibilityStatus(testNino)
        }

        "the connector returns an unexpected status" in new Server(defaultApp) {
          override def running(): Unit = {
            stubAuth(OK, Json.obj())
            stubGetEligibilityStatus(testNino)(
              status = INTERNAL_SERVER_ERROR
            )

            val result: WSResponse = get(s"/eligibility/nino/$testNino/utr/$testUtr")

            result must have(
              httpStatus(INTERNAL_SERVER_ERROR)
            )

            verifyGetEligibilityStatus(testNino)
          }
        }
      }
    }
  }

}
