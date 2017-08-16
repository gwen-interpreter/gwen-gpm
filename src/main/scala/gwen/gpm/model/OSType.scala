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

import java.util.Locale
import gwen.gpm.Errors.unknownHostOSType

/**
  * Enumeration of operating system types.
  */
object OSType extends Enumeration {

  type OSType = Value

  val Linux, Mac, Win = Value

  def determine(): OSType.Value = {
    val os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH)
    if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
      OSType.Mac
    } else if (os.indexOf("win") >= 0) {
      OSType.Win
    } else if (os.indexOf("nux") >= 0) {
      OSType.Linux
    } else unknownHostOSType()
  }

  def architecture: Int = System.getProperty("sun.arch.data.model", "64").toInt

}
