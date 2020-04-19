package dev.arbe.ld46.states.endings;

import dev.arbe.engine.utils.files.FileUtils;

public class Friday extends EndingState
{
	@Override
	protected void start()
	{
		title = "FRIDAY";
		text = FileUtils.loadString("res/friday.end");
		super.start();
	}
}
