package tripleTriad;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class Mouse extends MouseAdapter{
	
	public int x, y;
	public boolean pressed;
	private JPanel window;
	
	
	public Mouse(JPanel window) {
		this.window = window;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		pressed = true;
		//window.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
		//window.repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		//window.repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		//SwingUtilities.invokeLater(() -> window.repaint());
		//window.repaint();
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.black);
		g.drawString("Mouse Coordinates: (" + this.x + ", " + this.y + ")", 400, 20);
		
		if(this.pressed) {
			g.drawString("Mouse is pressed", 400, 50);
		}
		else {
			g.drawString("Mouse is released", 400, 50);
		}
	}

}
