package util
import Image.ImageSegmentation.ImagetoPrint
import util.ReadFile._
import java.io.File
import javax.imageio.ImageIO
import config.ConfReader
object StampaImmagine {

  def main(args: Array[String]): Unit = {

    val cartellaImmagine = ConfReader.getImageOut() // cartella dove si trova l'immagine nella forma x,y <cluster a cui appartiene il pixel x,y>
    val cartellaDim = ConfReader.getDimOut()// cartella in cui si trovano le dimensioni dell'immagine
    val cartellaCluster = ConfReader.getClusterOut() // cartella in cui ad ogni cluster vengono associati i colori b g r
    val outputPath = ConfReader.getPath() // path dell'immagine in output
    val size = getCluster(cartellaDim)
    val pred = getData(cartellaImmagine)
    val arrayClustersInt = getCluster(cartellaCluster)



    val stampa = ImagetoPrint(size(0)(0),size(0)(1),pred,arrayClustersInt)

    ImageIO.write(stampa, "jpg", new File(outputPath))

  }

}
