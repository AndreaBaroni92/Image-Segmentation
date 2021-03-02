package config

import java.io.FileInputStream
import java.util.Properties

object ConfReader {

  val propResource = new FileInputStream("project.properties")

  val properties = new Properties()

  properties.load(propResource)

  def getUrlImage() : String = properties.getProperty("image.url")

  def getNumK() :Int = properties.getProperty("cluster.k").toInt

  def getImageOut() :String = properties.getProperty("image.out")

  def getClusterOut():String = properties.getProperty("cluster.out")

  def getDimOut():String = properties.getProperty("dim.out")

  def getPath() :String = properties.getProperty("path.out")

}
