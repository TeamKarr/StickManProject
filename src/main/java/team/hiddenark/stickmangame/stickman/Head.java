package team.hiddenark.stickmangame.stickman;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

class Head{
	public Vector2d pos;
	private Joint parent;
	private double theta;
	private boolean headType;
	public void setTheta(double theta) {
		this.theta=theta;
	}
	public Head(Joint parent, boolean headType) {
		this.parent = parent;
		this.headType = headType;
	}
	public void draw(Graphics2D window) {
		window.setStroke(new BasicStroke(7));
		int length = parent.getLength();
		this.pos=parent.pos;
		
		if(headType) {
			int radius=18;
			window.fillOval((int)(pos.x-radius+17*Math.cos(parent.getTheta()+theta)), (int)(pos.y-radius+radius*Math.sin(parent.getTheta()+theta)), 2*radius, 2*radius);
		}else {
			int radius=20;
			window.drawOval((int)(pos.x-radius+(radius+2)*Math.cos(parent.getTheta()+theta)), (int)(pos.y-radius+(radius+2)*Math.sin(parent.getTheta()+theta)), radius*2, radius*2);
	}}
}