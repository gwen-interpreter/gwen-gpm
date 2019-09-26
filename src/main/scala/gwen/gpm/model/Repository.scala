/*
 * Copyright 2017 Branko Juric, Brady Wood
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
package gwen.gpm.model

import java.net.URI
import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Element
import gwen.gpm.Errors.latestVersionFetchError

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

/**
  * Enumeration of repository types where packages can be downloaded from.
  */
object Repository extends Enumeration {

  type Repository = Value

  abstract class RepositoryValue(val name: String) extends Val(name) {
    def fetchLatestVersion(baseUrl: String, name: String): String
  }

  implicit def toRepositoryValue(pkg: Value): RepositoryValue = pkg.asInstanceOf[RepositoryValue]

  val GitHub = new RepositoryValue("GitHub") {
    override def fetchLatestVersion(baseUrl: String, name: String): String = {
      val url = s"$baseUrl/$name/releases/latest"
      Try {
        val con = new URI(url).toURL.openConnection
        con.connect()
        con.getInputStream
        val redirectUrl = con.getURL.toString
        redirectUrl.substring(redirectUrl.lastIndexOf("/v") + 2).trim
      } match {
        case Success(version) => version
        case Failure(e) => latestVersionFetchError(url, name, e)
      }
    }
  }

  val S3 = new RepositoryValue("S3") {
    override def fetchLatestVersion(url: String, name: String): String = {
      Try {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val xmlStream = new URI(url).toURL.openStream()
        try {
          val contentsNodes = builder.parse(xmlStream).getElementsByTagName("Contents")
          val versions = for {
            contentsIdx <- 0 until contentsNodes.getLength
            contentsNode = contentsNodes.item(contentsIdx).asInstanceOf[Element]
            keyNodes = contentsNode.getElementsByTagName("Key")
            if keyNodes.getLength == 1
            key = keyNodes.item(0).getTextContent
            if key.contains(name) && !key.toLowerCase.contains("alpha") && !key.toLowerCase.contains("beta")
            lastModifiedNodes = contentsNode.getElementsByTagName("LastModified")
            if lastModifiedNodes.getLength == 1
            lastModified = lastModifiedNodes.item(0).getTextContent
            majorMinor = key.split("\\/")(0)
            archive = key.split("\\/")(1)
            idx = archive.lastIndexOf(majorMinor)
            revision = if (idx != -1) archive.substring(idx + majorMinor.length, archive.lastIndexOf(".zip")) else ""
          } yield (lastModified, s"$majorMinor$revision")
          versions.toList.maxBy(_._1)._2
        } finally {
          xmlStream.close()
        }
      } match {
        case Success(version) => version
        case Failure(e) => latestVersionFetchError(url, name, e)
      }
    }
  }

}
