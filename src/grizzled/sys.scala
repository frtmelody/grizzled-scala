package grizzled

import scala.util.matching.Regex

import java.io.File

object sys
{
    /**
     * Indicator of current operating system.
     *
     * <ul>
     *   <li>VMS - OpenVMS
     *   <li>Windows - Microsoft Windows, other than Windows CE
     *   <li>WindowsCE - Microsoft Windows CE
     *   <li>OS2 - OS2
     *   <li>NetWare - NetWare
     *   <li>Mac - Mac OS, prior to Mac OS X
     *   <li>Posix - Anything Unix-like, including Mac OS X
     * </ul>
     */
    object OperatingSystem extends Enumeration
    {
    	val Posix = Value("Posix")
        val Mac = Value("Mac OS")
        val Windows = Value("Windows")
        val WindowsCE = Value("Windows CE")
        val OS2 = Value("OS/2")
        val NetWare = Value("NetWare")
        val VMS = Value("VMS")
    }

    import OperatingSystem._

    /**
     * Indicator of the current operating system, as defined by the
     * <tt>OperatingSystem</tt> enumeration. These need to be functions,
     * not vals, so they can be overridden at runtime by the tests.
     */
    def grizzledOSName = System.getProperty("grizzled.os.name")

    def osName =
        if ((grizzledOSName != null) && (grizzledOSName != ""))
            grizzledOSName
        else
            System.getProperty("os.name")

    def os = osName.toLowerCase match
    {
        case "mac"        => Mac
        case "windows ce" => WindowsCE
        case "windows"    => Windows
        case "os/2"       => OS2
        case "netware"    => NetWare
        case "openvms"    => VMS
        case _            => Posix
    }

    /**
     * Get the Java system properties as a Scala iterable. The iterable
     * will produce a (name, value) tuple.
     *
     * @return the system properties as an iterable
     */
    def systemProperties: Iterable[(String, String)] =
    {
        import scala.collection.jcl.Hashtable

        // Need to wrap the standard Java system properties in something
        // Scala's for loop can grok.

        for ((k, v) <- new Hashtable(System.getProperties))
            yield (k.toString, v.toString)
    }
}
