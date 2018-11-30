
//
//  AntSim.java
//  Ant Sim
//
//  Created by Derek on Sun Oct 17 2004.
//  Copyright (c) 2004 Derek Davenport. All rights reserved.
//

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AntSim extends JFrame {
	private JPanel ButtonContainer;
	private GraphicsPanel AntWindow;
	private JTextField StatOutputField;

	public JLayeredPane Layers;

	protected ResourceBundle resbundle;

	protected final int shortcutKeyMask;
	protected Action spawnAntAction, killAntAction, setGoToAction, spawnFoodAction;
	protected final JMenuBar mainMenuBar;
	protected JMenu antMenu;
	protected JMenu foodMenu;
	protected JButton KillAntButton, GoToPointButton, SpawnAntButton, SpawnFoodButton, CancelButton;

	private Toolkit toolkit;
	protected Image[] ButtonIcons;
	private MediaTracker ButtonIconTracker;

	public AntSim() {
		super("");

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Puts all JMenus in the screen menu bar on OS X
		// Does not affect other OSes
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		// resbundle = ResourceBundle.getBundle("Ant_Simstrings", Locale.getDefault());
		setTitle("Ant Simulation"); // resbundle.getString("frameConstructor"));

		// Make our menu
		shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		mainMenuBar = new JMenuBar();
		antMenu = new JMenu("Ant");
		spawnAntAction = new spawnAntAction();
		killAntAction = new killAntAction();
		setGoToAction = new setGoToAction();
		spawnFoodAction = new spawnFoodAction();
		foodMenu = new JMenu("Food");

		antMenu.add(new JMenuItem(spawnAntAction));
		antMenu.add(new JMenuItem(killAntAction));
		antMenu.add(new JMenuItem(setGoToAction));
		mainMenuBar.add(antMenu);

		foodMenu.add(new JMenuItem(spawnFoodAction));
		mainMenuBar.add(foodMenu);

		setJMenuBar(mainMenuBar);

		toolkit = Toolkit.getDefaultToolkit();

		ButtonIcons = new Image[5];
		ButtonIcons[0] = toolkit.createImage("resources/spawnAntButton.png");
		ButtonIcons[1] = toolkit.createImage("resources/goToButton.png");
		ButtonIcons[2] = toolkit.createImage("resources/killAntButton.png");
		ButtonIcons[3] = toolkit.createImage("resources/cookie.png");
		ButtonIcons[4] = toolkit.createImage("resources/cancelButton.png");
		ButtonIconTracker = new MediaTracker(this);

		for (int i = 0; i < ButtonIcons.length; i++) {
			ButtonIconTracker.addImage(ButtonIcons[i], i);
		}

		try {
			for (int i = 0; i < ButtonIcons.length; i++) {
				ButtonIconTracker.waitForID(i);
			}
		} catch (InterruptedException e) {
			System.err.println(e);
			System.exit(1);
		}

		// Makes the buttons and adds listeners to them
		ButtonContainer = new JPanel();
		ButtonContainer.setLayout(new FlowLayout());

		SpawnAntButton = new JButton(new ImageIcon(ButtonIcons[0]));
		SpawnAntButton.setToolTipText("spawn a new ant");
		SpawnAntButton.addMouseListener(new SpawnAntButtonListener());
		ButtonContainer.add(SpawnAntButton);
		GoToPointButton = new JButton(new ImageIcon(ButtonIcons[1]));
		GoToPointButton.setToolTipText("go to point");
		GoToPointButton.addMouseListener(new GoToPointButtonListener());
		ButtonContainer.add(GoToPointButton);
		KillAntButton = new JButton(new ImageIcon(ButtonIcons[2]));
		KillAntButton.setToolTipText("kill selected ants");
		KillAntButton.addMouseListener(new KillAntButtonListener());
		ButtonContainer.add(KillAntButton);
		SpawnFoodButton = new JButton(new ImageIcon(ButtonIcons[3]));
		SpawnFoodButton.setToolTipText("drop food");
		SpawnFoodButton.addMouseListener(new SpawnFoodButtonListener());
		ButtonContainer.add(SpawnFoodButton);
		CancelButton = new JButton(new ImageIcon(ButtonIcons[4]));
		CancelButton.setToolTipText("cancel");
		CancelButton.addMouseListener(new CancelButtonListener());
		ButtonContainer.add(CancelButton);

		// makes the graphics area
		AntWindow = new GraphicsPanel(this);

		// makes the status bar
		StatOutputField = new JTextField("Welcome to the Ant Simulator");
		StatOutputField.setEditable(false);

		getContentPane().setLayout(new BorderLayout());

		// Adds everything to the window
		// I tried adding indexes because the graphics area is always
		// drawn on top of everything, even menus, but I never found a solution
		getContentPane().add(StatOutputField, BorderLayout.SOUTH, 0);
		getContentPane().add(AntWindow, BorderLayout.CENTER, 1);
		getContentPane().add(ButtonContainer, BorderLayout.NORTH, 2);

		Dimension D = getPreferredSize();
		setSize(640, 480 + (int) D.getHeight() + 10);

		setVisible(true);
	}

	// Makes our menu options
	public class spawnAntAction extends AbstractAction {
		public spawnAntAction() {
			super("Spawn Ant");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcutKeyMask));
		}

		public void actionPerformed(ActionEvent e) {
			AntWindow.spawnAnt();
		}
	}

	public class killAntAction extends AbstractAction {
		public killAntAction() {
			super("Kill Selected");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_K, shortcutKeyMask));
		}

		public void actionPerformed(ActionEvent e) {
			AntWindow.killAnt();
		}
	}

	public class setGoToAction extends AbstractAction {
		public setGoToAction() {
			super("Go To");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, shortcutKeyMask));
		}

		public void actionPerformed(ActionEvent e) {
			AntWindow.setGoTo();
		}
	}

	public class spawnFoodAction extends AbstractAction {
		public spawnFoodAction() {
			super("Drop Food");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, shortcutKeyMask));
		}

		public void actionPerformed(ActionEvent e) {
			AntWindow.spawnFood();
		}
	}

	// BUTTON LISTENING//
	public class SpawnAntButtonListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			AntWindow.spawnAnt();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	public class GoToPointButtonListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			AntWindow.setGoTo();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	public class KillAntButtonListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			AntWindow.killAnt();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	public class SpawnFoodButtonListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			AntWindow.spawnFood();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	public class CancelButtonListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			AntWindow.cancelAction();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	public void setStatOutputField(String text) {
		StatOutputField.setText(text);
	}

	// starts the program ;)
	public static void main(String args[]) {
		try {
			// Sets the look and feel to the OSes natural look and feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		new AntSim();
	}
}
