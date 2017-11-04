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

import java.text.DecimalFormat

import scala.concurrent.duration.Duration

/**
  * Progress bar for displaying download status.
  *
  * @param totalUnits the number of progress units
  */
class ProgressBar(barLength: Int, totalUnits: Long) {

  private var unitsDone = 0L
  private val startTime = System.nanoTime()

  show()

  /** Steps the progress bar by 1 unit */
  def step(): Unit = {
    stepBy(1)
  }

  /**
    * Steps the progress bar by a given number of units.
    * @param units the number of units to step
    */
   def stepBy(units: Long): Unit = {
    unitsDone += units
    show()
  }

  private def show(): Unit = {
    val percentage = unitsDone.toDouble / totalUnits.toDouble
    val barUnits = (percentage * barLength).toInt
    val bar = s"${"=" * (barUnits - 1)}>${"-" * (barLength - barUnits)}"
    val duration = DurationFormatter.format(Duration.fromNanos(System.nanoTime() - startTime))
    print(s"\r[gwen-gpm] [$bar] ${(percentage * 100).toInt}% $duration ")
    if (barUnits == barLength) {
      Thread.sleep(100)
      print(s"\r[gwen-gpm] [${"=" * barLength}] 100% $duration ")
    }
  }
}

/**
  * Formats durations for presentation purposes.
  */
object DurationFormatter {

  import scala.concurrent.duration._

  private val Formatters = List(
    HOURS -> ("h", new DecimalFormat("00")),
    MINUTES -> ("m", new DecimalFormat("00")),
    SECONDS -> ("s", new DecimalFormat("00"))
  )

  /**
    * Formats a given duration to ##h ##m ##s ###ms format.
    *
    * @param duration the duration to format
    */
  def format(duration: Duration): String = {
    val nanos = duration.toNanos
    val secs = (nanos / 1000000000) + (if ((nanos % 1000000000) < 500000000) 0 else 1)
    if (secs > 0) {
      var duration = Duration(secs, SECONDS)
      Formatters.foldLeft("") { (acc: String, f: (TimeUnit, (String, DecimalFormat))) =>
        val (unit, (unitName, formatter)) = f
        val unitValue = duration.toUnit(unit).toLong
        if (acc.length() == 0 && unitValue == 0) ""
        else {
          duration = duration - Duration(unitValue, unit)
          s"$acc ${formatter.format(unitValue)}$unitName"
        }
      }.trim.replaceFirst("^0+(?!$)", "")
    } else "0s"
  }

}
