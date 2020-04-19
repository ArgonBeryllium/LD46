package dev.arbe.ld46.components;

import dev.arbe.engine.systems.GameComponent;
import dev.arbe.engine.systems.GameEvent;
import dev.arbe.engine.systems.rendering.TexturedRenderer;
import dev.arbe.ld46.Main;

public class HidingPlace extends GameComponent
{
	public boolean occupied = false;

	@Override
	public void onParented()
	{
		parent.addComponent(new TexturedRenderer(-1, Main.sheets.getAsset("tiles").sprites[5]));
		super.onParented();
	}

	@Override
	public void onEvent(GameEvent event) {}
}
