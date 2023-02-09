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

package uk.gov.hmrc.incometaxsubscriptioneligibility.models.audits

import org.scalatestplus.play.PlaySpec

class EligibilityAuditModelSpec extends PlaySpec {

  val sautr: String = "test-sautr"
  val controlListCheck: String = "test-control-list-check"
  val agentReferenceNumber: String = "test-agent-reference-number"
  val testReasons: Set[String] = Set("reason one", "reason two")

  "EligibilityAuditModel" must {
    "produce the correct auditType and detail" when {
      "no agent reference number or reasons are provided" in {
        val model = EligibilityAuditModel(sautr, None, controlListCheck)
        model.auditType mustBe "mtdITSAControlList"
        model.transactionName mustBe "ITSAControlListRequest"
        model.detail mustBe Map(
          "saUtr" -> sautr,
          "userType" -> "individual",
          "controlListCheck" -> controlListCheck,
          "isSuccess" -> "true"
        )
      }
      "an agent reference number and reasons are provided" in {
        val model = EligibilityAuditModel(sautr, Some(agentReferenceNumber), controlListCheck, testReasons)
        model.auditType mustBe "mtdITSAControlList"
        model.transactionName mustBe "ITSAControlListRequest"
        model.detail mustBe Map(
          "agentReferenceNumber" -> agentReferenceNumber,
          "saUtr" -> sautr,
          "userType" -> "agent",
          "controlListCheck" -> controlListCheck,
          "isSuccess" -> "false",
          "failureReasons" -> testReasons.mkString(", ")
        )
      }
    }
  }

}
