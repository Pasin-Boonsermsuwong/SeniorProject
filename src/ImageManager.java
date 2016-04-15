import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.opencv.core.Core;

//Main class to detect multi faces from image and save output
public class ImageManager {

	public final String processedImageListFile = "C://opencv//workfile//ProcessedImage.txt";   //txt file
	String[] processedImages;
	public final String imageFolderPath = "C://opencv//workfile//ImageFolder";
	//public final String imageFolderPath = "C://opencv//workfile//test";
	public final String imageOutputFolderPath = "C://opencv//workfile//ImageOutput";
	public final String croppedFaceFolderPath = "C://opencv//workfile//ImageOutput_Cropped";
	ArrayList<String> newProcessedImage;

	public final String DETECTOR_FACE = "CascadeClassifier/haarcascades/haarcascade_frontalface_alt.xml";
	//public final String DETECTOR_EYE = "CascadeClassifier/haarcascades/haarcascade_eye.xml";
	public final String DETECTOR_EYE = "CascadeClassifier/haarcascades/parojos.xml";
	public final String DETECTOR_MOUTH = "CascadeClassifier/haarcascades/Mouth.xml";
	public final String DETECTOR_NOSE = "CascadeClassifier/haarcascades/Nose.xml";
	/**cropped image file format:
	 * 
	 * Delimiter = "_" //lastIndexOf
	 * 
	 * [Name]_pointX/pointY/width/height
	 */



	public ImageManager(){
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
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		readProcessedImages();
	}


	/**
	 * read processedImageListFile and put all processedImages into string
	 * DO IN CONSTRUCTOR
	 */
	private void readProcessedImages(){

		//count no of lines and initiate array to store processimagename
		int lines = 0;
		try {
			lines = countLines(processedImageListFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		processedImages = new String[lines];
		//read and store to processedImage
		int i = 0;
		try(BufferedReader br = new BufferedReader(new FileReader(processedImageListFile))) {
			String line = br.readLine();

			while (line != null) {
				if(line!="")processedImages[i]=line;
				//     sb.append(System.lineSeparator());
				line = br.readLine();
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//sort for binary search later
		//SORT WHEN WRITING TO FILE
		//	Arrays.sort(processedImages);

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
				CascadeClassifier faceDetector = new CascadeClassifier(this.DETECTOR_FACE);
				CascadeClassifier eyeDetector = new CascadeClassifier(this.DETECTOR_EYE);
				CascadeClassifier noseDetector = new CascadeClassifier(this.DETECTOR_NOSE);
				CascadeClassifier mouthDetector = new CascadeClassifier(this.DETECTOR_MOUTH);
				//Check if list is in array
				String filepath = f.getAbsolutePath();
				String filename = f.getName();
				if(Arrays.binarySearch(processedImages, filename)<0){
					newProcessedImage.add(filename); //add to list of processed image
					//Process new image
					System.out.println("New file to process: " + filename);
					Mat image = Imgcodecs.imread(filepath);
					//imageClone specifically use for createOutput (draw lines on image)
					Mat imageClone = null;
					if(createOutput){
						imageClone = image.clone();
					}
					MatOfRect faceDetections = new MatOfRect();
					faceDetector.detectMultiScale(image, faceDetections);
					System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

					//if(faceDetections.toArray().length<1)continue;
					for (Rect rect : faceDetections.toArray()) {
						MatOfInt moi = new MatOfInt();
						//	Mat croppedImage = new Mat(image,rect);
						Mat croppedImage = image.submat(rect);
						MatOfRect eyeDetections = new MatOfRect();
						//eyeDetector.detectMultiScale(croppedImage,eyeDetections);
						eyeDetector.detectMultiScale2(croppedImage, eyeDetections, moi, 1.1, 3, 0, new Size(rect.width/8,rect.height/8), new Size(rect.width/4,rect.height/4));
						//if(eyeDetections.toArray().length!=2)continue;
						MatOfRect noseDetections = new MatOfRect();
						noseDetector.detectMultiScale2(croppedImage,noseDetections, moi, 1.1, 3, 0, new Size(rect.width/4.55,rect.height/4.76), new Size(rect.width/3.13,rect.height/3.23));
						//if(noseDetections.toArray().length!=1)continue;
						MatOfRect mouthDetections = new MatOfRect();
						mouthDetector.detectMultiScale2(croppedImage,mouthDetections, moi, 1.1, 4, 0, new Size(rect.width/4.76,rect.height/7.69), new Size(rect.width/3.23,rect.height/4.35));
						//if(mouthDetections.toArray().length!=1)continue;
						//TODO: maybe add option to skip continue check;

						if(createCropped){
							//Cropped format = <filename>_x,y,width,height.<fileformat>
							StringBuilder sb = new StringBuilder();
							sb.append(filename.substring(0, filename.lastIndexOf('.')));
							sb.append('_');	sb.append(rect.x);
							sb.append(',');	sb.append(rect.y);
							sb.append(',');	sb.append(rect.width);
							sb.append(',');	sb.append(rect.height);
							sb.append(filename.substring(filename.lastIndexOf('.'),filename.length()));
							String formattedFilename = croppedFaceFolderPath+"//"+sb.toString();
							System.out.println(String.format("Writing[C] %s", formattedFilename));
							Imgcodecs.imwrite(formattedFilename, croppedImage);
						}
						if(createOutput){
							//FACE
							Imgproc.rectangle(imageClone, new Point(rect.x, rect.y), 
									new Point(rect.x + rect.width, rect.y + rect.height),
									new Scalar(0, 0, 255));
							//save coord
							int x = rect.x;
							int y = rect.y;
							//EYE
							for (Rect eyeRect : eyeDetections.toArray()) {
								Imgproc.rectangle(imageClone, new Point(x+eyeRect.x, y+eyeRect.y), 
										new Point(x+eyeRect.x + eyeRect.width, y+eyeRect.y + eyeRect.height),
										new Scalar(255, 0, 0));
							}
							//NOSE
							for (Rect noseRect : noseDetections.toArray()) {
								Imgproc.rectangle(imageClone, new Point(x+noseRect.x, y+noseRect.y), 
										new Point(x+noseRect.x + noseRect.width, y+noseRect.y + noseRect.height),
										new Scalar(0, 255, 0));
							}
							//MOUTH
							for (Rect mouthRect : mouthDetections.toArray()) {
								Imgproc.rectangle(imageClone, new Point(x+mouthRect.x, y+mouthRect.y), 
										new Point(x+mouthRect.x + mouthRect.width, y+mouthRect.y + mouthRect.height),
										new Scalar(255, 0, 255));
							}
						}
					}
					if(createOutput){
						String output = imageOutputFolderPath+"//"+filename;
						System.out.println(String.format("Writing: %s", output));
						try{
							Imgcodecs.imwrite(output, imageClone);
						}catch(CvException e){
							System.err.println(filename+" is of unsupported filetype");
							continue;
						}
					}
				};
			}
		}
		//add list of processed image to file
		try(FileWriter fw = new FileWriter(processedImageListFile, false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw))
		{
			String[] allProcessedImages = (String[])ArrayUtils.addAll(processedImages, newProcessedImage.toArray());
			Arrays.sort(allProcessedImages);
			for(String s:allProcessedImages){
				out.println(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void listFilePath(String path, ArrayList<String> output) {
		File root = new File(path);
		File[] list = root.listFiles();
		if (list == null) return;

		for ( File f : list ) {
			if (f.isDirectory()) {
				listFilePath(f.getAbsolutePath(),output);
			}
			else {
				output.add(f.getAbsolutePath());
			}
		}
	}
	public void listFile(String path, ArrayList<File> output) {
		File root = new File(path);
		File[] list = root.listFiles();
		if (list == null) return;

		for ( File f : list ) {
			if (f.isDirectory()) {
				listFile(f.getAbsolutePath(),output);
			}
			else {
				output.add(f.getAbsoluteFile());
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
			//FileUtils.cleanDirectory(new File(imageFolderPath));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print("");
		writer.close();
	}

	public static void main(String[] args) {
		ImageManager im = new ImageManager();
		//im.clearCaches();
		System.out.println("Start processing");
		im.processImage(im.imageFolderPath,true,false);
	}

	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}
}
