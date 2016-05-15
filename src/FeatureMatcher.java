import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FeatureMatcher {

	/**
	 * @param filePath Picture of cropped single face
	 * @return
	 */
	public static MatOfKeyPoint getFeatureFromFile(String filePath){
		Mat image =  Highgui.imread(filePath);
		boolean oversize = image.size().area()>786432;
		while(oversize){
			System.out.println("Warning: "+filePath+" is too large, shrinking");
			Imgproc.resize(image, image,new Size(image.size().width*0.75, image.size().height*0.75));
			oversize = image.size().area()>786432;
		}
		Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);
		Imgproc.equalizeHist(image, image);
	//	Imgproc.resize(image, image, new Size(92,112));
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
		detector.detect(image, keypoints);
		Features2d.drawKeypoints(image, keypoints, image);
		return keypoints;
	}

	public static void main(String args[]){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String input = "C:\\opencv\\workfile\\ImageFolder\\Obama\\Obama_5.jpg";
		MatOfKeyPoint sampleKeyPoint = getFeatureFromFile(input);
		System.out.println("SampleKeyPoint: "+sampleKeyPoint);
		ArrayList<ImgData> dat = (ArrayList<ImgData>) ImageManager.readObjectFromFile();
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SURF); 
		MatOfDMatch matches = new MatOfDMatch();
		Mat descSample = new Mat();
		Mat descCompare = new Mat();
		extractor.compute(Highgui.imread(input), sampleKeyPoint, descSample);

		for(ImgData d:dat){
	        extractor.compute(Highgui.imread(d.fileName), d.toKeyPointArray(), descCompare);
			//System.out.println(descSample.toString());
			//System.out.println(descCompare.toString());
	        try{
	        	matcher.match(descSample,descCompare,matches);
	        }catch(CvException e){
	        	e.printStackTrace();
	        	//System.err.println(d.fileName+" cannot be compared, assertion failed");
	        }
	       // System.out.println("matches.size() : " + matches.size());
			DMatch[] dist = matches.toArray();
		//	System.out.println(Arrays.toString(dist));
			d.avgDist = computeAvgDist(dist);
			d.minDist = getMinDist(dist);
			d.maxDist = getMaxDist(dist);
			//System.out.println("Filename: "+d.fileName+" avgDist: "+computeAvgDist(dist)+" minDist: "+getMinDist(dist)+" maxDist: "+getMaxDist(dist));
		}
		Object[] datArray = dat.toArray();
		Arrays.sort(datArray);
		for(Object o:datArray){
			ImgData d = (ImgData) o;
			System.out.println("Filename: "+d.fileName+" avgDist: "+d.avgDist+" minDist: "+d.minDist+" maxDist: "+d.maxDist);
		}
	}
	public static float computeAvgDist(DMatch[] d){
		float sum = 0;
		for(int i=0;i<d.length;i++){
			sum += d[i].distance;
		}
		float avg = sum/d.length;
		return avg;
	}
	public static float getMinDist(DMatch[] d){
		float min = Integer.MAX_VALUE;
		for(int i=0;i<d.length;i++){
			min = Math.min(min, d[i].distance);
		}
		return min;
	}
	public static float getMaxDist(DMatch[] d){
		float max = Integer.MIN_VALUE;
		for(int i=0;i<d.length;i++){
			max = Math.max(max, d[i].distance);
		}
		return max;
	}
}
