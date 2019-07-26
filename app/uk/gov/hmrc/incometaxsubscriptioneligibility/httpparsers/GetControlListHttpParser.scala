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

package uk.gov.hmrc.incometaxsubscriptioneligibility.httpparsers

import play.api.http.Status._
import play.api.libs.json.JsSuccess
import uk.gov.hmrc.http.{HttpReads, HttpResponse, InternalServerException}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.ControlListParameter

object GetControlListHttpParser {

  type GetControlListResponse = Either[ControlListError, Set[ControlListParameter]]

  sealed trait ControlListError

  case object ControlListDataNotFound extends ControlListError

  case object InvalidControlListFormat extends ControlListError

  implicit object GetControlListReads extends HttpReads[GetControlListResponse] {
    override def read(method: String, url: String, response: HttpResponse): GetControlListResponse = {
      response.status match {
        case OK => (response.json \ "controlListInformation").validate[String] match {
          case JsSuccess(controlList, _) => parseControlList(controlList)
          case _ => throw new InternalServerException("Invalid Control List JSON from DES")
        }
        case BAD_REQUEST => throw new InternalServerException("Invalid UTR submitted to DES")
        case NOT_FOUND => Left(ControlListDataNotFound)
        case _ => throw new InternalServerException(s"DES returned the following error code: '${response.status}' and message: '${response.body}'")
      }
    }
  }

  private def parseControlList(controlList: String): GetControlListResponse = {
    val CONTROL_LIST_FALSE = '0'
    val CONTROL_LIST_TRUE = '1'

    if (controlList matches "[0,1]{40}") Right((controlList.zipWithIndex flatMap {
        case (CONTROL_LIST_TRUE, index) => ControlListParameter.getParameterMap.get(index)
        case (CONTROL_LIST_FALSE, _) => None
      }).toSet)
    else Left(InvalidControlListFormat)
  }

}