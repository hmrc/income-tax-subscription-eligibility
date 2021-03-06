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

package uk.gov.hmrc.incometaxsubscriptioneligibility.helpers

import uk.gov.hmrc.domain.Generator

object IntegrationTestConstants {

  val testAppName = "testAppName"
  val testUrl = "/testUrl"


  object Audit {
    val testAuditType = "testAuditType"
    val testTransactionName = "testTransactionName"
    val testDetail = Map("foo" -> "bar")
    val agentServiceIdentifierKey = "AgentReferenceNumber"
    val agentServiceEnrolmentName = "HMRC-AS-AGENT"
    lazy val testARN = new Generator().nextAtedUtr.utr //Not a valid ARN, for test purposes only
  }

}
