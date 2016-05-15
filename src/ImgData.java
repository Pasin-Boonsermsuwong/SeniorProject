import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.KeyPoint;

public class ImgData implements Serializable , Comparator<ImgData>, Comparable<ImgData>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1731653894582618799L;
	public String fileName;
	public SerializedKP[] keyPoints;
	public float avgDist;
	public float minDist;
	public float maxDist;
	public ImgData(){}

	public ImgData(String fileName , KeyPoint[] keyPoints){
		this.fileName = fileName;
		SerializedKP[] serializedKP = new SerializedKP[keyPoints.length];
		for(int i=0;i<keyPoints.length;i++){
			serializedKP[i] = new SerializedKP(keyPoints[i]);
		}
		this.keyPoints = serializedKP;
	}
	public MatOfKeyPoint toKeyPointArray(){
		KeyPoint[] kp = new KeyPoint[keyPoints.length];
		for(int i=0;i<keyPoints.length;i++){
			kp[i] = keyPoints[i].toKeyPoint();
		}

		return new MatOfKeyPoint(kp);

	}
	public static void main(String args[]){
		ArrayList<ImgData> a = (ArrayList<ImgData>)ImageManager.readObjectFromFile();
		for(ImgData i:a){
			System.out.println(i.fileName);
			System.out.println(i.keyPoints.length);
		}
	}


	@Override
	public int compare(ImgData img1, ImgData img2) {
		if(img1.avgDist<img2.avgDist)return -1;
		else if(img1.avgDist==img2.avgDist) return 0;
		else return 1;
	}

	@Override
	public int compareTo(ImgData o) {
		if(this.avgDist<o.avgDist)return -1;
		else if(this.avgDist==o.avgDist) return 0;
		else return 1;
	}

}
