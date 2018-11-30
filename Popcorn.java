//
//  Popcorn.java
//  Ant Sim
//
//  Created by Derek on Sun Oct 24 2004.
//  Copyright (c) 2004 Derek Davenport. All rights reserved.
//

public class Popcorn extends Food {
	public static final int FOOD_AMOUNT = 150;

	public Popcorn(int x, int y) {
		super(FOOD_AMOUNT, 36, 36, x, y);
	}

	public int getImageNum() {
		return 2;
	}
}
