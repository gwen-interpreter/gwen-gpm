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

import scala.collection.JavaConverters._
import java.io.File
import java.util.Properties
import java.io.FileReader
import java.net.URI

import gwen.gpm.Errors.invalidPropertyError
import gwen.gpm.Errors.missingPropertyError
import gwen.gpm.process.FileIO.FileExtensions

/**
  * Provides access to system properties settings defined in gwen properties files.
  * Properties files are loaded in the order provided with the gwen.properties file in the user home directory (if it
  * exists) being loaded last to ensure that it overrides all others. Therefore latter properties always override
  * former ones. Existing system properties in the environment are not overridden by values in properties files
  * (and therefore cannot be overridden).
  *
  * @param propsFiles the properties files to load (in the order provided, latter props will override former)
  */
class GPMSettings(propsFiles: List[File]) {

  private val InlineProperty = """.*\$\{(.+?)\}.*""".r
  private val checksumFile = new File(new File(s"${FileIO.userHomeDir.getPath}/.gwen"), "gwen-checksums.properties")

  private val defaultOverrides = List(
    new File("gwen-checksums.properties"),
    new File("gwen-gpm.properties"),
    new File(FileIO.userHomeDir, "gwen-gpm.properties")
  )

  loadAll()

  /**
    * Loads all properties from the given files.
    *
    */
  private def loadAll(): Unit = {

    // load all properties files (ensuring that default overrides are loaded last)
    val propsFilesToLoad = ((propsFiles filter { f =>
      f.exists() && !defaultOverrides.exists(_.getCanonicalPath == f.getCanonicalPath)
    }).distinct ++ defaultOverrides).filter(_.exists())

    val props = propsFilesToLoad.foldLeft(new Properties()) {
      (props, file) =>
        props.load(new FileReader(file))
        props.entrySet().asScala foreach { entry =>
          val key = entry.getKey.asInstanceOf[String]
          if (key == null || key.trim.isEmpty) invalidPropertyError(entry.toString, file)
        }
        props
    }
    val existingSysProps = sys.props.keySet
    props.entrySet().asScala.foreach { entry =>
      val key = entry.getKey.asInstanceOf[String]
      if (!existingSysProps.contains(key)) {
        val value = resolve(props.getProperty(key), props)
        sys.props += ((key, value))
      }
    }
  }

  def loadChecksums(download: Boolean): Option[Map[String, String]] = {
    if (!checksumFile.getParentFile.exists()) checksumFile.getParentFile.mkdir()
    if (download) {
      if (checksumFile.exists()) checksumFile.delete()
      val checksumFileUrl = new URI("https://raw.githubusercontent.com/gwen-interpreter/gwen-gpm/master/gwen-checksums.properties").toURL
      checksumFile.download(checksumFileUrl, this, withProgressBar = false)
    }
    if (checksumFile.exists()) {
      val checksums = new Properties()
      checksums.load(new FileReader(checksumFile))
      Some(checksums.asScala.toMap)
    } else {
      None
    }
  }

  /**
   * Resolves a given property by performing any property substitutions.
   *
   * @param value the value to resolve
   * @param props the properties already read (candidates for substitution)
   */
  private[gwen] def resolve(value: String, props: Properties): String = value match {
    case InlineProperty(key) =>
      val inline = if (props.containsKey(key)) {
        props.getProperty(key)
      } else {
        sys.props.get(key).getOrElse(missingPropertyError(key))
      }
      val resolved = inline match {
        case InlineProperty(_) => resolve(inline, props)
        case _ => inline
      }
      resolve(value.replaceAll("""\$\{""" + key + """\}""", resolved), props)
    case _ => value
  }

  /**
    * Gets an optional property (returns None if not found)
    *
    * @param name the name of the property to get
    */
  def getOpt(name: String): Option[String] = sys.props.get(name)

  /**
    * Gets a mandatory property (throws exception if not found)
    *
    * @param name the name of the property to get
    */
  def get(name: String): String = getOpt(name).getOrElse(missingPropertyError(name))

}
