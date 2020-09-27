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
package gwen.gpm.process

import java.io.File

import scopt.OptionParser

import scala.util.{Failure, Success, Try}
import gwen.gpm.model.{GPackage, Operation}

/**
  * Holds all GwenGPM user options (parsed from command line args).
  *
  * @param operation the operation to perform
  * @param pkg the package
  * @param version the package version
  * @param destination the destination folder to install to (optional)
  * @param properties optional list of properties files to load
  */
case class GPMOptions(operation: Operation.Value = Operation.install,
                      pkg: GPackage.Value = GPackage.gwen_web,
                      version: String = "<version>",
                      destination: Option[File] = None,
                      properties: List[File] = Nil)

/**
  * Companion for loading user options (command line arguments).
  */
object GPMOptions {

  /**
    * Creates a copy of an options object with the given version.
    *
    * @param options the options to copy
    * @param version the version to set
    * @return the application options
    */
  def apply(options: GPMOptions, version: String): GPMOptions = {
    GPMOptions(
      options.operation,
      options.pkg,
      version,
      options.destination,
      options.properties)
  }

  /**
    * Accepts the user provided command line arguments and returns them in an application options object.
    *
    * @param args the command line arguments
    * @return the application options
    */
  def apply(args: Array[String]): Option[GPMOptions] = {

    // use scopt parser to read in arguments and return the application options
    val parser = new OptionParser[GPMOptions]("gwen-gpm") {

      help("help") text "prints this usage text"

      opt[String]('p', "properties") action {
        (ps, c) =>
          c.copy(properties = ps.split(",").toList.map(new File(_)))
      } validate { ps =>
        ((ps.split(",") flatMap { p =>
          if (new File(p).exists()) None
          else Some(s"Specified properties file not found: $p")
        }) collectFirst {
          case error => failure(error)
        }).getOrElse(success)
      } valueName "<files>" text "Comma separated list of properties files"

      arg[String]("<operation>").required().validate { o =>
        Try(Operation.withName(o)) match {
          case Success(_) => success
          case Failure(e) =>
            failure(s"Invalid operation '$o' specified: valid values include ${Operation.values.mkString(",")}")
        }
      } action { (o, options) =>
        options.copy(operation = Operation.withName(o))
      } text Operation.values.mkString(" | ")

      arg[String]("<package>").required().validate { p =>
        Try(GPackage.withName(p)) match {
          case Success(_) => success
          case Failure(_) =>
            failure(s"Invalid package '$p' specified, valid values include ${GPackage.values.mkString(",")}")
        }
      } action { (p, options) =>
        options.copy(pkg = GPackage.withName(p))
      } text GPackage.values.mkString(" | ")

      arg[String]("<version>").required().action { (v, options) =>
        options.copy(version = v)
      } text "latest | version property | version number"

      arg[File]("<destination>").optional().action { (d, options) =>
        options.copy(destination = Some(d))
      } text
        """the destination folder to install the package to
          |                           - if not specified, defaults to ~/.gwen/package/<package>""".stripMargin

    }

    parser.parse(args, GPMOptions())

  }

}