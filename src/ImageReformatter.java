import java.io.File;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageReformatter {
	
	public static final String INPUT_DIR ="C:\\opencv\\workfile\\att_img";
	public static final String OUTPUT_DIR ="C:\\opencv\\workfile\\att_img_png";
	public static void main(String arg[]){
		ImageReformatter.reformat(INPUT_DIR, OUTPUT_DIR, "png", false, 0, 0);
	}
	public static void reformat(String inputDir, String outputDir,String format,boolean greyScale,int w,int h) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File root = new File( inputDir );
		File[] list = root.listFiles();
		if (list == null) return;
		for ( File f : list ) {
			if ( f.isDirectory() ) {
				reformat( inputDir,outputDir,format,greyScale,w,h );
			}
			else {
				String filepath = f.getAbsolutePath();
				String filename = f.getName();
				Mat image = Imgcodecs.imread(filepath);
				String newFileName = "";
				if(format!=""){ //custom format
					newFileName = outputDir +"\\"+ filename.substring(0,filename.lastIndexOf('.')) + "." + format;
				}else{
					newFileName = outputDir +"\\"+ filename;
				}

				System.out.println(String.format("Writing[C] %s", newFileName));
				if(w!=0&&h!=0){
					Size size = new Size(w,h);
					Imgproc.resize(image, image, size);
				}
				if(greyScale){
					Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
				}
				Imgcodecs.imwrite(newFileName, image);
			}
		}
	}
}
