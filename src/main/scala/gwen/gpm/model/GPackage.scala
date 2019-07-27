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

import scala.language.implicitConversions

/**
  * Enumeration of the various packages that are managed.
  */
object GPackage extends Enumeration {

  type GPackage = Value

  abstract class GPackageValue(val name: String,
                               val archiveType: ArchiveType.Value,
                               val excludeTopDir: Boolean) extends Val(name) {
    def fetchLatestVersion: String
    def getDownloadUrl(version: String): String
  }

  implicit def toGPackageValue(pkg: Value): GPackageValue = pkg.asInstanceOf[GPackageValue]

  val gwen_web =
    new GPackageValue("gwen-web", ArchiveType.Zip, excludeTopDir = true) {
      def fetchLatestVersion: String =
        Repository.GitHub.fetchLatestVersion("https://github.com/gwen-interpreter", "gwen-web")
      def getDownloadUrl(version: String): String =
        s"https://github.com/gwen-interpreter/gwen-web/releases/download/v$version/gwen-web-$version.zip"
  }

  val selenium =
    new GPackageValue("selenium", ArchiveType.Zip, excludeTopDir = false) {
      def fetchLatestVersion: String =
        Repository.S3.fetchLatestVersion("https://selenium-release.storage.googleapis.com", "selenium-java")
      def getDownloadUrl(version: String): String =
        s"http://selenium-release.storage.googleapis.com/${majorMinorOf(version)}/selenium-java-$version.zip"
  }

  private def majorMinorOf(version: String) =
    if (version.count(_ == '.') == 2) version.substring(0, version.lastIndexOf('.'))
    else version
}