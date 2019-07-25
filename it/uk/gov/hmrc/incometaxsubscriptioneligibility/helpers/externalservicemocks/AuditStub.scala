/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsObject, Json}

object AuditStub extends WireMockMethods {

    val auditResponse = Json.parse(
      """
      |{
      |  "auditSource": "xxxx-xxxx",
      |  "auditType": "xxxx",
      |  "eventID": "xxxx",
      |  "generatedAt": "xxx",
      |  "version": "1",
      |  "requestID": "xxxx",
      |  "method": "GET",
      |  "path": "xxx",
      |  "authorisationToken": "xxxx",
      |  "responseStatus": "200",
      |  "responsePayload": {
      |    "type": "application/json"
      |  },
      |  "clientHeaders": {
      |    "accept": "xxxx",
      |    "accept-encoding": "xxxx",
      |    "connection": "xxxx",
      |    "user-agent": "xxxx"
      |  },
      |  "responseHeaders": {
      |    "cache-control": "xxxx",
      |    "content-length": "xxxx",
      |    "date": "xxxx",
      |    "server": "xxxx",
      |    "vary": "xxxx"
      |  }
      |}
    """.stripMargin).as[JsObject]
  val auditUri = "/write/audit"

 def stubAudit(statusReturnedFromAuth: Int, bodyFromAudit: JsObject = auditResponse): StubMapping = {
   when(method = POST, uri = auditUri)
     .thenReturn(status = statusReturnedFromAuth, body = bodyFromAudit)
 }
}
