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

package uk.gov.hmrc.incometaxsubscriptioneligibility.services

import uk.gov.hmrc.incometaxsubscriptioneligibility.config.StubControlListEligible
import uk.gov.hmrc.incometaxsubscriptioneligibility.helpers.FeatureSwitchingSpec

class ControlListEligibilityServiceSpec extends FeatureSwitchingSpec {

  val testSautr: String = "1234567890"

  "getEligibilityStatus" should {
    "return true" when {
      "feature switch is enabled" in {
        enable(StubControlListEligible)

        val testService = new ControlListEligibilityService
        val result = testService.getEligibilityStatus(testSautr)

        result mustBe true
      }
    }

    "return false" when {
      "feature switch is disabled" in {
        // Don't need to disable feature switches as they are disabled by an overridden method in FeatureSwitchingSpec

        val testService = new ControlListEligibilityService
        val result = testService.getEligibilityStatus(testSautr)

        result mustBe false
      }
    }
  }

}
