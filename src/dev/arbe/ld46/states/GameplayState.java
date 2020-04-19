package dev.arbe.ld46.states;

import dev.arbe.engine.GameObject;
import dev.arbe.engine.WindowManager;
import dev.arbe.engine.maths.vectors.SVec2;
import dev.arbe.engine.maths.vectors.Vec2;
import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.states.State;
import dev.arbe.engine.systems.animation.Puppeteer;
import dev.arbe.engine.systems.animation.SpriteAnimation;
import dev.arbe.engine.systems.input.InputSystem;
import dev.arbe.engine.systems.rendering.*;
import dev.arbe.engine.utils.DebugUtils;
import dev.arbe.engine.utils.TileMap;
import dev.arbe.ld46.GameManager;
import dev.arbe.ld46.Main;
import dev.arbe.ld46.components.*;
import dev.arbe.ld46.components.effects.Effects;
import dev.arbe.ld46.components.physics.BasicAABB;

import java.awt.*;

import static java.awt.event.KeyEvent.*;
import static java.lang.Math.atan2;

public class GameplayState extends State
{
	static final float ACTION_DISTANCE = 3,
						DUR_HIDE = .85f, DUR_CHOMP = 1.6f;

	WVec2 inVec;

	public static TileMap map;

	GameObject player;
	Monster monster;
	TextRenderer cText;

	GameObject selObj = null;
	//WVec2[] p;

	float time;

	float timer_monsterAnim = 0, timer_monsterHide = 0, timer_monsterChomp = 0;
	HidingPlace hp = null;
	Villager killQueue = null;
	static float shakeAmt;
	static float timer_camShake = 0;

	public static void cameraShake(float amt)
	{
		shakeAmt = amt;
		timer_camShake = 1;
	}

	@Override
	protected void start()
	{
		inVec = new WVec2();
	}

	@Override
	public void load()
	{
		time = 0;

		timer_monsterAnim = 0;
		timer_monsterHide = 0;
		timer_monsterChomp = 0;
		hp = null;
		killQueue = null;

		//region setting up the map
		map = TileMap.loadFromFile("res/town.map", Main.sheets.getAsset("tiles"), '.', ',', '#', '8', '~', 'O', 'O', 'D', 'P');
		createObj(new GameObject()).addComponent(map);
		//region instantiate map objects
		for (int i = 0; i < map.tiles.length; i++)
		{
			int t = map.tiles[i];
			if (t == 0 || t == 1) continue;
			GameObject go = createObj(new GameObject());
			go.transform.pos = map.getTilePos(i);
			go.transform.scl = new WVec2(map.scale, map.scale);
			switch (t)
			{
				case 2:
				case 3:
				case 4:
					go.addComponent(new BasicAABB());
					go.getComponent(BasicAABB.class).isStatic = true;
					go.tag = "wall";
					break;
				case 5:
				case 6:
					go.addComponent(new HidingPlace());
					break;
				case 7:
					go.addComponent(new WanderPoint());
					go.tag = "goal";
					break;
				case 8:
					go.addComponent(new WanderPoint(true));
					break;
			}
		}
		//endregion
		//endregion
		//region creating the player and the monster
		player = createObj(new GameObject());
		player.addComponent(new TexturedRenderer(Main.sheets.getAsset("people").sprites[0]));
		player.transform.pos = new WVec2(4, 3);
		player.addComponent(new Entity());

		GameObject o = createObj(new GameObject());
		monster = new Monster(player);
		o.addComponent(monster);
		o.addComponent(new TexturedRenderer(Main.sheets.getAsset("monster_pulse").sprites[0]));
		o.addComponent(new Puppeteer(
				new SpriteAnimation(5, Main.sheets.getAsset("monster_pulse").sprites),
				new SpriteAnimation(25, Main.sheets.getAsset("monster_bite").sprites)));
		//endregion
		//region setting up the hovertext
		o = createObj(new GameObject());
		cText = new TextRenderer("piss", Main.font, new SVec2(0, 0));
		o.addComponent(cText);
		cText.scaleSize = true;
		cText.size = 5;
		//endregion
		Effects.init();
		for (int i = 0; i < 16; i++)
		{
			GameObject villager = createObj(new GameObject());
			int t = Main.r.nextInt(map.w * map.h);
			while (map.tiles[t] > 1) t = Main.r.nextInt(map.w * map.h);
			villager.transform.pos.x = t % map.w;
			villager.transform.pos.y = t / map.w;
			villager.addComponent(new Villager());
		}
	}

	@Override
	public void unload()
	{
		while (objs.size() != 0)
			removeObj(objs.get(0));
	}

	@Override
	public void update(double delta)
	{
		time += delta / 7;
		DebugUtils.countFrames();

		if (time >= 12)
		{
			setActiveState(2);
			return;
		}
		BasicAABB.handleCols();
		Effects.update(delta);

		//region camera control
		if (InputSystem.getKey(VK_O) && mainCam.scale > .4)
			mainCam.scale -= delta;
		if (InputSystem.getKey(VK_I) && mainCam.scale < 1)
			mainCam.scale += delta;

		mainCam.pos = Vec2.lerp(mainCam.pos, player.transform.pos, (float) delta * 3 * (InputSystem.getKey(VK_SHIFT) ? 1.5f : 1));
		//endregion
		player.transform.pos = player.transform.pos.plus(inVec.times(delta * 2));

		//region animation & aesthetics
		monster.getParent().getComponent(TexturedRenderer.class).active = !monster.hidden;
		if (inVec.getLengthSquared() != 0)
			player.getComponent(Renderer.class).renderTransform.angle = atan2(inVec.x, -inVec.y);


		if (timer_camShake > 0)
		{
			timer_camShake -= delta;
			mainCam.pos = player.transform.pos.plus(
					new WVec2(Main.r.nextFloat() * shakeAmt, Main.r.nextFloat() * shakeAmt));
		}
		if (timer_monsterChomp > 0)
		{
			timer_monsterChomp -= delta;
			if (killQueue != null)
			{
				monster.getParent().transform.pos = Vec2.lerp(killQueue.getParent().transform.pos, monster.getParent().transform.pos, timer_monsterChomp/DUR_CHOMP);
				if (timer_monsterAnim <= 0)
				{
					monster.attack(killQueue);
					monster.getParent().getComponent(Puppeteer.class).setActiveIndex(1, true);
					timer_monsterAnim = 1;
					killQueue = null;
				}
			}
		}
		if (timer_monsterHide > 0)
		{
			timer_monsterHide -= delta;
			monster.getParent().transform.pos = Vec2.lerp(hp.getParent().transform.pos, monster.getParent().transform.pos, timer_monsterHide/DUR_HIDE);
			if (timer_monsterHide <= 0)
				monster.hide(hp);
		}
		if (timer_monsterAnim > 0)
		{
			timer_monsterAnim -= delta;
			if (timer_monsterAnim <= 0)
				monster.getParent().getComponent(Puppeteer.class).setActiveIndex(0, true);
		}

		//endregion

		//region finding the selected object
		WVec2 mpos = mainCam.toWorldCoords(InputSystem.getMousePos());
		cText.screenPos = InputSystem.getMousePos();
		selObj = null;
		for (GameObject o : objs)
		{
			if (BasicAABB.contains(o.transform, mpos))
			{
				selObj = o;
				break;
			}
		}
		//endregion

		//region colour overlays
		Graphics g = WindowManager.getGraphics();
		if (time <= 12)
		{
			if (time >= 11.5f)
			{
				g.setColor(new Color(0, 0, 0, (time - 11.5f) * 2));
				g.fillRect(0, 0, WindowManager.getWidth(), WindowManager.getHeight());
			}
			g.setColor(new Color(1f - (time / 12), .5f, (time / 12), .3f));
			g.fillRect(0, 0, WindowManager.getWidth(), WindowManager.getHeight());
		} else
		{
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, WindowManager.getWidth(), WindowManager.getHeight());
		}
		//endregion
		//region timer
		g.setColor(Color.WHITE);
		float s = (float) Camera.toScreenScale(new WVec2(1, 0), 1).x;
		g.setFont(Main.font.deriveFont(s));
		g.drawString(12 + (int) time + ":" + (int) ((time - (int) time) * 60), 0, (int) s);
		//endregion

		//region handling the hovertext
		if (selObj != null)
		{
			HidingPlace hp = selObj.getComponent(HidingPlace.class);
			if (hp != null)
			{
				if (Vec2.dist(monster.getParent().transform.pos, selObj.transform.pos) > ACTION_DISTANCE)
				{
					cText.text = "[too far away]";
					selObj = null;
					return;
				}

				cText.text = monster.hidden ? "STOP HIDING" : "HIDE";
				if (!hp.occupied && monster.hidden)
					selObj = null;
				return;
			}
			if (selObj.getComponent(Villager.class) != null)
			{
				if (Vec2.dist(monster.getParent().transform.pos, selObj.transform.pos) > ACTION_DISTANCE)
				{
					cText.text = "[too far away]";
					selObj = null;
					return;
				}

				cText.text = "DEVOUR";
			} else
				cText.text = ".";
		} else
			cText.text = ".";
		//endregion

		//region pathfinding debugging
//		g.setColor(Color.white);
//		p = villager.getComponent(Villager.class).calculatePath(WanderPoint.getClosestPolicePoint(villager.transform.pos).getParent().transform.pos);
//
//		if(p!=null)
//		for(int i = 1; i < p.length; i++)
//		{
//			SVec2 pp = mainCam.toScreenCoords(p[i-1]);
//			SVec2 pc = mainCam.toScreenCoords(p[i]);
//			g.drawLine((int)pp.x, (int)pp.y, (int)pc.x, (int)pc.y);
//		}
		//endregion
		//DebugUtils.countFrames();
	}

	@Override
	public void onButtonInput(int type, int code, int state)
	{
		//region setting up the input vector
		inVec.y = (InputSystem.getKey(VK_W) || InputSystem.getKey(VK_UP)) ? -1 :
				(InputSystem.getKey(VK_S) || InputSystem.getKey(VK_DOWN)) ? 1 : 0;
		inVec.x = (InputSystem.getKey(VK_A) || InputSystem.getKey(VK_LEFT)) ? -1 :
				(InputSystem.getKey(VK_D) || InputSystem.getKey(VK_RIGHT)) ? 1 : 0;
		inVec = inVec.normalised();
		//endregion

		if (type == InputSystem.T_BUTTON_INPUT_MOUSE)
		{
			if (code == 1 && state == 1)
				//region handling selected object action on click
				if (selObj != null)
				{
					if (selObj.getComponent(HidingPlace.class) != null)
					{
						if (!monster.hidden)
						{
							hp = selObj.getComponent(HidingPlace.class);
							timer_monsterHide = DUR_HIDE;
						} else
							monster.unhide();
					} else if (selObj.getComponent(Villager.class) != null)
					{
						killQueue = selObj.getComponent(Villager.class);
						timer_monsterChomp = DUR_CHOMP;
					}
				}
			//endregion
		}
	}
}