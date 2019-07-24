
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
