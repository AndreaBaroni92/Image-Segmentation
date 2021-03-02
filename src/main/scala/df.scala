import java.io.FileInputStream
import java.util.Properties
object df {
  def main(args: Array[String]): Unit = {

    val propResource = new FileInputStream("project.properties")

    val properties = new Properties()

    properties.load(propResource)

    val k:Int = properties.getProperty("num").toInt

    println(s" intero + 1 = ${k + 1}")




  }
}
