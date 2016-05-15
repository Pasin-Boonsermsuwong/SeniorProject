import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ProgressPanel extends JPanel{
	Color c = Color.GREEN;
	double value;
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);       
	    g.setColor(c);
	    g.fillRect(0, 0, (int) (this.getWidth()*value), this.getHeight()-1);
	}
	public void setColor(Color c){
		this.c = c;
		repaint();
	}
	public void setValue(double d){
		value = d;
	}
}
