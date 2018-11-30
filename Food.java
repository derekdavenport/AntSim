
//
//  Food.java
//  Ant Sim
//
//  Created by Derek on Sun Oct 24 2004.
//  Copyright (c) 2004 Derek Davenport. All rights reserved.
//
import java.awt.*;
import java.util.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;

//abstract superclass to all food
public abstract class Food extends OnScreen {
	protected int foodParts;
	protected Ellipse2D.Double Scent;
	protected final int smellRadius = 100;
	protected Line2D.Double ChemicalTrail;
	protected boolean found;

	public Food(int fP, int w, int h, int x, int y) {
		super(w, h, x, y);
		foodParts = fP;
		Scent = new Ellipse2D.Double(x - smellRadius, y - smellRadius, smellRadius * 2, smellRadius * 2);
		ChemicalTrail = new Line2D.Double(x, y, 320, 240);
		found = false;
	}

	public boolean hasFood() {
		return foodParts > 0;
	}

	public int takeFood(int amount) {
		int givenAmount;

		if (foodParts >= amount) {
			foodParts -= amount;
			givenAmount = amount;
		} else {
			givenAmount = foodParts;
			foodParts = 0;
		}

		return givenAmount;
	}

	public Ellipse2D.Double getScent() {
		return Scent;
	}

	public void found() {
		found = true;
	}

	public boolean isFound() {
		return found;
	}

	public Line2D.Double getChemicalTrail() {
		return ChemicalTrail;
	}

	// force subclasses to provide an image number
	public abstract int getImageNum();

}
