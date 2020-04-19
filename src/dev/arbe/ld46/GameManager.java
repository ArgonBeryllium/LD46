package dev.arbe.ld46;

import dev.arbe.engine.states.State;

public class GameManager
{
	public static final int ENDING_DEVOURED = 0, ENDING_ARREST = 1, ENDING_FRIDAY = 2,
							AVG_DEATHS = 4;
	public static String name = "Bulbo";

	public static int day = 0;
	public static int dead = 0;

	public static void initEnding(int endingID)
	{
		State.setActiveState(endingID+3);
		day = 0;
		dead = 0;
	}

	public static boolean appropriateDeaths()
	{
		return dead >= day * AVG_DEATHS;
	}
}
