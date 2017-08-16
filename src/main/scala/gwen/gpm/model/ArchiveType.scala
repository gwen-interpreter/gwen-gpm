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
  * Enumeration of archive types.
  */
object ArchiveType extends Enumeration {

  type ArchiveType = Value

  class ArchiveTypeValue(val fileExtension: String) extends Val

  implicit def toArchiveTypeValue(pkg: Value): ArchiveTypeValue = pkg.asInstanceOf[ArchiveTypeValue]

  val Zip = new ArchiveTypeValue("zip")
  val TarGz = new ArchiveTypeValue("tar.gz")

}
