import static org.bytedeco.javacpp.flandmark.flandmark_init;

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
//import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.utils.Converters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bytedeco.javacpp.flandmark.FLANDMARK_Model;

//CORE - IMPROC   HIGHGUI - IMGCODECS
//Main class to detect multi faces from image and save output
public class ImageManager {

	public String PATH_PROCESSED_LIST = "C:\\opencv\\workfile\\ProcessedImage.txt";   //txt file
	String[] processedImages;
	public String PATH_IMAGE_FOLDER = "C:\\opencv\\workfile\\ImageFolder";
	public String PATH_OUTPUT = "C:\\opencv\\workfile\\ImageOutput";
	public String PATH_OUTPUT_PREPROCESS = "C:\\opencv\\workfile\\ImageOutput_Preprocess";
	public String PATH_OUTPUT_CROPPED = "C:\\opencv\\workfile\\ImageOutput_Cropped";
	ArrayList<String> newProcessedImage;

	public String DETECTOR_FACE = "CascadeClassifier\\haarcascades\\haarcascade_frontalface_alt.xml";
	public String DETECTOR_EYE = "CascadeClassifier\\haarcascades\\haarcascade_eye.xml";
	//	public String DETECTOR_EYE = "CascadeClassifier\\haarcascades\\parojos.xml";
	public String DETECTOR_MOUTH = "CascadeClassifier\\haarcascades\\Mouth.xml";
	public String DETECTOR_NOSE = "CascadeClassifier\\haarcascades\\Nose.xml";

	public static String PATH_FEATURE_OUTPUT = "C:\\opencv\\workfile\\featureOutput.dat";

	final File flandmarkModelFile = new File("flandmark_model.dat");
	ArrayList<ImgData> featureData = new ArrayList<ImgData>();
	/**cropped image file format:
	 * 
	 * Delimiter = "_" //lastIndexOf
	 * 
	 * [Name]_pointX/pointY/width/height
	 */

	public static void main(String[] args) {
		ImageManager im = new ImageManager();
		im.clearCaches();
		im.readProcessedImages();
		//(String path, boolean createOutput, boolean highlightFeatures, boolean createCropped, int w, int h, boolean greyScale, String format, boolean saveToSubfolder)
		boolean savePreprocess = false;
		boolean createOutput = false;		//save picture + rectangle around face
		boolean highlightFeatures = false;	//rectangle around eye,mouth,nose
		boolean createCropped = true;		//save face as separate image
		boolean highlightSurf = false;		//highlight features keypoints
		int w = 0;
		int h = 0;
		String format = "png";
		im.processImage(im.PATH_IMAGE_FOLDER,savePreprocess,createOutput,highlightFeatures,createCropped,highlightSurf,w, h, format);
		//im.postProcess();
	}

	public ImageManager(){
		newProcessedImage = new ArrayList<String>();

		//All directories used by the program, check in loop if they exists
		String[] usedDir = {PATH_IMAGE_FOLDER,PATH_OUTPUT,PATH_OUTPUT_CROPPED,PATH_OUTPUT_PREPROCESS};
		// if the directory does not exist, create it
		for(String s:usedDir){
			File f = new File(s);
			if (!f.exists()) {
				System.out.println("Dir "+s+" does not exist, creating new dir");
				boolean result = false;
				try{
					f.mkdirs();
					result = true;
				} 
				catch(SecurityException se){System.err.print(se.getClass()+": "+se.getMessage());}        
				if(result) {    
					System.out.println(s+ " folder created");  
				}
			}
		}
		//check file exists as well
		File f2 = new File(PATH_PROCESSED_LIST);
		if(!f2.exists()) {
			try {
				f2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * read processedImageListFile and put all processedImages into string
	 * DO IN CONSTRUCTOR
	 */
	public void readProcessedImages(){
		//read and store to processedImage
		FileReader fileReader = null;
		try { 
			fileReader = new FileReader(PATH_PROCESSED_LIST);
		} catch (FileNotFoundException e) {e.printStackTrace();}
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {e.printStackTrace();}
		try {
			bufferedReader.close();
			fileReader.close();
		} catch (IOException e) {e.printStackTrace();}
		processedImages =  lines.toArray(new String[lines.size()]);
	}

	public void processImage(String path,boolean savePreprocess, boolean createOutput, boolean highlightFeatures ,boolean createCropped, boolean highlightSurf, int w, int h, String format) {
		int successCount = 0;
		
		if(processedImages==null)processedImages = new String[0];
		File root = new File( path );
		File[] list = root.listFiles();
		if (list == null) return;
		for ( File f : list ) {
			if ( f.isDirectory() ) {
				processImage(f.getAbsolutePath(),savePreprocess,createOutput,highlightFeatures,createCropped,highlightSurf, w, h, format);
			}
			else {
				CascadeClassifier faceDetector = new CascadeClassifier(this.DETECTOR_FACE);

				//Check if list is in array
				String filepath = f.getAbsolutePath();
				String filename = f.getName();
				if(Arrays.binarySearch(processedImages, filepath)<0){
					newProcessedImage.add(filepath); //add to list of processed image
					//Process new image
					System.out.println("New file to process: " + filename);
					Mat image =  Highgui.imread(filepath);

					//PRE PROCESSING

					boolean oversize = image.size().area()>786432;

					while(oversize){
						System.out.println("Warning: "+filename+" is too large, shrinking");
						Imgproc.resize(image, image,new Size(image.size().width*0.75, image.size().height*0.75));
						oversize = image.size().area()>786432;
					}
					// IMAGECLONE USE FOR STORE IMAGE/KEYPOINTS FOR CREATE OUTPUT
					// IMAGE USED FOR DETECTION
					Mat imageClone = null;
					imageClone = image.clone();

					try{
						Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);
					}catch(CvException e){
						e.printStackTrace();
						System.err.println(filename+" Error: "+e.getMessage());
						continue;
					}
					Imgproc.equalizeHist(image, image);
					if(savePreprocess){
						String formattedFilepath = PATH_OUTPUT_PREPROCESS
								+filepath.substring(filepath.indexOf(PATH_IMAGE_FOLDER)+PATH_IMAGE_FOLDER.length(),filepath.lastIndexOf('\\')) + "\\"
								+filename;
						String outputRoot = formattedFilepath.substring(0,formattedFilepath.lastIndexOf('\\'));
						if(!new File(outputRoot).exists())new File(outputRoot).mkdirs();
						System.out.println(String.format("Writing[P]: %s", formattedFilepath));
						try{
							Highgui.imwrite(formattedFilepath, image);
						}catch(CvException e){
							System.err.println(filename+" is of unsupported filetype");
							continue;
						}
					}

					MatOfRect faceDetections = new MatOfRect();

					//DETECT FACES

					faceDetector.detectMultiScale(image, faceDetections);
					System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
					if(faceDetections.toArray().length==1)successCount++;
					for (Rect rect : faceDetections.toArray()) {	
						//MatOfInt moi = new MatOfInt();
						Mat croppedImage = image.submat(rect);
						//DETECT LANDMARKS WITH FLANDMARK

						/*
						try {
							FLANDMARK_Model model = loadFLandmarkModel(flandmarkModelFile);
						} catch (IOException e) {
							e.printStackTrace();
						}

						double[] f_double;
						int[] f_int;


						flandmark.flandmark_detect(croppedImage, f_int, model, f_double);
						 */

						//=========================OUTPUT=========================
						//DETECT LANDMARKS WITH HAAR CASCADES
						if(createOutput){
							CascadeClassifier eyeDetector = null;
							CascadeClassifier noseDetector = null;
							CascadeClassifier mouthDetector = null;
							MatOfRect eyeDetections = null;
							MatOfRect noseDetections = null;
							MatOfRect mouthDetections = null;
							//	Rect _eyeRectL;
							//	Rect _eyeRectR;
							//	Rect _noseRect;
							//	Rect _mouthRect;
							if(highlightFeatures){
								eyeDetector = new CascadeClassifier(this.DETECTOR_EYE);
								noseDetector = new CascadeClassifier(this.DETECTOR_NOSE);
								mouthDetector = new CascadeClassifier(this.DETECTOR_MOUTH);
								//_eyeRectL = new Rect(rect.x+rect.width/7, rect.y, rect.width*2/7, rect.height/2);
								//_eyeRectR = new Rect(rect.x+rect.width*4/7, rect.y, rect.width*2/7, rect.height/2);
								//_noseRect = new Rect(rect.x+rect.width/4, rect.y+rect.height/4, rect.width/2, rect.height/2);
								//_mouthRect = new Rect(rect.x+rect.width/6, rect.y+rect.height*2/3, rect.width*2/3, rect.height/3);

								eyeDetections = new MatOfRect();

								eyeDetector.detectMultiScale(croppedImage,eyeDetections, 1.1, 3, 0, 
										new Size(rect.width/8,rect.height/8), new Size(rect.width/3,rect.height/3));
								noseDetections = new MatOfRect();
								noseDetector.detectMultiScale(croppedImage,noseDetections, 1.1, 3, 0, 
										new Size(rect.width/4.55,rect.height/4.76), new Size(rect.width/3.13,rect.height/3.23));
								mouthDetections = new MatOfRect();
								mouthDetector.detectMultiScale(croppedImage,mouthDetections, 1.1, 4, 0, 
										new Size(rect.width/4.76,rect.height/7.69), new Size(rect.width/3.23,rect.height/4.35));
								//Get the most possible detected feature

							}

							//DRAW RECT ON FACE

							Core.rectangle(imageClone, new Point(rect.x, rect.y), 
									new Point(rect.x + rect.width, rect.y + rect.height),
									new Scalar(0, 0, 255));
							if(highlightFeatures){
								//save coord
								int x = rect.x;
								int y = rect.y;

								double minDist = Double.MAX_VALUE;
								double minDist2 = Double.MAX_VALUE;

								//DRAW RECT ON EYE

								Rect eyeMostL = null;
								Rect eyeMostR = null;
								for (Rect eyeRect : eyeDetections.toArray()) {
									//dist btwn IDEAL / ACTUAL
									double dist = euclideanDist(new Point(x+rect.width*0.3,y+rect.height*0.3),new Point(x+eyeRect.x+eyeRect.width/2,y+eyeRect.y+eyeRect.height/2));
									if(dist<minDist){
										eyeMostL = eyeRect;
										minDist = dist;
									}
									double dist2 = euclideanDist(new Point(x+rect.width*0.7,y+rect.height*0.3),new Point(x+eyeRect.x+eyeRect.width/2,y+eyeRect.y+eyeRect.height/2));
									if(dist2<minDist2){
										eyeMostR = eyeRect;
										minDist2 = dist2;
									}
								}
								if(eyeMostL!=null){
									Core.rectangle(imageClone, new Point(x+eyeMostL.x, y+eyeMostL.y), 
											new Point(x+eyeMostL.x + eyeMostL.width, y+eyeMostL.y + eyeMostL.height),
											new Scalar(255, 255, 0));
								}
								if(eyeMostR!=null){
									Core.rectangle(imageClone, new Point(x+eyeMostR.x, y+eyeMostR.y), 
											new Point(x+eyeMostR.x + eyeMostR.width, y+eyeMostR.y + eyeMostR.height),
											new Scalar(255, 0, 0));
								}

								//DRAW RECT ON NOSE

								minDist = Double.MAX_VALUE;
								Rect noseMost = null;
								for (Rect noseRect : noseDetections.toArray()) {
									double dist = euclideanDist(new Point(x+rect.width*0.5,y+rect.height*0.65),new Point(x+noseRect.x+noseRect.width/2,y+noseRect.y+noseRect.height/2));
									if(dist<minDist){
										noseMost = noseRect;
										minDist = dist;
									}
								}
								if(noseMost!=null){
									Core.rectangle(imageClone, new Point(x+noseMost.x, y+noseMost.y), 
											new Point(x+noseMost.x + noseMost.width, y+noseMost.y + noseMost.height),
											new Scalar(0, 255, 0));
								}

								//DRAW RECT ON MOUTH

								minDist = Double.MAX_VALUE;
								Rect mouthMost = null;
								for (Rect mouthRect : mouthDetections.toArray()) {
									double dist = euclideanDist(new Point(x+rect.width*0.5,y+rect.height*0.80),new Point(x+mouthRect.x+mouthRect.width/2,y+mouthRect.y+mouthRect.height/2));
									if(dist<minDist){
										mouthMost = mouthRect;
										minDist = dist;
									}
								}
								if(mouthMost!=null){
									Core.rectangle(imageClone, new Point(x+mouthMost.x, y+mouthMost.y), 
											new Point(x+mouthMost.x + mouthMost.width, y+mouthMost.y + mouthMost.height),
											new Scalar(255, 0, 255));
								}

							}
						}
						//=========================CROPPED=========================
						//

						if(createCropped){
							
							//Cropped format = <filename>_x,y,width,height.<fileformat>
							StringBuilder sb = new StringBuilder();
							
							sb.append(filename.substring(0, filename.lastIndexOf('.')));
							sb.append('_');	sb.append(rect.x);
							sb.append(',');	sb.append(rect.y);
							sb.append(',');	sb.append(rect.width);
							sb.append(',');	sb.append(rect.height);
							if(format!=""){ //custom format
								sb.append("."+format);
							}else{
								sb.append(filename.substring(filename.lastIndexOf('.'),filename.length()));
							}
							String newFileName = sb.toString();
							
							String formattedFilepath = PATH_OUTPUT_CROPPED
									+filepath.substring(filepath.indexOf(PATH_IMAGE_FOLDER)+PATH_IMAGE_FOLDER.length(),filepath.lastIndexOf('\\')) + "\\"
									+newFileName;
							String outputRoot = formattedFilepath.substring(0,formattedFilepath.lastIndexOf('\\'));
							if(!new File(outputRoot).exists())new File(outputRoot).mkdirs();

							if(w!=0&&h!=0){
								Size size = new Size(w,h);
								Imgproc.resize(croppedImage, croppedImage, size);
							}

							//TODO: FEATURE EXTRACTION FOR EACH INVIDIDUAL FACE

							MatOfKeyPoint keypoints = new MatOfKeyPoint();
							FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
							detector.detect(croppedImage, keypoints);
							System.out.println("Keypoints.size(): "+keypoints.size());
							if(highlightSurf)Features2d.drawKeypoints(croppedImage, keypoints, croppedImage);

							//save to array of data
							featureData.add(new ImgData(formattedFilepath,keypoints.toArray()));

							//Save cropped image to folder
							System.out.println(String.format("Writing[C] %s", formattedFilepath));
							Highgui.imwrite(formattedFilepath, croppedImage);
						}

					}

					//SAVE OUTPUT FILE
					if(createOutput){
						String formattedFilepath = PATH_OUTPUT
								+filepath.substring(filepath.indexOf(PATH_IMAGE_FOLDER)+PATH_IMAGE_FOLDER.length(),filepath.lastIndexOf('\\')) + "\\"
								+filename;
						String outputRoot = formattedFilepath.substring(0,formattedFilepath.lastIndexOf('\\'));
						if(!new File(outputRoot).exists())new File(outputRoot).mkdirs();
						System.out.println(String.format("Writing: %s", formattedFilepath));
						try{
							Highgui.imwrite(formattedFilepath, imageClone);
						}catch(CvException e){
							System.err.println(filename+" is of unsupported filetype");
							continue;
						}
					}
				};
			}
		}
		//add list of processed image to file
		try(FileWriter fw = new FileWriter(PATH_PROCESSED_LIST, false);
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
		System.out.println("Success count: "+successCount);

		//TODO: save all featureData to file
		ArrayList<ImgData> oldData = (ArrayList<ImgData>) readObjectFromFile();
		if(oldData==null)oldData = new ArrayList<ImgData>();
		oldData.addAll(featureData);
		featureData.clear();
		saveObjectToFile(oldData);
		/*
		try {
			File file = new File(PATH_FEATURE_OUTPUT);
			JAXBContext jaxbContext = JAXBContext.newInstance(ImgDataList.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(featureData, file);
			jaxbMarshaller.marshal(featureData, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		 */
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
		System.out.println("Clearing caches");
		//clear image folder
		try {
			//FileUtils.cleanDirectory(new File(imageFolderPath));
			FileUtils.cleanDirectory(new File(PATH_OUTPUT)); 
			FileUtils.cleanDirectory(new File(PATH_OUTPUT_CROPPED)); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//delete processedImageListFile contents
		System.out.println(PATH_PROCESSED_LIST);
		try {
			FileUtils.forceDelete(new File(PATH_PROCESSED_LIST));

		} catch (IOException e1) {e1.printStackTrace();}
		try {
			FileUtils.forceDelete(new File(PATH_FEATURE_OUTPUT));
		} catch (IOException e1) {
			System.out.println("Cannot delete featureOutput file as it does not exists");
		}
		try {
			new File(PATH_PROCESSED_LIST).createNewFile();
		} catch (IOException e) {e.printStackTrace();}
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
	public double euclideanDist(Point p, Point q) {
		Point diff = new Point(p.x-q.x,p.y-q.y);
		return Math.sqrt(diff.x*diff.x + diff.y*diff.y);
	}
	public static FLANDMARK_Model loadFLandmarkModel(final File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException("FLandmark model file does not exist: " + file.getAbsolutePath());
		}
		final FLANDMARK_Model model = flandmark_init("flandmark_model.dat");
		if (model == null) {
			throw new IOException("Failed to load FLandmark model from file: " + file.getAbsolutePath());
		}

		return model;
	}
	public static void saveObjectToFile(Object data){
		try{
			FileOutputStream f_out = new FileOutputStream(PATH_FEATURE_OUTPUT);
			ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
			obj_out.writeObject ( data );
			obj_out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static Object readObjectFromFile(){
		Object obj = null;
		try{
			FileInputStream f_in = new FileInputStream(PATH_FEATURE_OUTPUT);
			ObjectInputStream obj_in = new ObjectInputStream (f_in);
			obj = obj_in.readObject();
			obj_in.close();
		}catch(Exception e){
			return null;
		}
		return obj;
	}
}
