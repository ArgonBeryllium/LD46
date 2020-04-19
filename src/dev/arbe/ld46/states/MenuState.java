package dev.arbe.ld46.states;

import dev.arbe.engine.Game;
import dev.arbe.engine.GameObject;
import dev.arbe.engine.WindowManager;
import dev.arbe.engine.maths.vectors.SVec2;
import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.states.State;
import dev.arbe.engine.systems.input.InputSystem;
import dev.arbe.engine.systems.rendering.RenderSystem;
import dev.arbe.engine.systems.rendering.TextRenderer;
import dev.arbe.engine.systems.rendering.TexturedRenderer;
import dev.arbe.engine.systems.sound.SoundSystem;
import dev.arbe.engine.utils.files.FileUtils;
import dev.arbe.ld46.GameManager;
import dev.arbe.ld46.Main;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MenuState extends State
{
	static final String[] INTRO =
			{
				"You've always been fond of animals.\nYou'd do just about anything\nto make the little critters happy.",
				"But the entity you stumbled upon\nthat Sunday evening in the deep woods\nwas no ordinary pet.",
				"No matter the cost, you will KEEP IT ALIVE\nby making sure it's well fed.\nNot just because of " +
				"your love for animals,\nbut also out of fear.\nWhat could happen if it's not... satisfied?"
			};
	static int i = 0;

	TextRenderer msgRend;
	TexturedRenderer titleRend;

	@Override
	protected void start()
	{
		RenderSystem.setBackgroundColour(Color.BLACK);
		GameObject o = createObj(new GameObject());
		titleRend = new TexturedRenderer(FileUtils.loadImage("res/sprites/logo.png"));
		o.addComponent(titleRend);
		o.transform.scl = new WVec2(5, 3);
		titleRend.active = false;

		o = createObj(new GameObject());
		msgRend = new TextRenderer("", Main.font);
		o.addComponent(msgRend);
		o.getComponent(TextRenderer.class).size = 5;
		o.transform.pos.y = 2;
		o.transform.pos.x = -5;
	}

	@Override
	public void update(double delta)
	{
		msgRend.screenPos = new SVec2(0, WindowManager.getHeight()/2);
		titleRend.renderTransform.pos.y = -3 + Math.sin(Game.getElapsedTime())*.1;
		if(i < INTRO.length)
			msgRend.text = INTRO[i] + "\n(Press [ENTER] to continue)";
		else if(i == INTRO.length)
			msgRend.text = "Name your new friend.\n" + GameManager.name + "_" + (GameManager.name.length()>2?"\n(Press [ENTER] to continue)":"");
		else if(i == INTRO.length + 1)
			msgRend.text = "Use WASD / arrow keys to move.\nSprint with [SHIFT]\nZoom in/out with [I] and [O].\nUse the mouse to instruct " + GameManager.name + ".\nPress [ENTER] to start.";
	}

	@Override
	public void onButtonInput(int type, int code, int state)
	{
		if(type == InputSystem.T_BUTTON_INPUT_KEY && state == 1)
		{
			if(code == KeyEvent.VK_ENTER)
			{
				SoundSystem.playSFX(Main.sounds.getAsset("beep"));
				if(i > INTRO.length)
					State.setActiveState(1);
				if(i == INTRO.length && GameManager.name.length()>2)
					i++;
				else if(i!=INTRO.length)
					i++;
				if(i == INTRO.length)
					titleRend.active = true;
			}
			if(i == INTRO.length)
			{
				SoundSystem.playSFX(Main.sounds.getAsset("boop"));
				if(GameManager.name.length() < 9 && code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z)
					GameManager.name += KeyEvent.getKeyText(code);
				else if(code == KeyEvent.VK_BACK_SPACE && GameManager.name.length()!=0)
					GameManager.name = GameManager.name.substring(0, GameManager.name.length()-1);
			}
		}
	}
}
