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

import java.io.File

/**
  * Convenience methods for signalling various types of errors.
  */
object Errors {

  def unknownHostOSType() =
    throw new GPMException("Could not determine host OS type")

  def latestVersionFetchError(url: String, packageName: String, cause: Throwable) =
    throw new GPMException(s"Could not fetch latest $packageName version from URL resource: $url (try again or specify an explicit version instead)")

  def latestVersionResolveError(packageName: String) =
    throw new GPMException(s"Could not resolve latest $packageName version (try again or specify an explicit version instead)")

  def cannotInstallToExternallyManagedDir(targetPkg: String, dir: File) =
    throw new GPMException(s"Cannot install $targetPkg package to existing folder that is externally managed: $dir")

  def cannotInstallToTamperedDir(targetPkg: String, dir: File) =
    throw new GPMException(s"Cannot install $targetPkg package to folder: $dir (the folder is either externally managed or has been tampered with)")

  def cannotOverwriteDifferentPackage(targetPkg: String, existingPkg: String, dir: File) =
    throw new GPMException(s"Cannot install $targetPkg package into folder containing $existingPkg package: $dir")

  def cannotBackupOutputDir(dir: File) =
     throw new GPMException(s"Could not create backup of existing installation: $dir")

  def invalidPropertyError(entry: String, propertyFile: File) =
    throw new GPMException(s"Invalid property entry '$entry' found in file: $propertyFile (name=value expected)")

  def missingPropertyError(name: String) =
    throw new GPMException(s"Property not found: $name")

  def invalidChecksumError() =
    throw new GPMException("Package discarded: calculated checksum did not match expected value")

  def checksumNotConfiguredError(checksumKey: String, downloadUrl: String) =
    throw new GPMChecksumNotConfiguredException(checksumKey, downloadUrl)

  def missingDestinationError() =
    throw new GPMException(s"Destination directory not provided")

  def proxyConfigError() =
    throw new GPMException(s"Invalid proxy configuration: See: https://github.com/gwen-interpreter/gwen-gpm#proxy-connections")

}

/**
  * Thrown when an error or unexpected condition occurs.
  *
  * @param msg the exception message
  *
  */
class GPMException(msg: String, cause: Throwable = null) extends Exception(msg, cause)

/**
  * Thrown when a checksum for a package version is not configured.
  *
  * @param checksumKey the checksum property key
  * @param downloadUrl the package download URL
  *
  */
class GPMChecksumNotConfiguredException(checksumKey: String, downloadUrl: String)
  extends GPMException(s"Checksum not configured for package at: $downloadUrl (package is unknown)")

