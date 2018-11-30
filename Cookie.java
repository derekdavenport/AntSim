//
//  Cookie.java
//  Ant Sim
//
//  Created by Derek on Sun Oct 24 2004.
//  Copyright (c) 2004 Derek Davenport. All rights reserved.
//

public class Cookie extends Food {
	public static int FOOD_AMOUNT = 100;

	public Cookie(int x, int y) {
		super(FOOD_AMOUNT, 24, 24, x, y);
	}

	public int getImageNum() {
		return 0;
	}
}
