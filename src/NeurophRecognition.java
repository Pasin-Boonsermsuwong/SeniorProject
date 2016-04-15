import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ImageRecognitionPlugin;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

public class NeurophRecognition {

 public static void main(String[] args) {
    // load trained neural network saved with Neuroph Studio (specify some existing neural network file here)
    NeuralNetwork nnet = NeuralNetwork.createFromFile("N3.nnet"); // load trained neural network saved with Neuroph Studio
    // get the image recognition plugin from neural network
    ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin)nnet.getPlugin(ImageRecognitionPlugin.class); // get the image recognition plugin from neural network

    try {
         // image recognition is done here (specify some existing image file)
        HashMap<String, Double> output = imageRecognition.recognizeImage(new File("someImage.jpg"));
        System.out.println(output.toString());
    } catch(IOException ioe) {
        ioe.printStackTrace();
    }
 }
}