import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Contour {

	public static void main(String[] args) {
		// Load the library

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Consider the image for processing
		Mat image = Imgcodecs.imread("C:\\opencv\\workfile\\Oasis_Towel_Stack_Z.jpg");
		Mat imageHSV = new Mat(image.size(), Imgproc.COLOR_BGR2GRAY);
		Mat imageBlurr = new Mat(image.size(), Imgproc.COLOR_BGR2GRAY);
		Mat imageA = new Mat(image.size(),Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(image, imageHSV, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(5,5), 0);
		Imgproc.adaptiveThreshold(imageBlurr, imageA, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,7, 5);

		Imgcodecs.imwrite("C:\\opencv\\workfile\\output_before.jpg", imageA);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
		Imgproc.findContours(imageA, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
		//Imgproc.drawContours(imageBlurr, contours, 1, new Scalar(0,0,255));
		MatOfPoint temp_contour;
		for(int i=0; i< contours.size();i++){
			System.out.println(Imgproc.contourArea(contours.get(i)));
		//	if (Imgproc.contourArea(contours.get(i) > 50 ){
				temp_contour = new MatOfPoint(contours.get(i).toArray());
				int contourSize = (int) temp_contour.total();
				Rect rect = Imgproc.boundingRect(contours.get(i));
			//	System.out.println(rect.height);
				
				MatOfPoint2f approxCurveTemp = new MatOfPoint2f();
				
				MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
				Imgproc.approxPolyDP(new_mat, approxCurveTemp, 0.05, true);
				if (approxCurveTemp.total()==4){
					System.out.println("YAY "+rect.height+" "+rect.width+" "+rect.x+" "+rect.y);
					Imgproc.rectangle(image, new Point(rect.x, rect.height), 
							new Point(rect.y,rect.width),
							new Scalar(0, 255, 0));
					
				}
		//	}
		}
		Imgcodecs.imwrite("C:\\opencv\\workfile\\output.jpg", image);
	}

}
