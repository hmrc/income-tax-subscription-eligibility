/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.{JsSuccess, JsValue}
import uk.gov.hmrc.http.{HttpReads, HttpResponse, InternalServerException}
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.PrepopData
import uk.gov.hmrc.incometaxsubscriptioneligibility.models.controllist.{ControlListParameter, ControlListResult}

object GetControlListHttpParser {

  type GetControlListResponse = Either[ControlListError, GetControlListSuccess]

  sealed trait GetControlListSuccess

  case class GetControlListSuccessResponse(controlList: Set[ControlListParameter], prepopData: Option[PrepopData] = None) extends GetControlListSuccess

  sealed trait ControlListError extends ControlListResult

  case object ControlListDataNotFound extends ControlListError {
    val errorMessage: String = "No control list data for specified UTR"
  }

  case object InvalidControlListFormat extends ControlListError {
    val errorMessage: String = "Incorrectly formatted control list"
  }

  implicit object GetControlListReads extends HttpReads[GetControlListResponse] {
    override def read(method: String, url: String, response: HttpResponse): GetControlListResponse = {
      response.status match {
        case OK => readGetControlList(response.json)
        case BAD_REQUEST => throw new InternalServerException(s"Invalid UTR submitted to DES, message: '${response.body}'")
        case NOT_FOUND => Left(ControlListDataNotFound)
        case _ => throw new InternalServerException(s"DES returned the following error code: '${response.status}' and message: '${response.body}'")
      }
    }
  }

  private def readGetControlList(json: JsValue): Either[ControlListError, GetControlListSuccessResponse] = {
    for {
      controlList <- extractControlList(json)
      prepopData <- Right(extractPrepopData(json))
    } yield (GetControlListSuccessResponse(controlList, prepopData))
  }

  private def extractControlList(json: JsValue): Either[ControlListError, Set[ControlListParameter]] = {
    (json \ "controlListInformation").validate[String] match {
      case JsSuccess(controlList, _) => parseControlList(controlList)
      case _ => throw new InternalServerException("Invalid Control List JSON from DES")
    }
  }

  private def extractPrepopData(json: JsValue) = {
    (json \ "prepopData").asOpt[PrepopData]
  }

  private def parseControlList(controlList: String): Either[ControlListError, Set[ControlListParameter]] = {
    val CONTROL_LIST_FALSE = '0'
    val CONTROL_LIST_TRUE = '1'

    if (controlList matches "[0,1]{40}") Right((controlList.zipWithIndex flatMap {
      case (CONTROL_LIST_TRUE, index) => ControlListParameter.getParameterMap.get(index)
      case (CONTROL_LIST_FALSE, _) => None
      case _ => None
    }).toSet)
    else Left(InvalidControlListFormat)
  }
}
