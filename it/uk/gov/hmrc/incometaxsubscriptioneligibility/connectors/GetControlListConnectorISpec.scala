package uk.gov.hmrc.incometaxsubscriptioneligibility.connectors

import java.util.UUID

import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.ComponentSpecBase
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.externalservicemocks.DesControlListApiStub.stubGetControlList
import uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers.GetControlListHttpParser.ControlListDataNotFound
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.NonResidentCompanyLandlord

import scala.concurrent.ExecutionContext.Implicits.global


class GetControlListConnectorISpec extends ComponentSpecBase {

  lazy val connector: GetControlListConnector = app.injector.instanceOf[GetControlListConnector]

  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  val testSautr: String = UUID.randomUUID().toString

  "getControlList" should {
    "return a Right(Set[ControlListParameters])" when {
      "DES returns a valid control list string" in {
        val testControlListString: String = "1000000000000000000000000000000000000000"
        val testJson: JsObject = Json.obj(
          "nino" -> "AA123456A",
          "year" -> "2019",
          "controlListInformation" -> testControlListString
        )

        stubGetControlList(testSautr)(status = OK, body = testJson)

        val res = connector.getControlList(testSautr)

        await(res) mustBe Right(Set(NonResidentCompanyLandlord))
      }
    }

    "return a Left(ControlListDataNotFound)" when {
      "DES returns a NOT_FOUND" in {
        stubGetControlList(testSautr)(status = NOT_FOUND)

        val res = connector.getControlList(testSautr)

        await(res) mustBe Left(ControlListDataNotFound)
      }
    }
  }

}
