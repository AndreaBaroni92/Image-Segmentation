package Image


import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import java.awt.Color
import java.awt.image.BufferedImage


object ImageSegmentation {

  def writeImage(width: Int,
                 height: Int,
                 img: Array[(Int, Int, Int, Int, Int)]): BufferedImage = { // copia l'immagine in input

    val out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)

    for (p <- img) {
      out.setRGB(p._1, p._2, new Color(p._5, p._4, p._3).getRGB)
    }

    out

  }


  def ImagetoPrint(width: Int,
                   height: Int,
                   imageToPrint: Array[(Int, Int, Int)],
                   clusters: Array[Array[Int]]
                  ): BufferedImage = {


    val out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)

    for (p <- imageToPrint) {
      out.setRGB(p._1, p._2, new Color(clusters(p._3)(2), clusters(p._3)(1), clusters(p._3)(0)).getRGB)
    }

    out

  }

  def ImageDataframe(spark: SparkSession, imagePath: String): (DataFrame, Int, Int) = {

    import spark.implicits._
    val image_df = spark.read.format("image").load(imagePath)


    val width = image_df.select(col("image.width")).as[Int].first()

    val height = image_df.select(col("image.height")).as[Int].first()

    println(s"Larghezza = ${width} altezza = $height")
    val data = image_df.select("image.data").as[Array[Byte]]


    val dataToFilter = data.flatMap(x => x.zipWithIndex.map(a => (a._1 & 0xff, a._2 % 3, a._2)))
    val blue = dataToFilter.filter(x => x._2 == 0).drop("_2").toDF("blue", "id")
    val green = dataToFilter.filter(x => x._2 == 1).drop("_2").toDF("green", "id")
    val red = dataToFilter.filter(x => x._2 == 2).drop("_2").toDF("red", "id")


    val green_updated = green.withColumn("id", col("id") - 1)
    val red_updated = red.withColumn("id", col("id") - 2)
    val int3: Dataset[((Int, Int, Int, Int))] = blue.join(green_updated, "id").join(red_updated, "id")
      .withColumn("id", (col("id") / 3).cast("int")).as[(Int, Int, Int, Int)]


    val ris: Dataset[(Int, Int, Int, Int, Int)] = int3.map(x => (x._1 % width, (x._1 / width.toDouble).toInt, x._2, x._3, x._4)) // aggiunto le coordinate asse x
      .toDF("width", "height", "blue", "green", "red")
      .as[(Int, Int, Int, Int, Int)]


    val assembler = new VectorAssembler()
      .setInputCols(Array("blue", "green", "red"))
      .setOutputCol("features")

    val output = assembler.transform(ris)
    (output, width, height)


  }

}
