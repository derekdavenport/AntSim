//
//  Bread.java
//  Ant Sim
//
//  Created by Derek on Sun Oct 24 2004.
//  Copyright (c) 2004 Derek Davenport. All rights reserved.
//

public class Bread extends Food {
	public static int FOOD_AMOUNT = 50;

	public Bread(int x, int y) {
		super(FOOD_AMOUNT, 24, 24, x, y);
	}

	public int getImageNum() {
		return 1;
	}
}
