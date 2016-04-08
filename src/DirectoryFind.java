import javax.swing.JFrame;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public class DirectoryFind extends JFrame {

	public static void main(String[] args) {
		Collection<File> images = FileUtils.listFiles(new File("C://opencv//build//install//x64//vc14//lib"), new String[]{"lib"}, false);
		Object[] ob = images.toArray();
		for(Object o:ob){
			String s = o.toString();
			System.out.println(s.substring(s.lastIndexOf('\\')+1, s.length()));
			//System.out.println(s.substring(s.lastIndexOf('\\')+1, s.lastIndexOf('.'))+"d"+s.substring(s.lastIndexOf('.'), s.length()));
		}
	}

}
