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

package uk.gov.hmrc.incometaxsubscriptioneligibility.connectors

import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.ComponentSpecBase
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.EligibilityStatusAPIStub.stubGetEligibilityStatus
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.EligibilityStatusHttpParser.EligibilityStatusResponse
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatus._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.EligibilityStatusFailureReason._
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.eligibility.{EligibilityStatusFailure, EligibilityStatusSuccessResponse}

import scala.concurrent.Future

class EligibilityStatusConnectorISpec extends ComponentSpecBase {

  def connector(implicit app: Application): EligibilityStatusConnector = app.injector.instanceOf[EligibilityStatusConnector]

  val testNino: String = "test-nino"
  val testUTR: String = "test-utr"

  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "getEligibilityStatus" must {
    "return a eligibility status success response" when {
      "the API returns an OK status with valid json" in new App(defaultApp) {
        override def running(): Unit = {
          stubGetEligibilityStatus(testNino, testUTR)(
            status = OK,
            body = Json.obj(
              "CY" -> "No",
              "CY1" -> "Yes",
              "ReasonKeyCY" -> Json.arr(
                "No Data Found", "Non-residents", "Non-resident Company Landlord", "Debt Management", "Minister of Religion",
                "Lloyds Underwriter", "Averaging Adjustment", "Trust Income", "Partnership Income", "Blind Person's Allowance",
                "Foster Carers", "Married Couple's Allowance", "Compliance Activity", "Budget Payment Plan", "Death",
                "Time To Pay", "Time To Pay (Self Serve)", "Disguised Renumeration Involvement", "No Repayment Signal",
                "Outstanding Returns", "Enforcement Signal", "Collection Priority Signal", "Bankrupt - Insolvent",
                "Bankrupt - Voluntary Arrangement", "Digitally Exempt", "MTD Exempt (Enduring)", "MTD Exempt 26/27",
                "MTD Exempt 27/28", "Mandation Inhibit 26/27", "Mandation Inhibit 27/28"
              ),
              "ReasonKeyCY1" -> Json.arr()
            )
          )

          val result: Future[EligibilityStatusResponse] = connector.getEligibilityStatus(testNino, testUTR)
          
          await(result) mustBe Right(EligibilityStatusSuccessResponse(
            currentTaxYear = Ineligible,
            nextTaxYear = Eligible,
            currentTaxYearFailureReasons = Seq(
              NoDataFound, NonResidents, NonResidentCompanyLandlord, DebtManagement, MinisterOfReligion, LloydsUnderwriter,
              AveragingAdjustment, TrustIncome, PartnershipIncome, BlindPersonsAllowance, FosterCarers, MarriedCouplesAllowance,
              ComplianceActivity, BudgetPaymentPlan, Death, TimeToPay, TimeToPaySelfServe, DisguisedRemunerationInvolvement,
              NoRepaymentSignal, OutstandingReturns, EnforcementSignal, CollectionPrioritySignal, BankruptInsolvent,
              BankruptVoluntaryArrangement, DigitallyExempt, MTDExemptEnduring, MTDExempt26To27, MTDExempt27To28,
              MandationInhibit26To27, MandationInhibit27To28
            ),
            nextTaxYearFailureReasons = Seq()
          ))
        }
      }
    }
    "return an eligibility failure response" when {
      "the API returns an OK status with invalid json" in new App(defaultApp) {
        override def running(): Unit = {
          stubGetEligibilityStatus(testNino, testUTR)(
            status = OK,
            body = Json.obj()
          )

          val result: Future[EligibilityStatusResponse] = connector.getEligibilityStatus(testNino, testUTR)

          await(result) mustBe Left(EligibilityStatusFailure.InvalidJson)
        }
      }
      "the API returns an unexpected status" in new App(defaultApp) {
        override def running(): Unit = {
          stubGetEligibilityStatus(testNino, testUTR)(
            status = INTERNAL_SERVER_ERROR,
            body = Json.obj()
          )

          val result: Future[EligibilityStatusResponse] = connector.getEligibilityStatus(testNino, testUTR)

          await(result) mustBe Left(EligibilityStatusFailure.UnexpectedStatus)
        }
      }
    }
  }

}
