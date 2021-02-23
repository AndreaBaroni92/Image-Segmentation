package Image


import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{Dataset, SparkSession}

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
case class A(x:Int)
object ImageSegmentation {
  def writeImage(width:Int,
                 height:Int,
                 img:Array[(Int,Int,Int,Int,Int)]): BufferedImage = { // copia l'immagine in input

    val out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)

    for (p <- img) {
      out.setRGB(p._1, p._2, new Color(p._5,p._4,p._3).getRGB)
    }

    out

  }


  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("schema")
      .getOrCreate()
    import spark.implicits._
    val image_df = spark.read.format("image").load("src/main/scala/iris.jpg")


    //image_df.show(6)

    val width=  image_df.select(col("image.width")).withColumnRenamed("width","x").as[A].first()

    val height=  image_df.select(col("image.height")).as[Int].first()

    val k = width.x
    println(s"Larghezza = ${width.x} altezza = $height")
    val data = image_df.select("image.data").as[Array[Byte]]

    val j = data.collect()(0)
    println(j.take(10).mkString("Array(", ", ", ")"))
    val blue = data.flatMap(x => x.zipWithIndex.map(a => (a._1 & 0xff,a._2 % 3,a._2))).filter(x => x._2 == 0).drop("_2")
      .toDF("blue","id")
    val green = data.flatMap(x => x.zipWithIndex.map(a => (a._1 & 0xff ,a._2 % 3,a._2))).filter(x => x._2 == 1).drop("_2")
      .toDF("green","id")
    val red = data.flatMap(x => x.zipWithIndex.map(a => (a._1 & 0xff,a._2 % 3,a._2))).filter(x => x._2 == 2).drop("_2")
      .toDF("red","id")
    // green.show(5)

    val green_updated = green.withColumn("id",col("id") - 1)
    val red_updated = red.withColumn("id",col("id") - 2)
    val int3:Dataset[((Int,Int,Int,Int))] = blue.join(green_updated,"id").join(red_updated , "id")
      .withColumn("id",(col("id") / 3).cast("int")).as[(Int,Int,Int,Int)]
    //val int3 = blue.join(green_updated,"id").withColumn("id",(col("id") / 3).cast("int")).as[(Int,Int,Int)]

    val ris:Dataset[(Int,Int,Int,Int,Int)] =  int3.map(x =>(x._1 % k ,(x._1 / k.toDouble).toInt,x._2,x._3,x._4 )) // aggiunto le coordinate asse x
      .toDF("width","height","blue","green","red")
      .as[(Int,Int,Int,Int,Int)]

    //ris.show(14)
    val imageToPrint:Array[(Int,Int,Int,Int,Int)] = ris.collect()


    println(s" Lunghezza dell' array = ${imageToPrint.size}")
    val stampa  = writeImage(k,height,imageToPrint)

    ImageIO.write(stampa, "jpg", new File("src/main/scala/iris_nuova.jpg"))



  }
}
