import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.opencv.imgcodecs.Imgcodecs;

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

public class Preview extends JFrame {

	private JPanel contentPane;

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
		
		JComboBox listIMG = new JComboBox();
		topPanel.add(listIMG);
		
		JComboBox listXML = new JComboBox();
	
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
		
		JLabel input = new JLabel("");
	//	scrollPane.add(input);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1);
		
		JLabel output = new JLabel("");
		
		scrollPane.setViewportView(input);
		scrollPane_1.setViewportView(output);

	//	input.setIcon(new ImageIcon("55258.jpg"));
	//	output.setIcon(new ImageIcon("55258.jpg"));
		Collection<File> files = FileUtils.listFiles(new File("CascadeClassifier"), new String[]{"xml"}, true);
	//	System.out.println(Arrays.toString(f.toArray()));
		listXML.setModel(new DefaultComboBoxModel(files.toArray()));
		
		Collection<File> images = FileUtils.listFiles(new File(""), new String[]{"png","tif","tiff","gif","jpg","jpeg","jpe","jfif","bmp","dib"}, false);
		listIMG.setModel(new DefaultComboBoxModel(images.toArray()));
	}
	public static Image Detect(String xmlPath, Image image){
		return null;
	}
}
