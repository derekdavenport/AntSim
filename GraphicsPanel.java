
//
//  GraphicsPanel.java
//  Ant Sim
//
//  Created by Derek on Thu Oct 07 2004.
//  Copyright (c) 2004 Derek Davenport. All rights reserved.
//
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;

public class GraphicsPanel extends Panel implements MouseListener, MouseMotionListener, Runnable {
	private int currentAction;
	public static final int NO_ACTION = 0;
	public static final int SPAWN_ANT = 1;
	public static final int SPAWN_FOOD = 3;
	public static final int SET_GOTO = 5;
	public static final int HIGHLIGHT_MODE = 10;
	public static final int COOKIE = 0;
	public static final int BREAD = 1;
	public static final int POPCORN = 2;

	private AntSim Parent;
	private Ellipse2D.Double NoDropZone;

	int delay;
	private Thread userFoodAmountanimator;

	private boolean minuteChange;
	private int day;
	private int hour;
	private int minute;
	private String errorMsg;
	private int errorMsgCounter;

	private int userFoodAmount;
	private int collectedFoodAmount;

	private int winCounter;
	private boolean win;

	private Dimension ScreenSize;

	// Stuff used for double buffering
	private Dimension offDimension;
	private Image offImage;
	private Graphics offGraphics;

	private LinkedList Ants;
	private LinkedList SelectedAnts;
	private LinkedList Foods;

	private Rectangle SelectionRect;

	private Toolkit toolkit;
	private Image BackgroundImage;
	private Image[] AntImages;
	private Image[] FoodImages;
	private Image[] FoodPieceImages;
	private MediaTracker ImageTracker;

	private Thread animator;

	public GraphicsPanel(AntSim P) {
		super();

		Parent = P;

		currentAction = NO_ACTION;

		Ants = new LinkedList();

		Ant newAnt = new Ant(320, 240);
		newAnt.setDegree(Math.random() * 360);
		Ants.add(newAnt);
		newAnt = new Ant(310, 230);
		newAnt.setDegree(Math.random() * 360);
		Ants.add(newAnt);
		newAnt = new Ant(330, 250);
		newAnt.setDegree(Math.random() * 360);
		Ants.add(newAnt);
		newAnt = new Ant(325, 235);
		newAnt.setDegree(Math.random() * 360);
		Ants.add(newAnt);
		newAnt = new Ant(315, 245);
		newAnt.setDegree(Math.random() * 360);
		Ants.add(newAnt);

		SelectedAnts = new LinkedList();
		Foods = new LinkedList();

		// Make the thread
		animator = new Thread(this);

		addMouseListener(this);
		addMouseMotionListener(this);

		ScreenSize = new Dimension(640, 480);

		SelectionRect = new Rectangle();

		toolkit = Toolkit.getDefaultToolkit();

		BackgroundImage = toolkit.createImage("resources/background.png");
		ImageTracker = new MediaTracker(this);
		ImageTracker.addImage(BackgroundImage, 0);

		AntImages = new Image[32];
		AntImages[0] = toolkit.createImage("resources/ant0-A.png");
		AntImages[1] = toolkit.createImage("resources/ant0-B.png");
		AntImages[2] = toolkit.createImage("resources/ant22.5-A.png");
		AntImages[3] = toolkit.createImage("resources/ant22.5-B.png");
		AntImages[4] = toolkit.createImage("resources/ant45-A.png");
		AntImages[5] = toolkit.createImage("resources/ant45-B.png");
		AntImages[6] = toolkit.createImage("resources/ant67.5-A.png");
		AntImages[7] = toolkit.createImage("resources/ant67.5-B.png");
		AntImages[8] = toolkit.createImage("resources/ant90-A.png");
		AntImages[9] = toolkit.createImage("resources/ant90-B.png");
		AntImages[10] = toolkit.createImage("resources/ant112.5-A.png");
		AntImages[11] = toolkit.createImage("resources/ant112.5-B.png");
		AntImages[12] = toolkit.createImage("resources/ant135-A.png");
		AntImages[13] = toolkit.createImage("resources/ant135-B.png");
		AntImages[14] = toolkit.createImage("resources/ant157.5-A.png");
		AntImages[15] = toolkit.createImage("resources/ant157.5-B.png");
		AntImages[16] = toolkit.createImage("resources/ant180-A.png");
		AntImages[17] = toolkit.createImage("resources/ant180-B.png");
		AntImages[18] = toolkit.createImage("resources/ant202.5-A.png");
		AntImages[19] = toolkit.createImage("resources/ant202.5-B.png");
		AntImages[20] = toolkit.createImage("resources/ant225-A.png");
		AntImages[21] = toolkit.createImage("resources/ant225-B.png");
		AntImages[22] = toolkit.createImage("resources/ant247.5-A.png");
		AntImages[23] = toolkit.createImage("resources/ant247.5-B.png");
		AntImages[24] = toolkit.createImage("resources/ant270-A.png");
		AntImages[25] = toolkit.createImage("resources/ant270-B.png");
		AntImages[26] = toolkit.createImage("resources/ant292.5-A.png");
		AntImages[27] = toolkit.createImage("resources/ant292.5-B.png");
		AntImages[28] = toolkit.createImage("resources/ant315-A.png");
		AntImages[29] = toolkit.createImage("resources/ant315-B.png");
		AntImages[30] = toolkit.createImage("resources/ant337.5-A.png");
		AntImages[31] = toolkit.createImage("resources/ant337.5-B.png");
		for (int i = 0; i < AntImages.length; i++) {
			ImageTracker.addImage(AntImages[i], i + 1);
		}

		FoodImages = new Image[3];
		FoodImages[0] = toolkit.createImage("resources/cookie.png");
		FoodImages[1] = toolkit.createImage("resources/bread.png");
		FoodImages[2] = toolkit.createImage("resources/popcorn.png");
		for (int i = 0; i < FoodImages.length; i++) {
			ImageTracker.addImage(FoodImages[i], AntImages.length + i);
		}

		FoodPieceImages = new Image[3];
		FoodPieceImages[0] = toolkit.createImage("resources/cookie piece.png");
		FoodPieceImages[1] = toolkit.createImage("resources/bread crumb.png");
		FoodPieceImages[2] = toolkit.createImage("resources/popcorn piece.png");
		for (int i = 0; i < FoodPieceImages.length; i++) {
			ImageTracker.addImage(FoodPieceImages[i], AntImages.length + FoodImages.length + i);
		}

		try {
			ImageTracker.waitForAll();
		} catch (InterruptedException e) {
			System.err.println(e);
			System.exit(1);
		}

		setSize(640, 480);

		setVisible(true);

		// start the thread
		delay = 33;
		minuteChange = false;
		day = 0;
		hour = 6;
		minute = 0;

		userFoodAmount = 0;
		collectedFoodAmount = 15;

		winCounter = 0;
		win = false;

		errorMsg = "";
		errorMsgCounter = -1;

		// no dropping food in the ant hole area
		NoDropZone = new Ellipse2D.Double(200, 120, 240, 240);
		animator.start();
	}

	public void spawnAnt() {
		if (collectedFoodAmount >= 100) {
			collectedFoodAmount -= 100;
			Ant newAnt = new Ant(320, 240);
			Ants.add(newAnt);
		} else {
			makeError("Not enough food");
		}

		currentAction = NO_ACTION;
	}

	public void killAnt() {
		Ant A;
		ListIterator I = SelectedAnts.listIterator(0);

		while (I.hasNext()) {
			A = (Ant) I.next();
			A.kill();
		}

		currentAction = NO_ACTION;
	}

	public void setGoTo() {
		if (SelectedAnts.size() > 0) {
			currentAction = SET_GOTO;
		} else {
			currentAction = NO_ACTION;
			makeError("Select some intelligent ants first");
		}
	}

	public void spawnFood() {
		if (userFoodAmount == 0) {
			currentAction = NO_ACTION;
			makeError("Not enough food");
		} else {
			currentAction = SPAWN_FOOD;
		}
	}

	public void cancelAction() {
		currentAction = NO_ACTION;
	}

	// MOUSE LISTENING //
	public void mouseClicked(MouseEvent e) {
		int clickedX = e.getX();
		int clickedY = e.getY();

		if (currentAction != NO_ACTION) {
			// Sets the GoTo Point for all selected Ants
			if (currentAction == SET_GOTO) {
				Ant A;
				ListIterator I = Ants.listIterator(0);

				while (I.hasNext()) {
					A = (Ant) I.next();
					if (A.isHighlighted()) {
						A.goToPoint(clickedX, clickedY);
					}
				}
				currentAction = NO_ACTION;
			}
			// Makes a new Food Object where the uses clicks
			// Food is random unless there is not enough food for the more costly food types
			// then it defaults to the next lowest food type
			// You cannot place new food near the anthill or in the scent of other food
			// items
			else if (currentAction == SPAWN_FOOD) {
				if (userFoodAmount == 0) {
					currentAction = NO_ACTION;
					makeError("Not enough food");
				} else {
					Food F;
					Food newFood = null;

					ListIterator I = Foods.listIterator(0);
					Rectangle TestArea = new Rectangle(clickedX - 25, clickedY - 25, 50, 50);

					boolean inScent = NoDropZone.intersects(TestArea);

					String error = "Can't place food there: too close to ";
					error += (inScent) ? "anthill" : "other food";

					while (!inScent && I.hasNext()) {
						F = (Food) I.next();
						if (F.getScent().intersects(TestArea)) {
							inScent = true;
						}
					}

					// If the user didn't click near other food then we can make a new food
					if (!inScent) {
						int rand = (int) Math.floor(Math.random() * 3);

						if (rand == POPCORN) {
							if (userFoodAmount >= Popcorn.FOOD_AMOUNT) {
								userFoodAmount -= Popcorn.FOOD_AMOUNT;
								newFood = new Popcorn(clickedX, clickedY);
							} else if (userFoodAmount >= Cookie.FOOD_AMOUNT) {
								userFoodAmount -= Cookie.FOOD_AMOUNT;
								newFood = new Cookie(clickedX, clickedY);
							} else if (userFoodAmount >= Bread.FOOD_AMOUNT) {
								userFoodAmount -= Bread.FOOD_AMOUNT;
								newFood = new Bread(clickedX, clickedY);
							}
						} else if (rand == COOKIE) {
							if (userFoodAmount >= Cookie.FOOD_AMOUNT) {
								userFoodAmount -= Cookie.FOOD_AMOUNT;
								newFood = new Cookie(clickedX, clickedY);
							} else if (userFoodAmount >= Bread.FOOD_AMOUNT) {
								userFoodAmount -= Bread.FOOD_AMOUNT;
								newFood = new Bread(clickedX, clickedY);
							}
						} else {
							if (userFoodAmount >= Bread.FOOD_AMOUNT) {
								userFoodAmount -= Bread.FOOD_AMOUNT;
								newFood = new Bread(clickedX, clickedY);
							}
						}

						if (newFood != null) {
							Foods.add(newFood);
						}

						currentAction = NO_ACTION;
					} else {
						makeError(error);
					}
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		// right-clicking is considered a setGoTo command
		if (e.getButton() == e.BUTTON3) {
			currentAction = SET_GOTO;
		}

		// starts highlight mode
		if (currentAction == NO_ACTION) {
			SelectionRect.setLocation(e.getX(), e.getY());
			SelectionRect.setSize(0, 0);
			currentAction = HIGHLIGHT_MODE;
		}
	}

	// if in highlight mode we see what ants are inside the selection box
	public void mouseReleased(MouseEvent e) {
		if (currentAction == HIGHLIGHT_MODE) {
			SelectedAnts.clear();

			Rectangle TestSelRect;
			boolean noDrag = false;
			boolean foundOne = false;

			int SRx = (int) SelectionRect.getX();
			int SRy = (int) SelectionRect.getY();
			int SRw = (int) SelectionRect.getWidth();
			int SRh = (int) SelectionRect.getHeight();
			if (SRw != 0 && SRh != 0) {
				if (SRw < 0) {
					SRw *= -1;
					SRx -= SRw;
				}
				if (SRh < 0) {
					SRh *= -1;
					SRy -= SRh;
				}

				SelectionRect.setBounds(SRx, SRy, SRw, SRh);
			} else {
				SelectionRect.setSize(1, 1);
				noDrag = true;
			}

			Ant A;
			ListIterator I = Ants.listIterator(Ants.size());
			int i = 0;

			while (I.hasPrevious()) {
				A = (Ant) I.previous();
				Rectangle TestRect = A.getRect();
				if (!foundOne && SelectionRect.intersects(TestRect)) {
					SelectedAnts.add(A);
					A.highlight();
					if (noDrag) {
						foundOne = true;
					}
					i++;
				} else {
					SelectedAnts.remove(A);
					A.unHighlight();
				}
			}

			// Parent.setStatOutputField(i + " ant(s) selected.");
			SelectionRect.setSize(0, 0);
			currentAction = NO_ACTION;
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (currentAction == HIGHLIGHT_MODE) {
			SelectionRect.setSize((int) (e.getX() - SelectionRect.getX()), (int) (e.getY() - SelectionRect.getY()));
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void makeError(String msg) {
		errorMsg = msg;
		errorMsgCounter = 50;
	}

	// Gets called when you start the Thread and after the Thread finishes
	public void run() {
		long tm = System.currentTimeMillis(); // Time this tick of the thread started
		while (Thread.currentThread() == animator) // Just make sure this is the only Thread running
		{
			repaint(); // repaint() calls the update() method

			// exception catching
			try {
				tm += delay; // Set the time this tick should end
				Thread.sleep(Math.max(0, tm - System.currentTimeMillis())); // Put the thread to sleep until then
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	// called by repaint()
	public void update(Graphics g) {
		// Get size of this applet
		Dimension d = ScreenSize;

		// Create an offscreen image to draw too
		if ((offGraphics == null) || (d.width != offDimension.width) || (d.height != offDimension.height)) {
			offDimension = d;
			offImage = createImage(d.width, d.height);
			offGraphics = offImage.getGraphics();
		}

		// Erase the previous image
		offGraphics.drawImage(BackgroundImage, 0, 0, null);

		// Paint the frame into the image
		paintFrame(offGraphics);

		// Paint the image onto the screen
		g.drawImage(offImage, 0, 0, null);
	}

	// Gets called if something (like another window) gets infront of the applet and
	// is then moved away
	public void paint(Graphics g) {
		// Just paint whatever's in the offscreen image
		if (offImage != null) {
			g.drawImage(offImage, 0, 0, null);
		}
	}

	// Called by Update. Does all the nasty calculations
	public void paintFrame(Graphics g) {
		g.setColor(Color.BLACK);

		// Gives the use 350 food at 6, 12, and 18 o'clock every day
		if (!minuteChange && minute == 0 && (hour == 6 || hour == 12 || hour == 18)) {
			userFoodAmount += 350;
		}

		// if this is a minuteChange tick then we change the minute
		if (minuteChange) {
			minute += 1;
			if (minute >= 60) {
				minute = 0;
				hour += 1;
				if (hour >= 24) {
					hour = 0;
					day += 1;
				}
			}
		}

		// Draw the Foods
		Food F;
		ListIterator I = Foods.listIterator(0);
		while (I.hasNext()) {
			F = (Food) I.next();
			if (F.hasFood()) {
				// g.drawOval((int)F.getScent().getX(), (int)F.getScent().getY(),
				// (int)F.getScent().getWidth(), (int)F.getScent().getHeight());
				g.drawImage(FoodImages[F.getImageNum()], F.getDrawnX(), F.getDrawnY(), null);
			} else {
				I.remove();
			}
		}

		// Draw the Ants
		Ant A;
		I = Ants.listIterator(0);
		int aliveAnts = 0;

		while (I.hasNext()) {
			A = (Ant) I.next();
			if (!A.isDead()) {
				A.setFoods(Foods);
				A.updatePosition();
				collectedFoodAmount += A.getDepositedFood();

				if (minuteChange) {
					A.addMinuteToLife();
				}

				if (A.isHungry() && collectedFoodAmount >= 3) {
					collectedFoodAmount -= 3;
					A.feed();
				}

				aliveAnts++;
			}

			g.drawImage(AntImages[A.getImageNum()], A.getDrawnX(), A.getDrawnY(), null);

			if (A.getHeldFoodAmount() > 0) {
				int[] foodData = A.getFoodData();
				g.drawImage(FoodPieceImages[foodData[0]], foodData[1], foodData[2], null);
			}

			if (A.isHighlighted()) {
				Color OldColor = g.getColor();
				g.setColor(Color.GREEN);
				Rectangle HighlightBox = A.getRect();
				g.drawRect((int) HighlightBox.getX(), (int) HighlightBox.getY(), (int) HighlightBox.getWidth(),
						(int) HighlightBox.getHeight());
				g.setColor(OldColor);
			}
		}

		// Draw the selection rectangle
		// For negative values of width and height we must transcribe the drawn
		// rectangle
		int SRx = (int) SelectionRect.getX();
		int SRy = (int) SelectionRect.getY();
		int SRw = (int) SelectionRect.getWidth();
		int SRh = (int) SelectionRect.getHeight();
		if (SRw != 0 && SRh != 0) {
			if (SRw < 0) {
				SRw *= -1;
				SRx -= SRw;
			}
			if (SRh < 0) {
				SRh *= -1;
				SRy -= SRh;
			}

			g.setColor(Color.GREEN);
			g.drawRect(SRx, SRy, SRw, SRh);
		}

		// Print out current status to the status bar
		String outputString = "";
		if (errorMsgCounter > 0) {
			outputString = errorMsg;
			errorMsgCounter--;
		} else if (SelectedAnts.size() == 1) {
			A = (Ant) SelectedAnts.getFirst();
			outputString = A.toString();
		} else if (currentAction != NO_ACTION) {
			switch (currentAction) {
			case SPAWN_ANT:
				outputString = "Spawn Ant";
				break;
			case SPAWN_FOOD:
				outputString = "Click where to drop the food.";
				break;
			case SET_GOTO:
				outputString = "Click where the ant should go.";
				break;
			case HIGHLIGHT_MODE:
				outputString = "Drag to select ants.";
				break;
			default:
				outputString = "Error: default";
			}
		} else {
			String sMinute = (minute < 10) ? "0" + minute : "" + minute;
			outputString = "Day: " + day + "; Time: " + hour + ":" + sMinute + "; Ant Population: " + aliveAnts
					+ "; Collected Food: " + collectedFoodAmount + "; Your Held Food: " + userFoodAmount + ";";
		}

		// See if the user has met the winning requirements
		if (collectedFoodAmount >= 1000 && aliveAnts >= 10) {
			if (winCounter == 0) {
				makeError("You have reached the winning requirements. Keep it up for a day!");
			}

			if (minuteChange) {
				winCounter += 1;
			}

			// if they have kept the requirements for a day then they win
			if (winCounter >= 1440) {
				outputString = "You Win! Completed in " + day + " days, " + hour + " hours, and " + minute + " minutes!";
				animator = null;
			}
		}
		// if they are under the requirements we set the counter back to 0
		// and tell them they suck
		else if (winCounter > 0) {
			makeError("You went under the winning requirements");
			winCounter = 0;
		}

		// After 10 days or if all ants die the game is over
		if (day >= 10 || aliveAnts == 0) {
			outputString = "Game Over";
			animator = null;
		}

		Parent.setStatOutputField(outputString);

		minuteChange = !minuteChange;
	}
}
