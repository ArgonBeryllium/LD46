package dev.arbe.ld46.states.endings;

import dev.arbe.engine.utils.files.FileUtils;

public class Devoured extends EndingState
{
	@Override
	protected void start()
	{
		title = "DEVOURED";
		text = FileUtils.loadString("res/devoured.end");
		super.start();
	}
}
