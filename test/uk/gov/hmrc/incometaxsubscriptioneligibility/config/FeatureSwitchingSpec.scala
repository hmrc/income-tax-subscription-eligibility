/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxsubscriptioneligibility.config

import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class FeatureSwitchingSpec extends PlaySpec with MockitoSugar with FeatureSwitching with BeforeAndAfterEach {
  val mockServicesConfig: ServicesConfig = mock[ServicesConfig]
  val mockConfig: Configuration = mock[Configuration]
  val appConfig = new AppConfig(mockServicesConfig, mockConfig)

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConfig)
    FeatureSwitch.switches foreach { switch =>
      sys.props -= switch.name
    }
  }

  "FeatureSwitching constants" should {
    "be true" in {
      FEATURE_SWITCH_ON mustBe "true"
    }

    "be false" in {
      FEATURE_SWITCH_OFF mustBe "false"
    }
  }

  "StubControlListEligible" should {
    "return true if StubControlListEligible feature switch is enabled in sys.props" in {
      enable(StubControlListEligible)
      isEnabled(StubControlListEligible) mustBe true
    }
    "return false if StubControlListEligible feature switch is disabled in sys.props" in {
      disable(StubControlListEligible)
      isEnabled(StubControlListEligible) mustBe false
    }

    "return false if StubControlListEligible feature switch does not exist" in {
      when(mockConfig.getOptional[String]("feature-switch.control-list-eligible")).thenReturn(None)
      isEnabled(StubControlListEligible) mustBe false
    }

    "return false if StubControlListEligible feature switch is not in sys.props but is set to 'off' in config" in {
      when(mockConfig.getOptional[String]("feature-switch.control-list-eligible")).thenReturn(Some(FEATURE_SWITCH_OFF))
      isEnabled(StubControlListEligible) mustBe false
    }

    "return true if StubControlListEligible feature switch is not in sys.props but is set to 'on' in config" in {
      when(mockConfig.getOptional[String]("feature-switch.control-list-eligible")).thenReturn(Some(FEATURE_SWITCH_ON))
      isEnabled(StubControlListEligible) mustBe true
    }
  }

  "UseStubForDesConnection" should {
    "return true if UseStubForDesConnection feature switch is enabled in sys.props" in {
      enable(UseStubForDesConnection)
      isEnabled(UseStubForDesConnection) mustBe true
    }
    "return false if UseStubForDesConnection feature switch is disabled in sys.props" in {
      disable(UseStubForDesConnection)
      isEnabled(UseStubForDesConnection) mustBe false
    }

    "return false if UseStubForDesConnection feature switch does not exist" in {
      when(mockConfig.getOptional[String]("feature-switch.use-stub-for-des-connection")).thenReturn(None)
      isEnabled(UseStubForDesConnection) mustBe false
    }

    "return false if UseStubForDesConnection feature switch is not in sys.props but is set to 'off' in config" in {
      when(mockConfig.getOptional[String]("feature-switch.use-stub-for-des-connection")).thenReturn(Some(FEATURE_SWITCH_OFF))
      isEnabled(UseStubForDesConnection) mustBe false
    }

    "return true if UseStubForDesConnection feature switch is not in sys.props but is set to 'on' in config" in {
      when(mockConfig.getOptional[String]("feature-switch.use-stub-for-des-connection")).thenReturn(Some(FEATURE_SWITCH_ON))
      isEnabled(UseStubForDesConnection) mustBe true
    }
  }
}
