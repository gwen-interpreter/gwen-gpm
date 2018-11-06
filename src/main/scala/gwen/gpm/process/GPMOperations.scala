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
import java.net.URI

import gwen.gpm.Errors._
import gwen.gpm.GPMChecksumNotConfiguredException
import gwen.gpm.model.{GPackage, OSType, Operation}
import gwen.gpm.process.FileIO.FileExtensions

import scala.io.Source
import scala.sys.process._
import scala.util.{Failure, Try}

/**
  * Performs all Gwen package management operations.
  *
  * @param options the command line options
  * @param settings the property settings
  */
class GPMOperations(options: GPMOptions, settings: GPMSettings) {

  private val rootDir = new File(s"${FileIO.userHomeDir.getPath}/.gwen")

  private val lockFile = init(options)
  private val cacheDir = new File(s"$rootDir/cache")
  private val packageDir = new File(s"$rootDir/package")

  private val version = resolveVersion(options)
  private val packageId = s"${options.pkg.name}-$version"
  private val checksumKey = s"gwen.checksum.$packageId"

  private val archiveFile = new File(cacheDir, s"${options.pkg.name}/$packageId.${options.pkg.archiveType.fileExtension}")
  private val destinationDir = options.destination.getOrElse(new File(packageDir, options.pkg.name))
  private val installFile = new File(destinationDir, s".gwen/$packageId")

  private def init(options: GPMOptions): File = {

    // create or wait for lock file (only one process at a time allowed to work in .gwen folder)
    val lockFile = new File(s"${rootDir.getPath}/.lock")
    if (lockFile.exists()) {
      println("[gwen-gpm] Waiting for another process to release the ~/.gwen/.lock file (delete it manually to force resume)..")
      while(lockFile.exists()) Thread.sleep(1000)
      println("[gwen-gpm] ..lock file released")
    }
    if (!lockFile.getParentFile.exists()) lockFile.getParentFile.mkdirs()
    lockFile.createNewFile()
    lockFile.deleteOnExit()
    lockFile
  }

  def resolveVersion(options: GPMOptions): String = {
    val targetVersion = settings.getOpt(options.version).getOrElse(options.version)
    println(s"[gwen-gpm] Target ${options.pkg} version is $targetVersion")
    val targetLatest = targetVersion == "latest"
    val latestVersionFile = new File(cacheDir, s"${options.pkg.name}/${options.pkg.name}.latest")
    if (options.operation == Operation.update || options.operation == Operation.download || (targetLatest && !latestVersionFile.exists())) {
      val latestVersion = options.pkg.fetchLatestVersion
      if (targetVersion != latestVersion) {
        println(s"[gwen-gpm] The latest available ${options.pkg} version is $latestVersion")
      }
      if ((targetLatest || targetVersion == latestVersion) && options.pkg.name.contains("gwen")) {
        println(s"[gwen-gpm] You'll be using the latest ${options.pkg} ..Very good!")
      }
      if (!latestVersionFile.getParentFile.exists()) latestVersionFile.getParentFile.mkdirs()
      latestVersionFile.writeText(latestVersion)
      if (targetLatest) latestVersion else targetVersion
    } else if (targetLatest) {
      if (latestVersionFile.exists()) {
        val latestVersion = Source.fromFile(latestVersionFile).mkString.trim
        if (targetVersion != latestVersion) {
          println(s"[gwen-gpm] The latest cached ${options.pkg} version is $latestVersion")
        }
        latestVersion
      } else {
        latestVersionResolveError(options.pkg.name)
      }
    } else {
      targetVersion
    }
  }

  def install(): File = {
    try {

      download()

      // do not install windows packages on non-windows platforms
      if (options.pkg == GPackage.ie_driver && OSType.determine() != OSType.Win) ieDriverAvailableOnWindowsOnly()

      if (destinationDir.exists()) {
        deleteExisting()
      }
      installArchive()
      destinationDir
    } finally {
      lockFile.delete()
    }
  }

  def download(): File = {
    if (!archiveFile.exists()) {
      val downloadUrl = options.pkg.getDownloadUrl(version)
      // report error if no checksum is configured for this package version
      if (settings.getOpt(checksumKey).isEmpty) {
        if (archiveFile.exists()) archiveFile.delete()
        checksumNotConfiguredError(checksumKey, downloadUrl)
      }
      println(s"[gwen-gpm] Downloading $packageId from $downloadUrl")
      if (!archiveFile.getParentFile.exists()) archiveFile.getParentFile.mkdirs()
      val archiveURL = new URI(downloadUrl)
      val (_, fileChecksum) = archiveFile.download(archiveURL.toURL, settings)
      verifyChecksum(options, fileChecksum)
      println(s"[gwen-gpm] Download done")
    }  else {
      println(s"[gwen-gpm] $packageId exists in download cache")
    }
    archiveFile
  }

  private def verifyChecksum(options: GPMOptions, fileChecksum: String) = {
    val validChecksums = settings.getOpt(checksumKey).map(_.split(",").toList).getOrElse {
      archiveFile.delete()
      checksumNotConfiguredError(checksumKey, options.pkg.getDownloadUrl(version))
    }
    if (!validChecksums.exists(_.equalsIgnoreCase(fileChecksum))) {
      archiveFile.delete()
      invalidChecksumError()
    } else {
      println(s"[gwen-gpm] Checksum OK")
    }
  }

  private def installArchive(): Option[File] = {
    if (!destinationDir.exists()) {
      println(s"[gwen-gpm] Installing $packageId to ${destinationDir.maskUserHomeDir}")
      unpackArchive()
      println("[gwen-gpm] Install done")
      Some(destinationDir)
    } else {
      println(s"[gwen-gpm] $packageId already installed in ${destinationDir.maskUserHomeDir}")
      None
    }
  }

  private def unpackArchive() = {

    // verify checksum of archive first (reattempt download on failure)
    Try(verifyChecksum(options, archiveFile.checksum)) match {
      case Failure(e) =>
        if (e.isInstanceOf[GPMChecksumNotConfiguredException]) throw e
        else {
          println(s"[gwen-gpm] ${e.getMessage}")
          println(s"[gwen-gpm] Attempting download one more time")
          download()
        }
      case _ => // OK
    }

    destinationDir.mkdirs()

    // unpack the archive
    if (archiveFile.getName.endsWith(".zip")) archiveFile.unpackZip(destinationDir, options.pkg.excludeTopDir)
    else archiveFile.unpackTarGz(destinationDir, options.pkg.excludeTopDir)

    // move all files up if unpacked contents consist of a single directory having the same name as the package Id
    destinationDir.listFiles().toList match {
      case dir :: Nil if dir.isDirectory && dir.getName == packageId =>
        dir.moveFiles(destinationDir)
        dir.delete()
      case _ => // noop
    }

    // create a package install file in hidden .gwen folder
    if (!installFile.getParentFile.exists()) installFile.getParentFile.mkdirs()
    installFile.createNewFile()

    // set execution permission on non windows platforms
      if (OSType.determine() != OSType.Win) {
        Seq("chmod", "-R", "u+x", destinationDir.getAbsolutePath).!
      }

  }

  private def deleteExisting() = {
    val hiddenGwenDir = new File(destinationDir, ".gwen")
    if (!hiddenGwenDir.exists()) {
      cannotInstallToExternallyManagedDir(packageId, destinationDir)
    } else {
      Option(hiddenGwenDir.listFiles()) match {
        case Some(files) =>
          files.map(_.getName).find(name => GPackage.values.exists(pkg => name.startsWith(pkg.name))) match {
            case Some(existingPkg) =>
              if (!existingPkg.startsWith(options.pkg.name)) {
                cannotOverwriteDifferentPackage(packageId, existingPkg, destinationDir)
              }
              if(!existingPkg.endsWith(version)) {
                println(s"[gwen-gpm] Deleting $existingPkg installation at: ${destinationDir.maskUserHomeDir}")
                destinationDir.deleteDir()
                println("[gwen-gpm] Delete done")
              }
            case None => cannotInstallToTamperedDir(packageId, destinationDir)
          }
        case None => cannotInstallToTamperedDir(packageId, destinationDir)
      }
    }
  }

}


