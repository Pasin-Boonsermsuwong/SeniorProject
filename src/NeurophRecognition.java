import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.imgrec.ImageRecognitionPlugin;
import org.neuroph.util.NeuralNetworkType;

import java.util.HashMap;
import java.io.File;
import java.io.IOException;

public class NeurophRecognition {

 public static void main(String[] args) {
	 
	DataSet ds;
	ds.addRow(input);
	ds.ad
	
	
	ImageRecognitionPlugin ir;
	NeuralNetwork n;
	n.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);
	n.add
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