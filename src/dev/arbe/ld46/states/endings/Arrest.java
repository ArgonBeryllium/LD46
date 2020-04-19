package dev.arbe.ld46.states.endings;

import dev.arbe.engine.utils.files.FileUtils;

public class Arrest extends EndingState
{
	@Override
	protected void start()
	{
		title = "ARREST";
		text = FileUtils.loadString("res/arrest.end");
		super.start();
	}
}
