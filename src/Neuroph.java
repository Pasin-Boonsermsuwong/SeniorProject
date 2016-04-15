import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ImageRecognitionPlugin;
import org.neuroph.nnet.MultiLayerPerceptron;

public class Neuroph {

	public static void main(String[] args) {
		/*
		MultiLayerPerceptron m;
		// TODO Auto-generated method stub
		m = (MultiLayerPerceptron) NeuralNetwork.createFromFile("N3.nnet");
		ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin)m.getPlugin(ImageRecognitionPlugin.class);
		System.out.println(imageRecognition);
		*/
		
	    NeuralNetwork nnet = NeuralNetwork.createFromFile("N3.nnet"); // load trained neural network saved with Neuroph Studio
	    // get the image recognition plugin from neural network
	    ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin)nnet.getPlugin(ImageRecognitionPlugin.class); // get the image recognition plugin from neural network
	    
	    
	    
	    
	    ImageManager im = new ImageManager();
	    ArrayList<File> filelist = new ArrayList<File>();
	    im.listFile(im.croppedFaceFolderPath, filelist);
	    //System.out.println(Arrays.toString(filelist.toArray()));
	    System.out.println("Load list of cropped faces, no. of pic loaded = "+filelist.size());
	    
	    for(File f: filelist){
	    	
	     //    System.out.println("Output: "+output.toString());

	    }
	
	    
	}

}
