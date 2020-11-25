/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits

import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListIndices.{MARRIAGE_ALLOWANCE, NON_RESIDENT_COMPANY_LANDLORD, STUDENT_LOANS}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter

class EligibilityAuditModelSpec extends PlaySpec {

  "The Audit Eligibility model" should {
    val expectedDetailSuccess = Map("isSuccess" -> "true", "saUtr" -> "1234567890", "userType" -> "individual")
    val expectedDetailFailure = Map("isSuccess" -> "false", "saUtr" -> "1234567890", "userType" -> "individual",
      "failureReasons" -> "Non Resident Company Landlord, Student Loans, Transfers/receives Marriage Allowance")
    val expectedDetailAgentSuccess = Map("isSuccess" -> "true", "saUtr" -> "1234567890", "userType" -> "agent", "agentReferenceNumber" -> "123456789")
    val expectedDetailAgentFailure = Map("isSuccess" -> "false", "saUtr" -> "1234567890", "userType" -> "agent", "agentReferenceNumber" -> "123456789",
      "failureReasons" -> "Non Resident Company Landlord, Student Loans, Transfers/receives Marriage Allowance")

    "Have detail with a utr and isSuccess set" when {
      "the audit event is for a success" in {
        val result = EligibilityAuditModel(true, "1234567890", "individual", None)
        result.auditType mustBe "mtdITSAControlList"
        result.detail mustBe expectedDetailSuccess
      }
    }
    "Have detail with utr, isSuccess and failureReasons set" when {
      "the audit event is for a failure" in {
        val seqOfErrors = Seq(ControlListParameter.getParameterMap(NON_RESIDENT_COMPANY_LANDLORD),
          ControlListParameter.getParameterMap(STUDENT_LOANS),
          ControlListParameter.getParameterMap(MARRIAGE_ALLOWANCE))

        val result = EligibilityAuditModel(false, "1234567890", "individual", None, seqOfErrors)
        result.auditType mustBe "mtdITSAControlList"
        result.detail mustBe expectedDetailFailure
      }
    }

    "Have detail with utr, isSuccess & agentReferenceNumber set for agent userType" when {
      "the audit event is for a success" in {
        val result = EligibilityAuditModel(true, "1234567890", "agent", Some("123456789"))
        result.auditType mustBe "mtdITSAControlList"
        result.detail mustBe expectedDetailAgentSuccess
      }
    }
    "Have detail with utr, isSuccess, agentReferenceNumber & failureReasons set for agent userType" when {
      "the audit event is for a failure" in {
        val seqOfErrors = Seq(ControlListParameter.getParameterMap(NON_RESIDENT_COMPANY_LANDLORD),
          ControlListParameter.getParameterMap(STUDENT_LOANS),
          ControlListParameter.getParameterMap(MARRIAGE_ALLOWANCE))

        val result = EligibilityAuditModel(false, "1234567890", "agent", Some("123456789"), seqOfErrors)
        result.auditType mustBe "mtdITSAControlList"
        result.detail mustBe expectedDetailAgentFailure
      }
    }
  }
}
