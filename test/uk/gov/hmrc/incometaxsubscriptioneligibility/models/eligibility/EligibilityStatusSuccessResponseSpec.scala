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

package uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility

import org.scalatestplus.play.PlaySpec
import play.api.libs.json._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatus.{Eligible, Ineligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason.{DigitallyExempt, MTDExempt26To27, MTDExempt27To28, MTDExemptEnduring, NonResidentCompanyLandlord}

class EligibilityStatusSuccessResponseSpec extends PlaySpec {

  val fullReadJson: JsObject = Json.obj(
    "CY" -> "No",
    "CY1" -> "Yes",
    "ReasonKeyCY" -> Json.arr(
      "Digitally Exempt"
    ),
    "ReasonKeyCY1" -> Json.arr()
  )

  val fullReadModel: EligibilityStatusSuccessResponse = EligibilityStatusSuccessResponse(
    currentTaxYear = Ineligible,
    nextTaxYear = Eligible,
    currentTaxYearFailureReasons = Seq(DigitallyExempt),
    nextTaxYearFailureReasons = Seq.empty
  )

  "EligibilityStatusSuccessResponse" must {
    "read from json successfully" when {
      "all fields are present in the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson) mustBe JsSuccess(fullReadModel)
      }
    }
    "fail to read from json" when {
      "'CY' is missing from the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson - "CY") mustBe JsError(__ \ "CY", "error.path.missing")
      }
      "'CY1' is missing from the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson - "CY1") mustBe JsError(__ \ "CY1", "error.path.missing")
      }
      "'ReasonKeyCY' is missing from the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson - "ReasonKeyCY") mustBe JsError(__ \ "ReasonKeyCY", "error.path.missing")
      }
      "'ReasonKeyCY1' is missing from the json" in {
        Json.fromJson[EligibilityStatusSuccessResponse](fullReadJson - "ReasonKeyCY1") mustBe JsError(__ \ "ReasonKeyCY1", "error.path.missing")
      }
    }

    "write to json successfully" when {
      "the response is fully eligible" in {
        val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
          currentTaxYear = Eligible,
          nextTaxYear = Eligible,
          currentTaxYearFailureReasons = Seq.empty,
          nextTaxYearFailureReasons = Seq.empty
        ))

        val expectedJson = Json.obj(
          "eligibleCurrentYear" -> true,
          "eligibleNextYear" -> true
        )

        writeJson mustBe expectedJson
      }
      "the response is eligible for next year only" when {
        "the current year failed with a non exception reason" in {
          val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
            currentTaxYear = Ineligible,
            nextTaxYear = Eligible,
            currentTaxYearFailureReasons = Seq(NonResidentCompanyLandlord),
            nextTaxYearFailureReasons = Seq.empty
          ))

          val expectedJson = Json.obj(
            "eligibleCurrentYear" -> false,
            "eligibleNextYear" -> true
          )

          writeJson mustBe expectedJson
        }
        "the current year failed with a exemption reason" in {
          val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
            currentTaxYear = Ineligible,
            nextTaxYear = Eligible,
            currentTaxYearFailureReasons = Seq(DigitallyExempt),
            nextTaxYearFailureReasons = Seq.empty
          ))

          val expectedJson = Json.obj(
            "eligibleCurrentYear" -> false,
            "eligibleNextYear" -> true
          )

          writeJson mustBe expectedJson
        }
      }
      "the response is ineligible for both years" when {
        "they are ineligible with a non exception reason" in {
          val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
            currentTaxYear = Ineligible,
            nextTaxYear = Ineligible,
            currentTaxYearFailureReasons = Seq(NonResidentCompanyLandlord),
            nextTaxYearFailureReasons = Seq(NonResidentCompanyLandlord)
          ))

          val expectedJson = Json.obj(
            "eligibleCurrentYear" -> false,
            "eligibleNextYear" -> false
          )

          writeJson mustBe expectedJson
        }
        "they are ineligible with a exemption reason" when {
          "there is a single exemption reason given" in {
            val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
              currentTaxYear = Ineligible,
              nextTaxYear = Ineligible,
              currentTaxYearFailureReasons = Seq(DigitallyExempt),
              nextTaxYearFailureReasons = Seq(DigitallyExempt)
            ))

            val expectedJson = Json.obj(
              "eligibleCurrentYear" -> false,
              "eligibleNextYear" -> false,
              "exemptionReason" -> DigitallyExempt.key
            )

            writeJson mustBe expectedJson
          }
          "there are multiple exemption reasons given and the highest priority available is digitally exempt" in {
            val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
              currentTaxYear = Ineligible,
              nextTaxYear = Ineligible,
              currentTaxYearFailureReasons = Seq(MTDExempt26To27, MTDExempt27To28, MTDExemptEnduring, DigitallyExempt),
              nextTaxYearFailureReasons = Seq(MTDExempt26To27, MTDExempt27To28, MTDExemptEnduring, DigitallyExempt)
            ))

            val expectedJson = Json.obj(
              "eligibleCurrentYear" -> false,
              "eligibleNextYear" -> false,
              "exemptionReason" -> DigitallyExempt.key
            )

            writeJson mustBe expectedJson
          }
          "there are multiple exemption reasons given and the highest priority available is mtd exempt enduring" in {
            val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
              currentTaxYear = Ineligible,
              nextTaxYear = Ineligible,
              currentTaxYearFailureReasons = Seq(MTDExempt26To27, MTDExempt27To28, MTDExemptEnduring),
              nextTaxYearFailureReasons = Seq(MTDExempt26To27, MTDExempt27To28, MTDExemptEnduring)
            ))

            val expectedJson = Json.obj(
              "eligibleCurrentYear" -> false,
              "eligibleNextYear" -> false,
              "exemptionReason" -> MTDExemptEnduring.key
            )

            writeJson mustBe expectedJson
          }
          "there are multiple exemption reasons given and the highest priority available is mtd exempt 27-28" in {
            val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
              currentTaxYear = Ineligible,
              nextTaxYear = Ineligible,
              currentTaxYearFailureReasons = Seq(MTDExempt26To27, MTDExempt27To28),
              nextTaxYearFailureReasons = Seq(MTDExempt26To27, MTDExempt27To28)
            ))

            val expectedJson = Json.obj(
              "eligibleCurrentYear" -> false,
              "eligibleNextYear" -> false,
              "exemptionReason" -> MTDExempt27To28.key
            )

            writeJson mustBe expectedJson
          }
          "there are multiple exemption reasons given and the highest priority available is mtd exempt 26-27" in {
            val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
              currentTaxYear = Ineligible,
              nextTaxYear = Ineligible,
              currentTaxYearFailureReasons = Seq(MTDExempt26To27),
              nextTaxYearFailureReasons = Seq(MTDExempt26To27)
            ))

            val expectedJson = Json.obj(
              "eligibleCurrentYear" -> false,
              "eligibleNextYear" -> false,
              "exemptionReason" -> MTDExempt26To27.key
            )

            writeJson mustBe expectedJson
          }
        }
      }
    }
  }

}
