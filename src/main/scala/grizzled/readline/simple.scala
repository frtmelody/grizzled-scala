/*
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2009 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2009 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "The Grizzled Scala Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M. Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  ---------------------------------------------------------------------------
*/

/**
 * Simple, pure Scala implementation of the traits defined in the base
 * readline package.
 */
package grizzled.readline.simple

import grizzled.readline._

/**
 * Simple history implementation.
 */
private[simple] class SimpleHistory extends History
{
    import scala.collection.mutable.ArrayBuffer

    private val history = new ArrayBuffer[String]
    private var maxSize = Integer.MAX_VALUE

    /**
     * Get the contents of the history buffer, in a list.
     *
     * @return the history lines
     */
    def get: List[String] = history.toList

    /**
     * Clear the history buffer
     */
    def clear = history.clear

    /**
     * Get the last (i.e., most recent) entry from the buffer.
     *
     * @return the most recent entry, as an <tt>Option</tt>, or
     *         <tt>None</tt> if the history buffer is empty
     */
    def last: Option[String] =
    {
        history.length match
        {
            case 0 => None
            case _ => Some(history.last)
        }
    }

    /**
     * Get the current number of entries in the history buffer.
     *
     * @return the size of the history buffer
     */
    def size: Int = history.length

    /**
     * Get maximum history size.
     *
     * @return the current max history size, or 0 for unlimited.
     */
    def max: Int = maxSize

    /**
     * Set maximum history size.
     *
     * @param newSize the new max history size, or 0 for unlimited.
     */
    def max_=(newSize: Int)
    {
        maxSize = newSize
        ensureMaxSize
    }

    /**
     * Unconditionally appends the specified line to the history.
     *
     * @param line  the line to add
     */
    protected def append(line: String) =
    {
        history += line
        ensureMaxSize
    }

    private def ensureMaxSize: Unit =
    {
        if (history.length > maxSize)
        {
            // Must convert the newHistory variable to a list, because it's
            // a projection into the real buffer, and it'll go away when the
            // buffer is cleared.
            val newHistory = history.drop(history.length - maxSize).toList
            history.clear
            history.insertAll(0, newHistory)
        }
    }
}

/**
 * Simple implementation of the Readline trait.
 */
private[readline] class SimpleImpl(appName: String,
                                   val autoAddHistory: Boolean)
    extends Readline
{
    import java.io.{InputStreamReader, LineNumberReader}

    val name = "Pure Java"
    val history = new SimpleHistory
    val input = new LineNumberReader(new InputStreamReader(System.in))

    private[readline] def doReadline(prompt: String): Option[String] =
    {
        try
        {
            print(prompt)
            val s = input.readLine
            if (s == null)
                None
            else
                Some(s)
        }

        catch
        {
            case e: java.io.EOFException => None
        }
    }
}