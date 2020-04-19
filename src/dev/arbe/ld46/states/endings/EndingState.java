package dev.arbe.ld46.states.endings;

import dev.arbe.engine.GameObject;
import dev.arbe.engine.WindowManager;
import dev.arbe.engine.states.State;
import dev.arbe.engine.systems.input.InputSystem;
import dev.arbe.engine.systems.rendering.RenderSystem;
import dev.arbe.engine.systems.rendering.TextRenderer;
import dev.arbe.ld46.GameManager;
import dev.arbe.ld46.Main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public abstract class EndingState extends State
{
	protected String title, text;
	protected BufferedImage image;

	@Override
	protected void start()
	{
		GameObject o = createObj(new GameObject());
		o.addComponent(new TextRenderer(title, Main.font));
		o.transform.pos.x = -10;

		o = createObj(new GameObject());
		o.addComponent(new TextRenderer(text, Main.font));
		o.getComponent(TextRenderer.class).size = 10;
		o.getComponent(TextRenderer.class).text = o.getComponent(TextRenderer.class).text.replace("[Bulbo]", GameManager.name);
		o.transform.pos.y = 3;
		o.transform.pos.x = -10;

		mainCam.scale = .5f;
	}

	@Override
	public void load()
	{
		RenderSystem.setBackgroundColour(Color.black);
	}

	@Override
	public void update(double delta)
	{
		Graphics g = WindowManager.getGraphics();
		for(GameObject o : objs)
			o.transform.pos.y -= .4f * delta;
		if(objs.get(1).transform.pos.y < -10.5 || InputSystem.getKey(KeyEvent.VK_ENTER))
			State.setActiveState(0);
	}
}
