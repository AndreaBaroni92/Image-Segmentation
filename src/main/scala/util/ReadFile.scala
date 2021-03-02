package util
import java.io.File
import scala.io.Source
object ReadFile{

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter( x => {x.isFile && x.getName.endsWith(".csv")}).toList
    } else {
      List[File]()
    }
  }


  def getData(name:String):Array[(Int,Int,Int)] = {
    val l = getListOfFiles(name)
    val sorted = l.sortBy(x => x.getName.split("-").apply(1).toInt)

    val ris = (for {
     f <- sorted
     source = Source.fromFile(f)
     line <- source.getLines()
     tup = line.split(",")
     risult = (tup(0).toInt,tup(1).toInt,tup(2).toInt)
   } yield risult).toArray
    ris
  }

  def getCluster(name:String):Array[Array[Int]] = {
    val l = getListOfFiles(name)
    val sorted = l.sortBy(x => x.getName.split("-").apply(1).toInt)

    val ris = (for {
      f <- sorted
      source = Source.fromFile(f)
      line <- source.getLines()
      tup = line.split(",").map( x => x.toInt)
    } yield tup).toArray
    ris
  }

}
