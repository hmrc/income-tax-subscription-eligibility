package uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsObject, Json}


object DesControlListApiStub extends WireMockMethods {

  private val desHeaders = Map(
    "Authorization" -> "Bearer dev",
    "Content-Type" -> "application/json",
    "Environment" -> "dev"
  )

  def stubGetControlList(sautr: String)(status: Int, body: JsObject = Json.obj()): StubMapping = {
    when(method = GET, uri = s"/income-tax/controlList/$sautr")
      .thenReturn(status = status, headers = desHeaders, body = body)
  }

}
