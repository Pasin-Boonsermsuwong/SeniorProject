import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.opencv.core.Core;
import org.opencv.core.MatOfKeyPoint;
@XmlRootElement
public class ImgDataList {
	public ArrayList<ImgData> list;
	public ImgDataList(){
		list = new ArrayList<ImgData>();
	}

	public static void main(String args[]){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try {

			File file = new File(ImageManager.PATH_FEATURE_OUTPUT);
			JAXBContext jaxbContext = JAXBContext.newInstance(ImgDataList.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			ImgDataList data = (ImgDataList) jaxbUnmarshaller.unmarshal(file);
			for(ImgData d:data.list){
				System.out.println(d.keyPoints.size());
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}


	}
}
