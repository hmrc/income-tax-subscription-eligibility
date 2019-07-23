
package uk.gov.hmrc.incometaxsubscriptioneligibility.services

import org.scalatest.{Matchers, WordSpecLike}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.{ControlListParameter, StudentLoans}

class ConvertConfigValuesServicesISpec extends WordSpecLike with Matchers {

  class Setup(config: Map[ControlListParameter, Boolean]) {
    lazy val app: Application = new GuiceApplicationBuilder()
      .in(Environment.simple(mode = Mode.Dev))
      .configure(toConfigList(config))
      .build
    lazy val service: ConvertConfigValuesService = app.injector.instanceOf[ConvertConfigValuesService]

    app.stop()
  }

  def toConfigList(configValues: Map[ControlListParameter, Boolean]): Map[String, String] = {
    configValues.map {
      case (param, isEligible) =>
        s"control-list.${param.configKey}.eligible" -> isEligible.toString
    }
  }

  val allControlListParams: Set[ControlListParameter] = ControlListParameter.getParameterMap.values.toSet
  val testAllTrue: Map[ControlListParameter, Boolean] = allControlListParams.map(
    param => param -> true
  ).toMap
  val testAllFalse: Map[ControlListParameter, Boolean] = testAllTrue.mapValues(_ => false)

  "convertConfigValues service" should {
    "return an empty set" when {
      "all config values are true" in new Setup(testAllTrue) {
        val result = service.convertConfigValues()
        result shouldBe Set()
      }
    }
    "return a set with a value" when {
      "Student Loans is false" in new Setup(testAllTrue.updated(StudentLoans, false)) {
        val result = service.convertConfigValues()
        result shouldBe Set(StudentLoans)
      }
    }
    "return a set of values with all the control list parameters" when {
      "all config values are false" in new Setup(testAllFalse) {
        val result = service.convertConfigValues()
        result shouldBe allControlListParams
      }
    }
    "returns an empty set and ignores the incorrect parameter" when {
      "a control list parameter with all values as true and one an incorrect parameter" in {
        val allConfig: Map[String, String] = allControlListParams.map {
          param => s"control-list.${param.configKey}.eligibile" -> "true"
        }.toMap
        val testConfig: Map[String, String] = allConfig.updated("control-list.some-incorrect-param.eligible", "false")

        val testApp: Application = new GuiceApplicationBuilder()
          .in(Environment.simple(mode = Mode.Dev))
          .configure(testConfig)
          .build
        val service: ConvertConfigValuesService = testApp.injector.instanceOf[ConvertConfigValuesService]

        val result = service.convertConfigValues()
        result shouldBe Set()

        testApp.stop()
      }
    }
    "throws an exception for incorrect value" when {
      "a control list parameter are true but one has an incorrect value" in {
        val allConfig: Map[String, String] = allControlListParams.map {
          param => s"control-list.${param.configKey}.eligibile" -> "true"
        }.toMap
        val testConfig: Map[String, String] = allConfig.updated(s"control-list.${StudentLoans.configKey}.eligible", "ineligible")

        val testApp: Application = new GuiceApplicationBuilder()
          .in(Environment.simple(mode = Mode.Dev))
          .configure(testConfig)
          .build
        val service: ConvertConfigValuesService = testApp.injector.instanceOf[ConvertConfigValuesService]

        intercept[IllegalArgumentException] {
          service.convertConfigValues()
        }
        testApp.stop()
      }
    }
  }


}
