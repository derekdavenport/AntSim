
//
//  Ant.java
//  Ant Sim
//
//  Created by Derek on Tue Oct 05 2004.
//  Copyright (c) 2004 Derek Davenport. All rights reserved.
//
import java.awt.*;
import java.util.*;

public class Ant extends OnScreen {
	protected double degree, speed;
	protected double dx, dy;
	protected int legPosition, positionHeld;
	protected final int TURN_BY = 5;
	protected boolean highlighted, dead;

	protected Point GoTo;
	protected Point KnownFoodPoint;
	protected final Point AntHole = new Point(320, 240);
	protected LinkedList ChemicalTrails;
	protected LinkedList Foods;

	protected Food FoundFood;
	protected int heldFoodAmount;
	protected int heldFoodType;
	protected int depositedFoodAmount;

	protected int minutesAlive;
	protected boolean hungry;
	protected boolean checkHunger;

	protected int currentAction;
	public final static int NO_ACTION = 0;
	public final static int GOTO_FOOD = 1;
	public final static int GOTO_POINT = 2;
	public final static int GATHER_FOOD = 3;
	public final static int TAKE_FOOD_HOME = 4;

	// Constructor that makes a new Ant at these x and y coordinates
	public Ant(int x, int y) {
		super(24, 24, x, y);

		degree = Math.random() * 360;
		speed = 0;
		legPosition = 0;
		positionHeld = 0;

		highlighted = false;
		dead = false;

		GoTo = new Point(0, 0);
		ChemicalTrails = new LinkedList();
		Foods = new LinkedList();

		heldFoodAmount = 0;
		depositedFoodAmount = 0;

		minutesAlive = 0;
		hungry = false;
		checkHunger = true;

		currentAction = NO_ACTION;
	}

	// returns ant strength depending on how long the ant has been alive
	protected int getStrength() {
		return (int) Math.floor(minutesAlive / 720);
	}

	protected double getMaxSpeed() {
		double output;
		switch (getStrength()) {
		case 0:
			output = 1;
			break;
		case 1:
			output = 1.5;
			break;
		case 2:
			output = 2;
			break;
		case 3:
			output = 4;
			break;
		default:
			output = 2;
		}

		return output;
	}

	protected int getMaxHoldableFood() {
		int output;
		switch (getStrength()) {
		case 0:
			output = 10;
			break;
		case 1:
			output = 15;
			break;
		case 2:
			output = 20;
			break;
		case 3:
			output = 30;
			break;
		default:
			output = 10;
		}

		return output;
	}

	protected int getQuadrant(double degree) {
		int quadrant;
		// in first quadrant
		if (degree >= 0 && degree <= 90) {
			quadrant = 1;
		} // in second quadrant
		else if (degree > 90 && degree <= 180) {
			quadrant = 2;
		}
		// in third quadrant
		else if (degree > 180 && degree <= 270) {
			quadrant = 3;
		}
		// in fourth quadrant
		else {
			quadrant = 4;
		}

		return quadrant;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void highlight() {
		highlighted = true;
	}

	public void unHighlight() {
		highlighted = false;
	}

	public void kill() {
		dead = true;
	}

	public double adjustDegree(double degree) {
		while (degree > 360) {
			degree -= 360;
		}

		while (degree < 0) {
			degree += 360;
		}

		return degree;
	}

	protected void setDegree(double degree) {
		this.degree = adjustDegree(degree);
	}

	// turns the ant to a degree, but won't turn the ant more than is defined by
	// TURN_BY
	protected void turnToDegree(double newDegree) {
		double diffDegree = adjustDegree(newDegree - degree);

		if (Math.abs(newDegree - degree) <= TURN_BY) {
			setDegree(newDegree);
		} else if (diffDegree < 180) {
			setDegree(degree + TURN_BY);
			accelerate(-0.11);
		} else {
			setDegree(degree - TURN_BY);
			accelerate(-0.11);
		}
	}

	protected void setSpeed(double speed) {
		if (speed < 0) {
			speed = 0;
		} else if (speed > getMaxSpeed()) {
			speed = getMaxSpeed();
		}

		this.speed = speed;
	}

	protected void accelerate(double accel) {
		double newSpeed = speed + accel;
		if (newSpeed < 0) {
			newSpeed = 0;
		}

		setSpeed(newSpeed);
	}

	// Ants are intelligent after they've been alive for 24 hours
	public boolean isIntelligent() {
		return minutesAlive >= 1440;
	}

	// Sets a point for this Ant to try to go to
	public void goToPoint(int x, int y) {
		if (!dead && isIntelligent()) {
			GoTo = new Point(x, y);
			currentAction = GOTO_POINT;
			FoundFood = null;
			KnownFoodPoint = null;
		}
	}

	// Sets a Food for this ant to go to
	// Note that this is not a public method and is only used internally
	protected void goToFood(Food F) {
		F.found();
		GoTo = new Point((int) F.getX(), (int) F.getY());
		FoundFood = F;
		currentAction = GOTO_FOOD;
	}

	// rounds the direction the ant is facing to the nearest image
	// legPosition
	public int getImageNum() {
		return (((int) Math.round(degree / 22.5) % 16) * 2) + legPosition;
	}

	public int getHeldFoodAmount() {
		return heldFoodAmount;
	}

	// returns an array telling what type of food the ant is holding and where to
	// draw it
	public int[] getFoodData() {
		double dx = 0;
		double dy = 0;
		int quadrant = getQuadrant(degree);
		// in first quadrant
		if (quadrant == 1) {
			double cosine = Math.cos(Math.toRadians(degree));
			dx = cosine * 11;
			dy = Math.sqrt(121 - (dx * dx));
		}
		// in second quadrant
		else if (quadrant == 2) {
			double cosine = Math.cos(Math.toRadians(180 - degree));
			dx = -(cosine * 11);
			dy = Math.sqrt(121 - (dx * dx));
		}
		// in third quadrant
		else if (quadrant == 3) {
			double cosine = Math.cos(Math.toRadians(degree - 180));
			dx = -(cosine * 11);
			dy = -(Math.sqrt(121 - (dx * dx)));
		}
		// in fourth quadrant
		else {
			double cosine = Math.cos(Math.toRadians(360 - degree));
			dx = cosine * 11;
			dy = -(Math.sqrt(121 - (dx * dx)));
		}

		int[] returnArray = { heldFoodType, (int) (x + dx - 4), (int) (y - dy - 4) };
		return returnArray;
	}

	public void setFoods(LinkedList F) {
		Foods = F;
	}

	public int getDepositedFood() {
		int returnValue = depositedFoodAmount;
		depositedFoodAmount = 0;
		return returnValue;
	}

	public void addMinuteToLife() {
		minutesAlive += 1;
	}

	public boolean isHungry() {
		return hungry;
	}

	public boolean isDead() {
		return dead;
	}

	public void feed() {
		hungry = false;
	}

	// Updates the position of the ant
	// Does all ant thinking
	public void updatePosition() {
		// Sees if the ant is 2 days old
		if (!dead && minutesAlive >= 2880) {
			dead = true;
		}

		if (!dead) {
			dx = 0;
			dy = 0;

			// Every 3 hours the ant will want food
			// If he doesn't get food in another 3 hours he dies of hunger
			if (checkHunger && minutesAlive % 180 == 0) {
				if (!hungry) {
					hungry = true;
				} else {
					dead = true;
				}
			}

			checkHunger = !checkHunger;

			// Swaps out the position of the legs in the ant animation (A to B to A...)
			positionHeld++;
			if (positionHeld > 3) {
				legPosition = (legPosition == 0) ? 1 : 0;
				positionHeld = 0;
			}

			Rectangle AntArea = getRect();

			// Sniffs for Food
			if (currentAction == NO_ACTION) {
				Food F;
				ListIterator I = Foods.listIterator(0);
				while ((currentAction == NO_ACTION) && I.hasNext()) {
					F = (Food) I.next();
					if (F.getScent().intersects(AntArea) || (F.isFound() && F.getChemicalTrail().intersects(AntArea))) {
						goToFood(F);
					}

				}
			}
			// If the ant has an action we see if he has arrived at his destination
			else if (AntArea.contains(GoTo)) {
				if (currentAction == GOTO_FOOD) {
					KnownFoodPoint = new Point((int) FoundFood.getX(), (int) FoundFood.getY());
					currentAction = GATHER_FOOD;
				} else if (currentAction == GATHER_FOOD) {
					if (heldFoodAmount < getMaxHoldableFood()) {
						if (FoundFood.hasFood()) {
							heldFoodAmount += FoundFood.takeFood(1);
							heldFoodType = FoundFood.getImageNum();
						} else {
							KnownFoodPoint = null;
							FoundFood = null;
							if (heldFoodAmount > 0) {
								GoTo = new Point(AntHole);
								currentAction = TAKE_FOOD_HOME;
							} else {
								currentAction = NO_ACTION;
							}
						}
					} else {
						if (!FoundFood.hasFood()) {
							KnownFoodPoint = null;
							FoundFood = null;
						}
						GoTo = new Point(AntHole);
						currentAction = TAKE_FOOD_HOME;
					}
				} else if (currentAction == TAKE_FOOD_HOME) {
					// add to food total;
					depositedFoodAmount += heldFoodAmount;
					heldFoodAmount = 0;

					if (KnownFoodPoint != null) {
						GoTo = new Point(KnownFoodPoint);
						currentAction = GOTO_FOOD;
					} else {
						currentAction = NO_ACTION;
					}
				} else if (currentAction == GOTO_POINT) {
					if (heldFoodAmount >= getMaxHoldableFood()) {
						GoTo = new Point(AntHole);
						currentAction = TAKE_FOOD_HOME;
					} else if (KnownFoodPoint != null) {
						GoTo = new Point(KnownFoodPoint);
						currentAction = GOTO_FOOD;
					} else {
						currentAction = NO_ACTION;
					}
				}
				speed = 0;

			} else if (currentAction == GATHER_FOOD) {
				currentAction = NO_ACTION;
			}

			accelerate(0.1);

			// If the ant has somewhere to go, make him turn there
			if ((currentAction == GOTO_FOOD) || (currentAction == GOTO_POINT) || (currentAction == TAKE_FOOD_HOME)) {
				double opp = Math.abs(GoTo.getY() - y);
				double adj = Math.abs(GoTo.getX() - x);
				double newDegree = Math.toDegrees(Math.atan(opp / adj));
				// in first quadrant
				if (GoTo.getX() > x && GoTo.getY() < y) {
					turnToDegree(newDegree);
				}
				// in second quadrant
				else if (GoTo.getX() < x && GoTo.getY() < y) {
					turnToDegree(180 - newDegree);
				}
				// in third quadrant
				else if (GoTo.getX() < x && GoTo.getY() > y) {
					turnToDegree(180 + newDegree);
				}
				// in fourth quadrant
				else {
					turnToDegree(360 - newDegree);
				}
			}
			// If the ant has nowhere to go (no action) he just moves randomly
			else {
				turnToDegree(this.degree + (Math.random() * TURN_BY * 4 - TURN_BY * 2));
				accelerate(Math.random() - 0.5);
			}

			// Calculates the x and y coordinate to draw the ant at
			int quadrant = getQuadrant(degree);
			// in first quadrant
			if (quadrant == 1) {
				double cosine = Math.cos(Math.toRadians(degree));
				dx = cosine * speed;
				dy = Math.sqrt((speed * speed) - (dx * dx));
			}
			// in second quadrant
			else if (quadrant == 2) {
				double cosine = Math.cos(Math.toRadians(180 - degree));
				dx = -(cosine * speed);
				dy = Math.sqrt((speed * speed) - (dx * dx));
			}
			// in third quadrant
			else if (quadrant == 3) {
				double cosine = Math.cos(Math.toRadians(degree - 180));
				dx = -(cosine * speed);
				dy = -(Math.sqrt((speed * speed) - (dx * dx)));
			}
			// in fourth quadrant
			else {
				double cosine = Math.cos(Math.toRadians(360 - degree));
				dx = cosine * speed;
				dy = -(Math.sqrt((speed * speed) - (dx * dx)));
			}
		}

		double newX = x + dx;
		double newY = y - dy;

		// Make sure the ant doesn't go off the screen
		// If he hits the edge make him turn
		if (newX < 0) {
			this.speed = 0;
			if (degree >= 180) {
				setDegree(degree + TURN_BY);
			} else {
				setDegree(degree - TURN_BY);
			}
		} else if (newX > 640) {
			speed = 0;
			if (degree <= 90) {
				setDegree(degree + TURN_BY);
			} else {
				setDegree(degree - TURN_BY);
			}
		} else if (newY < 0) {
			speed = 0;
			if (degree >= 90) {
				setDegree(degree + TURN_BY);
			} else {
				setDegree(degree - TURN_BY);
			}
		} else if (newY > 480) {
			speed = 0;
			if (degree >= 270) {
				setDegree(degree + TURN_BY);
			} else {
				setDegree(degree - TURN_BY);
			}
		} else {
			x = newX;
			y = newY;
		}
	}

	// returns a String representing this ant's state
	public String toString() {
		String outputString = "";
		if (dead) {
			outputString = "Dead";
		} else {
			// int days = (int)Math.floor(minutesAlive / 1440);
			int hours = (int) Math.floor(minutesAlive / 60);
			// int minutes = minutesAlive % 60;
			String hunger = (hungry) ? "Yes" : "No";
			String intel = (isIntelligent()) ? "Yes" : "No";
			String action;
			switch (currentAction) {
			case GOTO_FOOD:
				action = "going to food";
				break;
			case GOTO_POINT:
				action = "going to point";
				break;
			case GATHER_FOOD:
				action = "picking up food";
				break;
			case TAKE_FOOD_HOME:
				action = "taking food home";
				break;
			default:
				action = "searching";
			}

			outputString = "Alive: " + hours + " hours; Hungry: " + hunger + "; Intelligent: " + intel + "; Food: "
					+ heldFoodAmount + "; Action: " + action + ";";
		}
		return outputString;
	}
}
