package KMeans

import Image.ImageSegmentation.{ImageDataframe, ImagetoPrint}
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.sql.SparkSession
import config.ConfReader
import java.io.File
import javax.imageio.ImageIO

object KMeansImage {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
     // .master("local[*]")
      .appName("schema")
      .getOrCreate()


    val input: String = ConfReader.getUrlImage()
    val numK: Int = ConfReader.getNumK()
    val imageFolder = ConfReader.getImageOut()
    val clusterFolder = ConfReader.getClusterOut()
    val dimFolder = ConfReader.getDimOut()
    val pathOut = ConfReader.getPath()

    import spark.implicits._
    val kmeans = new KMeans().setK(numK.toInt).setSeed(1L).setFeaturesCol("features").setPredictionCol("prediction")


    val imageData = ImageDataframe(spark, input)

    val newImage = kmeans.fit(imageData._1)

    val centro = newImage.clusterCenters

    val ArrayClustersDouble = centro.map(x => x.toArray)

    val arrayClustersInt = ArrayClustersDouble.map(x => x.map(y => y.toInt))

    val pred = newImage.transform(imageData._1)
      .drop("features", "blue", "green", "red").as[(Int, Int, Int)]
    // .collect()

    val arrayClusterDataframe = arrayClustersInt.map(x => (x(0), x(1), x(2)))
      .toSeq
      .toDF("blue", "green", "red")
    val imageSizeDataframe = Seq((imageData._2, imageData._3)).toDF()

    pred.write.csv(imageFolder)
    arrayClusterDataframe.write.csv(clusterFolder)
    imageSizeDataframe.write.csv(dimFolder)


    val stampa = ImagetoPrint(imageData._2,imageData._3,pred.collect(),arrayClustersInt)

    ImageIO.write(stampa, "jpg", new File(pathOut))

    spark.stop()

  }


}
