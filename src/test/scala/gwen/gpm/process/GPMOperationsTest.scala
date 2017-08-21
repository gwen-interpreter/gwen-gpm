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

import gwen.gpm.model.{GPackage, Operation}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by branko on 30/7/17.
  */
class GPMOperationsTest extends FlatSpec with Matchers {

  private val rootDir = new File(s"${FileIO.userHomeDir.getPath}/.gwen")
  private val lockFile = new File(rootDir, ".lock")
  private val settings = new GPMSettings(Nil)

  "gwen-web package" should "install" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"${rootDir.getPath}/package/gwen-web")
    val options = GPMOptions(Operation.install, GPackage.gwen_web, "latest", None)
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "gwen-web").get should exist
  }

  "gwen-web package" should "install into external dir" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"target/gwen-package/gwen-web")
    val options = GPMOptions(Operation.install, GPackage.gwen_web, "latest", Some(destDir))
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "gwen-web").get should exist
  }

  "gwen-web package" should "update" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"${rootDir.getPath}/package/gwen-web")
    val options = GPMOptions(Operation.update, GPackage.gwen_web, "latest", None)
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "gwen-web").get should exist
  }

  "gwen-web package" should "update into external dir" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"target/gwen-package/gwen-web")
    val options = GPMOptions(Operation.update, GPackage.gwen_web, "latest", Some(destDir))
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "gwen-web").get should exist
  }

  "chrome-driver package" should "install" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"${rootDir.getPath}/package/chrome-driver")
    val options = GPMOptions(Operation.install, GPackage.chrome_driver, "latest", None)
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "chrome-driver").get should exist
  }

  "chrome-driver package" should "install in external dir" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"target/gwen-package/chrome-driver")
    val options = GPMOptions(Operation.install, GPackage.chrome_driver, "latest", Some(destDir))
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "chrome-driver").get should exist
  }

  "chrome-driver package" should "update" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"${rootDir.getPath}/package/chrome-driver")
    val options = GPMOptions(Operation.update, GPackage.chrome_driver, "latest", None)
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "chrome-driver").get should exist
  }

  "chrome-driver package" should "update in external dir" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"target/gwen-package/chrome-driver")
    val options = GPMOptions(Operation.update, GPackage.chrome_driver, "latest", Some(destDir))
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "chrome-driver").get should exist
  }

  "gecko-driver package" should "install" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"${rootDir.getPath}/package/gecko-driver")
    val options = GPMOptions(Operation.install, GPackage.gecko_driver, "latest", None)
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "gecko-driver").get should exist
  }

  "gecko-driver package" should "install in external dir" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"target/gwen-package/gecko-driver")
    val options = GPMOptions(Operation.install, GPackage.gecko_driver, "latest", Some(destDir))
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "gecko-driver").get should exist
  }

  "gecko-driver package" should "update" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"${rootDir.getPath}/package/gecko-driver")
    val options = GPMOptions(Operation.update, GPackage.gecko_driver, "latest", None)
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "gecko-driver").get should exist
  }

  "gecko-driver package" should "update in external dir" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"target/gwen-package/gecko-driver")
    val options = GPMOptions(Operation.update, GPackage.gecko_driver, "latest", Some(destDir))
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "gecko-driver").get should exist
  }

  "selenium package" should "install" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"${rootDir.getPath}/package/selenium")
    val options = GPMOptions(Operation.install, GPackage.selenium, "latest", None)
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "selenium").get should exist
  }

   "selenium package" should "install in external dir" in {
     if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"target/gwen-package/selenium")
    val options = GPMOptions(Operation.install, GPackage.selenium, "latest", Some(destDir))
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "selenium").get should exist
  }

  "selenium package" should "update" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"${rootDir.getPath}/package/selenium")
    val options = GPMOptions(Operation.update, GPackage.selenium, "latest", None)
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "selenium").get should exist
  }

  "selenium package" should "update in external dir" in {
    if (lockFile.exists()) lockFile.delete()
    val destDir = new File(s"target/gwen-package/selenium")
    val options = GPMOptions(Operation.update, GPackage.selenium, "latest", Some(destDir))
    new GPMOperations(options, settings).install().getPath should be (destDir.getPath)
    destDir should exist
    destDir.isDirectory should be (true)
    new File(destDir, ".gwen").listFiles().find(_.getName startsWith "selenium").get should exist
  }

}
