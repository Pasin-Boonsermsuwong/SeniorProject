import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;

//Main class to detect multi faces from image and save output
public class ImageManager {

	public String processedImageListFile = "C://opencv//workfile//ProcessedImage.txt";   //txt file
	ArrayList<String> processedImages;
	public String imageFolderPath = "C://opencv//workfile//ImageFolder";
	public String imageOutputFolderPath = "C://opencv//workfile//ImageOutput";
	public String croppedFaceFolderPath = "C://opencv//workfile//ImageOutput_Cropped";
	ArrayList<String> newProcessedImage;
	/**cropped image file format:
	 * 
	 * Delimiter = "_" //lastIndexOf
	 * 
	 * [Name]_pointX/pointY/width/height
	 */

	

	public ImageManager(){
		processedImages = new ArrayList<String>();
		newProcessedImage = new ArrayList<String>();
		
		//All directories used by the program, check in loop if they exists
		String[] usedDir = {imageFolderPath,imageOutputFolderPath,croppedFaceFolderPath};
		//TODO: check if the folders/processedimageTxt exist,
		// if the directory does not exist, create it
		for(String s:usedDir){
			File f = new File(s);
			if (!f.exists()) {
				System.out.println("Dir "+s+" does not exist, creating new dir");
			    boolean result = false;
			    try{
			        f.mkdir();
			        result = true;
			    } 
			    catch(SecurityException se){
			        System.err.print(se.getClass()+": "+se.getMessage());
			    }        
			    if(result) {    
			        System.out.println(s+ " folder created");  
			    }
			}
		}
		//check file exists as well
		File f2 = new File(processedImageListFile);
		if(!f2.exists()) {
		    try {
				f2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		try {
			FileOutputStream oFile = new FileOutputStream(f2, true);
		} catch (FileNotFoundException e) {} 
		
		
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		readProcessedImages();
		
	}
	
	
	/**
	 * read processedImageListFile and put all processedImages into string
	 * DO IN CONSTRUCTOR
	 */
	private void readProcessedImages(){
		//read and store to processedImage
		try(BufferedReader br = new BufferedReader(new FileReader(processedImageListFile))) {
		    String line = br.readLine();

		    while (line != null) {
		    	if(line!="")processedImages.add(line);
		   //     sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//sort for binary search later
		Arrays.sort(processedImages.toArray());

	}
	
    /**
     * Image processing method
     * @param path
     * @param createOutput
     * @param createCropped
     */
    public void processImage(String path,boolean createOutput, boolean createCropped ) {

        File root = new File( path );
        File[] list = root.listFiles();
        if (list == null) return;
        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	processImage( f.getAbsolutePath(),createOutput,createCropped );
            }
            else {
                System.out.println( "File: " + f.getAbsoluteFile() );         
                
            }
        }
    }
	
	/**
	 *  delete from processedImageListFile
	 *  delete from output folder
	 *  delete from cropped face folder
	 */
	public void clearCaches(){
		//clear image folder
		try {
			FileUtils.cleanDirectory(new File(imageFolderPath));
			FileUtils.cleanDirectory(new File(imageOutputFolderPath)); 
			FileUtils.cleanDirectory(new File(croppedFaceFolderPath)); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//delete processedImageListFile contents
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(processedImageListFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.print("");
		writer.close();
	}
	
	public static void main(String[] args) {
		ImageManager im = new ImageManager();

	}

}
