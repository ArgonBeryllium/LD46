package dev.arbe.ld46.states;

import dev.arbe.engine.GameObject;
import dev.arbe.engine.WindowManager;
import dev.arbe.engine.maths.vectors.SVec2;
import dev.arbe.engine.states.State;
import dev.arbe.engine.systems.input.InputSystem;
import dev.arbe.engine.systems.rendering.RenderSystem;
import dev.arbe.engine.systems.rendering.TextRenderer;
import dev.arbe.ld46.GameManager;
import dev.arbe.ld46.Main;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MenuState extends State
{
	static final String[] INTRO =
			{
				"You've always been fond of animals.\nYou'd do just about anything\n to make the little critters happy.",
				"But the entity you stumbled upon\nthat Sunday evening in the deep woods\n was no ordinary pet.",
				"No matter the cost, you will KEEP IT ALIVE\nby making sure it's well fed.\nNot just because of" +
				"your love for animals,\n but also out of fear.\nWhat could happen if it's not... satisfied?"
			};
	static int i = 0;

	TextRenderer msgRend, titleRend;

	@Override
	protected void start()
	{
		RenderSystem.setBackgroundColour(Color.BLACK);
		GameObject o = createObj(new GameObject());
		titleRend = new TextRenderer("", Main.font);
		o.addComponent(titleRend);
		titleRend.size = 10;
		o.transform.pos.x = -5;

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
		titleRend.screenPos = new SVec2(0, titleRend.size * 4 * WindowManager.getWidth()/540);
		if(i < INTRO.length)
			msgRend.text = INTRO[i] + "\n(Press [ENTER] to continue)";
		else if(i == INTRO.length)
			msgRend.text = "Enter a name:\n" + GameManager.name + "_" + (GameManager.name.length()>2?"\n(Press [ENTER] to continue)":"");
		else if(i == INTRO.length + 1)
			msgRend.text = "Use WASD / arrow keys to move.\nUse the mouse to instruct " + GameManager.name + ".\nPress [ENTER] to start.";
	}

	@Override
	public void onButtonInput(int type, int code, int state)
	{
		if(type == InputSystem.T_BUTTON_INPUT_KEY && state == 1)
		{
			if(code == KeyEvent.VK_ENTER)
			{
				if(i > INTRO.length)
					State.setActiveState(1);
				if(i == INTRO.length && GameManager.name.length()>2)
					i++;
				else if(i!=INTRO.length)
					i++;
				if(i == INTRO.length)
					titleRend.text = "DANGER PET";
			}
			if(i == INTRO.length)
			{
				if(GameManager.name.length() < 9 && code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z)
					GameManager.name += KeyEvent.getKeyText(code);
				else if(code == KeyEvent.VK_BACK_SPACE && GameManager.name.length()!=0)
					GameManager.name = GameManager.name.substring(0, GameManager.name.length()-1);
			}
		}
	}
}
