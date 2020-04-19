package dev.arbe.ld46.components.effects;

import dev.arbe.engine.GameObject;
import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.states.State;
import dev.arbe.engine.systems.animation.Puppeteer;
import dev.arbe.engine.systems.animation.SpriteAnimation;
import dev.arbe.engine.systems.rendering.TexturedRenderer;
import dev.arbe.ld46.Main;
import dev.arbe.ld46.states.GameplayState;

import java.util.ArrayList;

public class Effects
{
	private Effects() {}
	static ArrayList<GameObject> effects = new ArrayList<GameObject>();
	protected static Puppeteer aP, sP;
	protected static float aL, sL;

	public static void init()
	{
		aP = new Puppeteer();
		sP = new Puppeteer();
		GameObject aO = State.states[1].createObj(new GameObject());
		GameObject sO = State.states[1].createObj(new GameObject());
		sO.transform.scl = new WVec2(2,2);
		aO.addComponent(new TexturedRenderer(2, null));
		sO.addComponent(new TexturedRenderer(2, null));
		aO.addComponent(aP);
		sO.addComponent(sP);
		aP.addAnimation(new SpriteAnimation(15,  Main.sheets.getAsset("alert").sprites));
		sP.addAnimation(new SpriteAnimation(10,  Main.sheets.getAsset("splatter").sprites));
		aL = 0;
		sL = 0;
	}

	public static void update(double delta)
	{
		if(sL>=0)
		{
			sL -= delta;
			if(sL<0)
				sP.getParent().getComponent(TexturedRenderer.class).active = false;
		}
		if(aL>=0)
		{
			aL -= delta;
			if(aL<0)
				aP.getParent().getComponent(TexturedRenderer.class).active = false;
		}

	}

	public static void splatter(WVec2 pos)
	{
		sP.getParent().transform.pos = pos;
		sP.setActiveIndex(0, true);
		sP.getParent().getComponent(TexturedRenderer.class).active = true;
		sL = .5f;
		GameplayState.cameraShake(.2f);
	}
	public static void alert(WVec2 pos)
	{
		aP.getParent().transform.pos = pos;
		aP.setActiveIndex(0, true);
		aP.getParent().getComponent(TexturedRenderer.class).active = true;
		aL = 1;
		GameplayState.cameraShake(.05f);
	}
}
