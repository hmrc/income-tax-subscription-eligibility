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
import play.api.libs.json.*
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatus.{Eligible, Ineligible}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason.*

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
        val writeJson = Json.toJson(EligibilityStatusSuccessResponse(
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
          val writeJson = Json.toJson(EligibilityStatusSuccessResponse(
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
          val writeJson = Json.toJson(EligibilityStatusSuccessResponse(
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
        "they are ineligible with an unhandled reason" in {
          val writeJson = Json.toJson(EligibilityStatusSuccessResponse(
            currentTaxYear = Ineligible,
            nextTaxYear = Ineligible,
            currentTaxYearFailureReasons = Seq(OutstandingReturns),
            nextTaxYearFailureReasons = Seq(OutstandingReturns)
          ))

          val expectedJson = Json.obj(
            "eligibleCurrentYear" -> false,
            "eligibleNextYear" -> false
          )

          writeJson mustBe expectedJson
        }
        "they are ineligible with a handled reason" should {
          "return a reason of Digitally Exempt" when {
            s"they are $DigitallyExempt" in {
              val writeJson = Json.toJson(EligibilityStatusSuccessResponse(
                currentTaxYear = Ineligible,
                nextTaxYear = Ineligible,
                currentTaxYearFailureReasons = Seq(DigitallyExempt),
                nextTaxYearFailureReasons = Seq(DigitallyExempt)
              ))

              val expectedJson = Json.obj(
                "eligibleCurrentYear" -> false,
                "eligibleNextYear" -> false,
                "exemptionReason" -> "Digitally Exempt"
              )

              writeJson mustBe expectedJson
            }
          }

          "return a reason of MTD Exempt Enduring" when {
            Seq(MTDExemptEnduring, MTDExempt28to29, MTDExempt27To28, MarriedCouplesAllowance, MinisterOfReligion, LloydsUnderwriter, BlindPersonsAllowance) foreach { reason =>
              s"they are $reason" in {
                val writeJson = Json.toJson(EligibilityStatusSuccessResponse(
                  currentTaxYear = Ineligible,
                  nextTaxYear = Ineligible,
                  currentTaxYearFailureReasons = Seq(reason),
                  nextTaxYearFailureReasons = Seq(reason)
                ))

                val expectedJson = Json.obj(
                  "eligibleCurrentYear" -> false,
                  "eligibleNextYear" -> false,
                  "exemptionReason" -> "MTD Exempt Enduring"
                )

                writeJson mustBe expectedJson
              }
            }
          }

          "return a reason of MTD Exempt 26/27" when {
            Seq(
              AveragingAdjustment, TrustIncome, FosterCarers, NonResidents, MTDExempt26To27
            ) foreach { reason =>
              s"they are $reason" in {
                val writeJson = Json.toJson(EligibilityStatusSuccessResponse(
                  currentTaxYear = Ineligible,
                  nextTaxYear = Ineligible,
                  currentTaxYearFailureReasons = Seq(reason),
                  nextTaxYearFailureReasons = Seq(reason)
                ))

                val expectedJson = Json.obj(
                  "eligibleCurrentYear" -> false,
                  "eligibleNextYear" -> false,
                  "exemptionReason" -> "MTD Exempt 26/27"
                )

                writeJson mustBe expectedJson
              }
            }
          }

          "return a reason of No Data" when {
            Seq(
              NoDataFound, Death, NonResidentCompanyLandlord, BankruptInsolvent,
              BankruptVoluntaryArrangement, MandationInhibit26To27, MandationInhibit27To28
            ) foreach { reason =>
              s"they are $reason" in {
                val writeJson = Json.toJson(EligibilityStatusSuccessResponse(
                  currentTaxYear = Ineligible,
                  nextTaxYear = Ineligible,
                  currentTaxYearFailureReasons = Seq(reason),
                  nextTaxYearFailureReasons = Seq(reason)
                ))

                val expectedJson = Json.obj(
                  "eligibleCurrentYear" -> false,
                  "eligibleNextYear" -> false,
                  "exemptionReason" -> "No Data"
                )

                writeJson mustBe expectedJson
              }
            }
          }

          "return the highest priority reason" when {
            "there are multiple exemption reasons given and the highest priority available is digitally exempt" in {
              val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
                currentTaxYear = Ineligible,
                nextTaxYear = Ineligible,
                currentTaxYearFailureReasons = Seq(NoDataFound, MTDExempt26To27, MTDExemptEnduring, DigitallyExempt),
                nextTaxYearFailureReasons = Seq(NoDataFound, MTDExempt26To27, MTDExemptEnduring, DigitallyExempt)
              ))

              val expectedJson = Json.obj(
                "eligibleCurrentYear" -> false,
                "eligibleNextYear" -> false,
                "exemptionReason" -> "Digitally Exempt"
              )

              writeJson mustBe expectedJson
            }
            "there are multiple exemption reasons given and the highest priority available is mtd exempt enduring" in {
              val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
                currentTaxYear = Ineligible,
                nextTaxYear = Ineligible,
                currentTaxYearFailureReasons = Seq(NoDataFound, MTDExempt26To27, MTDExemptEnduring),
                nextTaxYearFailureReasons = Seq(NoDataFound, MTDExempt26To27, MTDExemptEnduring)
              ))

              val expectedJson = Json.obj(
                "eligibleCurrentYear" -> false,
                "eligibleNextYear" -> false,
                "exemptionReason" -> "MTD Exempt Enduring"
              )

              writeJson mustBe expectedJson
            }
            "there are multiple exemption reasons given and the highest priority available is mtd exempt 26-27" in {
              val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
                currentTaxYear = Ineligible,
                nextTaxYear = Ineligible,
                currentTaxYearFailureReasons = Seq(NoDataFound, MTDExempt26To27),
                nextTaxYearFailureReasons = Seq(NoDataFound, MTDExempt26To27)
              ))

              val expectedJson = Json.obj(
                "eligibleCurrentYear" -> false,
                "eligibleNextYear" -> false,
                "exemptionReason" -> "MTD Exempt 26/27"
              )

              writeJson mustBe expectedJson
            }
            "there are multiple exemption reasons given and the highest priority available is no data found" in {
              val writeJson = Json.toJson[EligibilityStatusSuccessResponse](EligibilityStatusSuccessResponse(
                currentTaxYear = Ineligible,
                nextTaxYear = Ineligible,
                currentTaxYearFailureReasons = Seq(NoDataFound),
                nextTaxYearFailureReasons = Seq(NoDataFound)
              ))

              val expectedJson = Json.obj(
                "eligibleCurrentYear" -> false,
                "eligibleNextYear" -> false,
                "exemptionReason" -> "No Data"
              )

              writeJson mustBe expectedJson
            }
          }
        }
      }
    }
  }

}
