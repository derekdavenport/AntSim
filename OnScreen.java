
//
//  OnScreen.java
//  Ant Simulation
//
//  Created by Derek on Thu Sep 23 2004.
//  Copyright (c) 2004 Derek Davenport. All rights reserved.
//

import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class OnScreen {
	protected double x, y;
	protected int w, h;
	protected Rectangle Rect;

	public OnScreen(int w, int h, int x, int y) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		Rect = new Rectangle(this.w, this.h);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	// our x and y coordinates refer to the center of the object (for the coder's
	// sake)
	// Java draws images from the top-left pixel so we must
	// transcribe by half the width and height to get the correct
	// coordinate to be drawn to
	public int getDrawnX() {
		return (int) (x - (w / 2));
	}

	public int getDrawnY() {
		return (int) (y - (h / 2));
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

	// returns a binding rectangle
	public Rectangle getRect() {
		Rect.setLocation(getDrawnX(), getDrawnY());
		return Rect;
	}
}
