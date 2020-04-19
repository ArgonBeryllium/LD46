package dev.arbe.ld46.states;

import dev.arbe.engine.Game;
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

public class BasementState extends State
{
	static final String[]
	MESSAGES_OKAY =
			{
				"[Bulbo] seems satisfied.\nYou're not really sure if the constant \nlow growls and rumbles are a sign of\ncontent or hostility, " +
				"but either way,\nit hasn't bitten you yet, which you\ntake as a good sign.\nBetter keep this pace up.",
				"[Bulbo]'s looking rather full.\nThat seemed like definitely enough\ncivilians for the day.",
				"[Bulbo]'s stomach isn't rumbling\nas much.\nOr maybe it's stopped growling.\nEither way, seems you're doing pretty well.",
				"[Bulbo]'s sleeping like a baby.\nA monstrous, human-feasting baby,\nbut a baby nontheless.",
				"[Bulbo]'s sleeping comfortably in the\ncorner.\nSeeing it like this is oddly soothing."
			},
	MESSAGES_BAD =
			{
				"[Bulbo] seems to be staring at you.\nYou're not quite sure how that's possible,\ngiven it doesn't have any eyeballs\nto speak of, " +
				"but you can definitely feel\nits heavy gaze.\nIt's a look of disappointment.\nBetter luck tomorrow.",
				"[Bulbo]'s disappointed stare from before\nhas slightly shifted in emotion.\nYou can't quite place\nwhat it's supposed to convey now," +
				"\nbut you definitely feel uneasy.\nHopefully tomorrow'll go better.",
				"You're now certain what [Bulbo]'s stare\nis communicating.\nHunger.\nA great, great hunger." +
				"\nYou don't feel safe sleeping in\nthe same room anymore.\nYou move back upstairs for the night."
			},
	WEEKDAYS = { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY" };

	String msg;
	TextRenderer msgRend, titleRend;

	@Override
	protected void start()
	{
		GameObject o = createObj(new GameObject());
		titleRend = new TextRenderer(WEEKDAYS[0]+ ", basement", Main.font);
		o.addComponent(titleRend);
		titleRend.size = 10;

		o = createObj(new GameObject());
		msgRend = new TextRenderer("", Main.font);
		o.addComponent(msgRend);
		o.getComponent(TextRenderer.class).size = 5;
		o.getComponent(TextRenderer.class).text = o.getComponent(TextRenderer.class).text.replace("[Bulbo]", GameManager.name);
		o.transform.pos.y = 2;
		o.transform.pos.x = -10;
	}

	@Override
	public void load()
	{
		GameManager.day++;
		if(GameManager.day >= 4 && !GameManager.appropriateDeaths())
		{
			GameManager.initEnding(GameManager.ENDING_DEVOURED);
			return;
		}
		msg = GameManager.appropriateDeaths()?MESSAGES_OKAY[GameManager.day-1]:MESSAGES_BAD[GameManager.day-1];
		msg = msg.replace("[Bulbo]", GameManager.name);
		msg += "\n (Press [ENTER] to continue)";
		msgRend.text = msg;
		titleRend.text = WEEKDAYS[GameManager.day-1] + ", basement";
		RenderSystem.setBackgroundColour(Color.BLACK);
	}

	@Override
	public void update(double delta)
	{
		msgRend.screenPos = new SVec2(0, WindowManager.getHeight()/2);
		titleRend.screenPos = new SVec2(0, titleRend.size * 4 * WindowManager.getWidth()/540);
	}

	@Override
	public void onButtonInput(int type, int code, int state)
	{
		if(type== InputSystem.T_BUTTON_INPUT_KEY)
		{
			if(code== KeyEvent.VK_ENTER)
			{
				if(GameManager.day<4)
					State.setActiveState(1);
				else
				{
					try { Thread.sleep(500); }
					catch (InterruptedException e) { e.printStackTrace(); }
					GameManager.initEnding(GameManager.ENDING_FRIDAY);
				}
			}
		}
	}
}
