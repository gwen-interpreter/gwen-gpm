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
package gwen.gpm

import gwen.gpm.model.{GPackage, Operation}
import gwen.gpm.process._

/**
  * A package manager that downloads and installs Gwen and other external packages into the `.gwen` folder of the
  * user's home directory.
  *
  * Created by branko on 27/7/17.
  */
object GwenGPM extends App {

  lazy val implVersion: String = Option(this.getClass.getPackage.getImplementationVersion).getOrElse("-SNAPSHOT")

  printBanner(args)

  // load user arguments and run the app
  val exitCode = GPMOptions(args).map(run).getOrElse(1)
  System.exit(exitCode)

  private def run(options: GPMOptions): Int = {
    try {
      val settings = new GPMSettings(options.properties)
      val version = if (options.pkg == GPackage.selenium) {
        settings.getOpt(options.version).getOrElse(options.version)
      } else {
        options.version
      }
      if (version != "provided") {
        val operations = new GPMOperations(options, settings)
        options.operation match {
          case Operation.install => operations.install()
          case Operation.update => operations.install()
          case _ => None
        }
        0 // success
      }
      else {
        println(s"[gwen-gpm] ${options.pkg} is provided by Gwen")
        -1 // noop
      }
    } catch {
      case e: Throwable =>
        if (!e.isInstanceOf[GPMException]) e.printStackTrace()
        println(s"[gwen-gpm] Failed with error: ${e.getMessage}")
        1 // error
    }
  }

  private def printBanner(args: Array[String]) {
    println(("""|   _
                |  { \," Gwen Package Manager
                | {_`/   gwen-gpm v""" + implVersion + """
                |   `    """).stripMargin)
    println(s"[gwen-gpm] ${args.mkString(" ")}")
  }

}




