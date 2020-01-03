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

import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter
import uk.gov.hmrc.incometaxsubscriptioneligibility.services.AuditModel

case class EligibilityAuditModel(eligibilityResult: Boolean,
                                 sautr: String,
                                 isAgent: Boolean,
                                 reasons: Seq[ControlListParameter] = Seq.empty) extends AuditModel {

  private val formattedReasons = if (reasons.isEmpty) Map() else Map("failureReasons" -> reasons.map(_.errorMessage).mkString(", "))

  val auditType: String = "mtdITSAControlList"
  val transactionName: String = "ITSAControlListRequest"
  val detail: Map[String, String] = Map(
    "isSuccess" -> eligibilityResult.toString,
    "saUtr" -> sautr,
    "isAgent" -> isAgent.toString) ++
    formattedReasons

}