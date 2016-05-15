import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.ArrayUtils;
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
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javafx.scene.control.ComboBox;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.Component;
import java.awt.Desktop;

import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;

public class Gui_ImageManager extends JFrame {

	private JPanel contentPane;
	private JTextField textWidth;
	private JTextField textHeight;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	ImageManager im;
	private JCheckBox c1;
	private JCheckBox c2;
	private JCheckBox c3;
	private JCheckBox c4;
	private JCheckBox c5;
	private JComboBox comboBox;
	private JLabel labelPic;
	private JLabel labelFace;
	private ProgressPanel panel_4;
	private JLabel info;
	int tempCount;
	int fileCount;
	int faceCount;
	double mainImgCount;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui_ImageManager frame = new Gui_ImageManager();
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
	public Gui_ImageManager() {
		im = new ImageManager();
		im.readProcessedImages();
		im.gui = this;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 693, 434);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		JLabel lblNewLabel_4 = new JLabel("Image Folder");
		panel_1.add(lblNewLabel_4);

		Component rigidArea_5 = Box.createRigidArea(new Dimension(51, 20));
		panel_1.add(rigidArea_5);

		textField = new JTextField();
		panel_1.add(textField);
		textField.setColumns(10);

		JButton btnNewButton_4 = new JButton("Browse");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(textField.getText()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel_1.add(btnNewButton_4);

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));

		JLabel lblNewLabel_5 = new JLabel("Output Folder");
		panel_2.add(lblNewLabel_5);

		Component rigidArea_4 = Box.createRigidArea(new Dimension(47, 20));
		panel_2.add(rigidArea_4);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		panel_2.add(textField_1);

		JButton btnNewButton_3 = new JButton("Browse");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(textField_1.getText()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel_2.add(btnNewButton_3);

		JPanel panel_5 = new JPanel();
		contentPane.add(panel_5);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));

		JLabel lblNewLabel_6 = new JLabel("Cropped Face Folder  ");
		panel_5.add(lblNewLabel_6);

		textField_2 = new JTextField();
		textField_2.setColumns(10);
		panel_5.add(textField_2);

		JButton btnNewButton_2 = new JButton("Browse");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(textField_2.getText()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel_5.add(btnNewButton_2);

		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_c = new JPanel();
		panel.add(panel_c, BorderLayout.CENTER);

		JButton btnNewButton_1 = new JButton("Process Image");
		btnNewButton_1.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton_1.setEnabled(false);
				Executors.newSingleThreadExecutor().execute(new Runnable() {
					public void run() {
						try {
							updateStat();
							mainImgCount = 0;
							int w = 0;	int h = 0;
							try {
								w = Integer.parseInt(textWidth.getText());
								h = Integer.parseInt(textHeight.getText());
							} catch (NumberFormatException e1) {
								w = 0;h = 0;
								textWidth.setText("0");textHeight.setText("0");
							}
							im.processImage(textField.getText(), c1.isSelected(), c2.isSelected(), c3.isSelected(), c4.isSelected(), c5.isSelected(), w, h, comboBox.getSelectedItem().toString(),true);
							updateStat();
							ProgressUpdate(false,null);
							btnNewButton_1.setEnabled(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		panel_c.setLayout(new BoxLayout(panel_c, BoxLayout.Y_AXIS));

		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		panel_c.add(rigidArea);
		panel_c.add(btnNewButton_1);

		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		panel_c.add(rigidArea_1);

		JButton btnNewButton = new JButton("Clear Caches");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				im.clearCaches();
				updateStat();
				info.setText("Caches cleared");
			}
		});
		btnNewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_c.add(btnNewButton);

		Component rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
		panel_c.add(rigidArea_2);

		info = new JLabel("");
		info.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_c.add(info);

		Component rigidArea_3 = Box.createRigidArea(new Dimension(20, 20));
		panel_c.add(rigidArea_3);

		panel_4 = new ProgressPanel();
		panel_4.setBackground(Color.WHITE);
		panel_c.add(panel_4);

		Component verticalGlue = Box.createVerticalGlue();
		panel_c.add(verticalGlue);

		JPanel panel_e = new JPanel();
		panel.add(panel_e, BorderLayout.EAST);
		panel_e.setLayout(new BoxLayout(panel_e, BoxLayout.Y_AXIS));

		c1 = new JCheckBox("Save pre-process");
		panel_e.add(c1);

		c2 = new JCheckBox("Create output");
		c2.setSelected(true);
		panel_e.add(c2);

		c3 = new JCheckBox("Highlight features");
		panel_e.add(c3);

		c4 = new JCheckBox("Create cropped");
		c4.setSelected(true);
		panel_e.add(c4);

		c5 = new JCheckBox("Highlight keypoints");
		panel_e.add(c5);

		JPanel panel_3 = new JPanel();
		panel_3.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_e.add(panel_3);
		panel_3.setLayout(new GridLayout(2, 2, 0, 0));

		JLabel lblNewLabel = new JLabel("Width");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel_3.add(lblNewLabel);

		textWidth = new JTextField();
		textWidth.setText("100");
		panel_3.add(textWidth);
		textWidth.setColumns(5);

		JLabel lblNewLabel_1 = new JLabel("Height");
		panel_3.add(lblNewLabel_1);

		textHeight = new JTextField();
		textHeight.setText("100");
		panel_3.add(textHeight);
		textHeight.setColumns(5);

		comboBox = new JComboBox(new String[]{"png","bmp","jpg","jp2","webp","pgm","sr","tif"});
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_e.add(comboBox);

		Component verticalGlue_1 = Box.createVerticalGlue();
		panel_e.add(verticalGlue_1);

		JPanel panel_w = new JPanel();
		panel.add(panel_w, BorderLayout.WEST);
		panel_w.setLayout(new BoxLayout(panel_w, BoxLayout.Y_AXIS));

		JLabel Stat = new JLabel("Statistics");
		Stat.setFont(new Font("Tahoma", Font.BOLD, 20));
		panel_w.add(Stat);

		labelPic = new JLabel("Total number of pictures: 0");
		panel_w.add(labelPic);

		labelFace = new JLabel("Total number of faces detected: 0");
		panel_w.add(labelFace);


		textField.setText(im.PATH_IMAGE_FOLDER);
		textField_1.setText(im.PATH_OUTPUT);
		textField_2.setText(im.PATH_OUTPUT_CROPPED);

		updateStat();
	}
	void updateStat(){
		fileCount = countResursive(textField.getText());
		faceCount = countResursive(textField_1.getText());
		labelPic.setText("Total number of pictures: "+fileCount);
		labelFace.setText("Total number of faces: "+faceCount);
	}
	public void ProgressUpdate(boolean mainImg,String s){
		System.out.println("progressUpdate: "+s);
		if(s==null){
			panel_4.setValue(0.0);
			info.setText("");
			return;
		}
		if(mainImg){
			mainImgCount++;
			double value = mainImgCount/fileCount;
			System.out.println("value: "+value);
			panel_4.setValue(value);
		}
		info.setText(s);
		this.repaint();
	}
	public int countResursive(String path){
		tempCount = 0;
		countRecursive2(path);
		return tempCount;
	}
	private void countRecursive2(String path) {
		File root = new File( path );
		File[] list = root.listFiles();
		if (list == null) return;
		for ( File f : list ) {
			if ( f.isDirectory() ) {
				countRecursive2(f.getAbsolutePath());
			}
			else {
				tempCount++;
			}
		}
	}
}