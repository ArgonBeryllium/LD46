package dev.arbe.ld46;

import dev.arbe.engine.states.State;

public class GameManager
{
	public static final int ENDING_DEVOURED = 0, ENDING_ARREST = 1, ENDING_FRIDAY = 2;

	public void initEnding(int endingID)
	{
		State.setActiveState(endingID+2);
	}
}
