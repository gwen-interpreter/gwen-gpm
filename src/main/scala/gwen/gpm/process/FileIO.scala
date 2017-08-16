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

import java.io._
import java.net.{HttpURLConnection, URL}
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.{ZipEntry, ZipInputStream, ZipOutputStream}
import javax.xml.bind.DatatypeConverter

import gwen.gpm.Errors
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.utils.IOUtils

import scala.util.Try

/**
  * Provides convenience functions for file I/O.
  */
object FileIO {

  /** The user home directory. */
  val userHomeDir: File = new File(System.getProperty("user.home").replace("\\", "/"))

  /**
    * Extension methods for the File class. Any file instances that are in the scope of this implicit class will
    * inherit the methods defined here.
    *
    * @param file the file object instance to extend
    */
  implicit class FileExtensions[F <: File](val file: F) extends AnyVal {

    /** Returns the path string of this file with the user home path (if it exists) replaced with '~'. */
    def maskUserHomeDir: String = file.getPath.replace("\\", "/").replace(userHomeDir.getPath, "~")

    /**
      * Writes the provided text to the calling file.
      *
      * @param text the text to write
      * @return the file the text was written to (calling file)
      */
    def writeText(text: String): File = {
      if (file.getParentFile != null && !file.getParentFile.exists()) {
        file.getParentFile.mkdirs()
      }
      val fw = new FileWriter(file)
      try {
        fw.write(text)
      } finally {
        fw.close()
      }
      file
    }

    /**
      * Moves all the files in the directory represented by this file instance to another directory.
      *
      * @param toDir the directory to move the files to
      */
    def moveFiles(toDir: File) {
      if (file.isDirectory) {
        file.listFiles() foreach { f =>
          if (f.isDirectory) {
            val dir = new File(toDir, f.getName)
            dir.mkdir()
            f.moveFiles(dir)
            f.delete()
          } else {
            f.renameTo(new File(toDir, f.getName))
          }
        }
      }
    }

    /**
      * Downloads the contents of the given URL to the file represented by this instance.
      *
      * @param url the URL to the contents to download
      * @return a tuple containing the downloaded file and the SHA-256 hash sum (hex digest) of the contents
      */
    def download(url: URL): (File, String) = {
      val httpConn = url.openConnection().asInstanceOf[HttpURLConnection]
      val contentLength = httpConn.getContentLengthLong
      val in = new BufferedInputStream(httpConn.getInputStream)
      val digest = MessageDigest.getInstance("SHA-256")
      val blockSize = 4096
      try {
        val progressBar = new ProgressBar(28, contentLength)
        val out = new BufferedOutputStream(new FileOutputStream(file), blockSize)
        try {
          val block = new Array[Byte](blockSize)
          var length = in.read(block, 0, blockSize)
          while (length >= 0) {
            progressBar.stepBy(length)
            out.write(block, 0, length)
            digest.update(block, 0, length)
            length = in.read(block, 0, blockSize)
          }
        } finally {
          println()
          out.close()
        }
      } finally {
        in.close()
      }
      (file, DatatypeConverter.printHexBinary(digest.digest()).toLowerCase)
    }

    /**
      * Calculates the SHA-256 checksum of the file represented by this instance.
      *
      * @return the SHA-256 hash sum (hex digest) of this file
      */
    def checksum: String = {
      val in = new BufferedInputStream(new FileInputStream(file))
      val digest = MessageDigest.getInstance("SHA-256")
      val blockSize = 4096
      try {
        val block = new Array[Byte](blockSize)
        var length = in.read(block, 0, blockSize)
        while (length >= 0) {
          digest.update(block, 0, length)
          length = in.read(block, 0, blockSize)
        }
      } finally {
        in.close()
      }
      DatatypeConverter.printHexBinary(digest.digest()).toLowerCase
    }

    /**
      * Unpacks the ZIP archive represented to the calling file instance.
      *
      * @param targetDir the directory to unpack the ZIP to
      * @param excludeTopDir true to exclude containing folder, false otherwise
      * @return the directory where the archive was unpacked to
      */
    def unpackZip(targetDir: File, excludeTopDir: Boolean): File = {
      val in = new ZipInputStream(new FileInputStream(file))
      try {
        var entry = in.getNextEntry
        while (entry != null) {
          extractFile(in, targetDir, entry.getName, excludeTopDir)
          entry = in.getNextEntry
        }
      } finally {
        try {
          in.closeEntry()
        } finally {
          in.close()
        }
      }
      targetDir
    }

    /**
      * Unpacks the TarGz archive represented to the calling file instance.
      *
      * @param targetDir the directory to unpack the ZIP to
      * @param excludeTopDir true to exclude containing folder, false otherwise
      * @return the directory where the archive was unpacked to
      */
    def unpackTarGz(targetDir: File, excludeTopDir: Boolean): File = {
      val in = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(file))))
      try {
        var entry = in.getNextTarEntry
        while (entry != null) {
          extractFile(in, targetDir, entry.getName, excludeTopDir)
          entry = in.getNextTarEntry
        }
      } finally {
        in.close()
      }
      targetDir
    }

    private def extractFile(in: InputStream, targetDir: File, fileEntry: String, excludeTopDir: Boolean) = {
      val filePath = if (excludeTopDir) {
        Try(fileEntry.substring(fileEntry.indexOf("/")).substring(1)) getOrElse {
          Try(fileEntry.substring(fileEntry.indexOf("\\")).substring(1)).getOrElse(fileEntry)
        }
      } else {
        fileEntry
      }
      if (filePath.nonEmpty) {
        val targetFile = new File(s"${targetDir.getPath}/$filePath")
        if (filePath.endsWith("/") || filePath.endsWith("\\")) {
          targetFile.mkdirs()
        } else {
          if (!targetFile.getParentFile.exists) targetFile.getParentFile.mkdirs()
          if (!targetFile.exists) targetFile.createNewFile()
          val fos = new FileOutputStream(targetFile)
          try {
            IOUtils.copy(in, fos)
          } finally {
            fos.close()
          }
        }
      }
    }

    /**
      * Backs up and zips the directory represented by the calling file. The directory is backed up into a timestamped
      * zip file in the given directory
      *
      * @param toDir the directory to create the backup zip to
      * @param existingPkg the existing package name (with version)
      *
      * @return Some(zip file) if it was successfully backed up or None if the calling file is not a directory
      */
    def createBackupZip(toDir: File, existingPkg: String): Option[File] = {

      // for deleting backup dir after successful zip
      def deleteDir(dir: File) {
        val fs = dir.listFiles()
        if (fs != null) {
          fs foreach { f => if (f.isDirectory) deleteDir(f) else f.delete() }
        }
        dir.delete()
      }

      if (file.isDirectory) {
        val timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
        val backupDir = new File(s"${toDir.getPath}/$existingPkg-bak-$timestamp")
        if (!backupDir.getParentFile.exists()) backupDir.getParentFile.mkdirs()
        println(s"[gwen-gpm] Backing up existing installation to: ${backupDir.maskUserHomeDir}.zip")
        if (file.renameTo(backupDir)) {
          val zipFile = backupDir.zipDir()
          if (zipFile.nonEmpty) {
            deleteDir(backupDir)
          }
          zipFile
        }
        else Errors.cannotBackupOutputDir(file)
      } else {
        None
      }

    }

    /**
      * Zips the directory represented by this file instance.
      *
      * @return the zip file if this file instance is a directory or None otherwise
      */
    def zipDir(): Option[File] = {
      def zipDir(rootDir: File, sourceDir: File, out: ZipOutputStream): Unit = {
        Option(sourceDir.listFiles()) match {
          case Some(files) =>
            files foreach { f =>
              if (f.isDirectory) {
                zipDir(rootDir, new File(sourceDir, f.getName), out)
              }
              else {
                val entry = new ZipEntry(new File(sourceDir.getPath.replace(rootDir.getPath, ""), f.getName).getPath)
                out.putNextEntry(entry)
                val in = new FileInputStream(new File(sourceDir, f.getName))
                IOUtils.copy(in, out)
                IOUtils.closeQuietly(in)
              }
            }
          case None => // noop
        }
      }
      if (file.isDirectory) {
        val outFile = new File(file.getParentFile, s"${file.getName}.zip")
        val zipFile = new ZipOutputStream(new FileOutputStream(outFile))
        zipDir(file, file, zipFile)
        IOUtils.closeQuietly(zipFile)
        Some(outFile)
      } else {
        None
      }
    }

  }

}

