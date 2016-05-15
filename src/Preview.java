import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.awt.GridBagConstraints;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.event.ActionEvent;

public class Preview extends JFrame {

	private JPanel contentPane;
	private JComboBox listIMG;
	private JComboBox listXML;
	private JLabel output;
	private JLabel input;
	String filename ;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Preview frame = new Preview();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Preview() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel topPanel = new JPanel();
		contentPane.add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

		listIMG = new JComboBox();
		listIMG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				input.setIcon(new ImageIcon(listIMG.getSelectedItem().toString()));
				Detect(listXML.getSelectedItem().toString(),listIMG.getSelectedItem().toString());
			
			}
		});
		topPanel.add(listIMG);

		listXML = new JComboBox();
		listXML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setIcon(new ImageIcon(listIMG.getSelectedItem().toString()));
				Detect(listXML.getSelectedItem().toString(),listIMG.getSelectedItem().toString());

			}
		});

		topPanel.add(listXML);

		JPanel imagePanel = new JPanel();
		contentPane.add(imagePanel);
		imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.X_AXIS));

		JPanel panel = new JPanel();
		imagePanel.add(panel);
		JPanel panel_1 = new JPanel();
		imagePanel.add(panel_1);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));


		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);		

		input = new JLabel("");
		//	scrollPane.add(input);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1);

		output = new JLabel("");

		scrollPane.setViewportView(input);
		scrollPane_1.setViewportView(output);

		//	input.setIcon(new ImageIcon("55258.jpg"));
		//	output.setIcon(new ImageIcon("55258.jpg"));
		Collection<File> files = FileUtils.listFiles(new File("CascadeClassifier"), new String[]{"xml"}, true);
		//	System.out.println(Arrays.toString(f.toArray()));
		listXML.setModel(new DefaultComboBoxModel(files.toArray()));

		Collection<File> images = FileUtils.listFiles(new File("."), new String[]{"png","tif","tiff","gif","jpg","jpeg","jpe","jfif","bmp","dib"}, false);
		listIMG.setModel(new DefaultComboBoxModel(images.toArray()));
	}
	public void Detect(String xmlPath, String imgPath){
		System.out.println("DETECT: "+imgPath+" "+xmlPath);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CascadeClassifier faceDetector = new CascadeClassifier(xmlPath);
		Mat image = Highgui
				.imread(imgPath);

		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);

		System.out.println(String.format("Detected %s ", faceDetections.toArray().length));

		for (Rect rect : faceDetections.toArray()) {
			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
					new Scalar(0, 255, 0));
		}
		output.setIcon(new ImageIcon(Mat2BufferedImage(image)));
		filename = "output."+ FilenameUtils.getExtension(imgPath);
		//   System.out.println(filename);
		Highgui.imwrite(filename, image);

	}
	public BufferedImage Mat2BufferedImage(Mat m){
		// source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
		// Fastest code
		// The output can be assigned either to a BufferedImage or to an Image

		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels()*m.cols()*m.rows();
		byte [] b = new byte[bufferSize];
		m.get(0,0,b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);  
		return image;

	}
}
