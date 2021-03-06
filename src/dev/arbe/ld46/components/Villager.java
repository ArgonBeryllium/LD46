package dev.arbe.ld46.components;

import dev.arbe.engine.Game;
import dev.arbe.engine.GameObject;
import dev.arbe.engine.WindowManager;
import dev.arbe.engine.maths.vectors.SVec2;
import dev.arbe.engine.maths.vectors.Vec2;
import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.states.State;
import dev.arbe.engine.systems.GameEvent;
import dev.arbe.engine.systems.rendering.Camera;
import dev.arbe.engine.systems.rendering.Renderer;
import dev.arbe.engine.systems.rendering.TexturedRenderer;
import dev.arbe.ld46.GameManager;
import dev.arbe.ld46.Main;
import dev.arbe.ld46.components.effects.Effects;
import dev.arbe.ld46.components.physics.BasicAABB;
import dev.arbe.ld46.components.physics.Raycast;

import java.awt.*;

import static java.lang.Math.*;

public class Villager extends Entity
{
	static final float WANDER_DISTANCE = 1;
	public static final float PERCEPTION = 3.5f;

	public WanderPoint target;
	WVec2[] path;
	int cs = 0;
	float droningAngle = 0;

	public boolean witness = false;

	@Override
	public void onParented()
	{
		super.onParented();
		parent.addComponent(new TexturedRenderer(Main.sheets.getAsset("people").sprites[Main.r.nextInt(Main.sheets.getAsset("people").sprites.length)]));
		parent.tag = "villager";
	}

	@Override
	public void onCollision(BasicAABB other)
	{
		if(!witness)
		switch (other.getParent().tag)
		{
			case "villager":
				if(((Villager)other).witness)
					getAlerted();
				break;
			case "wall":
				initiateWander();
				break;
			case "murder":
				getAlerted();
				break;
		}
	}

	public void getAlerted()
	{
		target = WanderPoint.getClosestPolicePoint(parent.transform.pos);
		path = calculatePath(target.getParent().transform.pos);
		cs = 0;
		witness = true;
		Effects.alert(parent.transform.pos);
	}

	public void initiateWander()
	{
		target = WanderPoint.getRandomPoint();
		path = calculatePath(target.getParent().transform.pos);
		cs = 0;
	}

	public boolean onScreen()
	{
		SVec2 sc = State.getActiveState().mainCam.toScreenCoords(min);
		SVec2 ss = Camera.toScreenScale(parent.transform.scl, State.getActiveState().mainCam.scale);
		return sc.x + ss.x/2 > 0 && sc.y + ss.y/2 > 0 && sc.x - ss.x/2 < WindowManager.getWidth() && sc.y - ss.y/2 < WindowManager.getHeight();
	}

	@Override
	public void onEvent(GameEvent event)
	{
		super.onEvent(event);
		if(!onScreen())return;

		if(event.type == GameEvent.eventType.frame)
		{
			if (witness)
			{
				if(Vec2.dist(parent.transform.pos, target.getParent().transform.pos) < WANDER_DISTANCE)
				{
					GameManager.initEnding(GameManager.ENDING_ARREST);
					return;
				}
				if(path==null || cs >= path.length)
				{
					target = WanderPoint.getClosestPolicePoint(parent.transform.pos);
					path = calculatePath(target.getParent().transform.pos);
					cs = 0;
					return;
				}
				if(cs < path.length)
				{
					parent.transform.pos = parent.transform.pos.plus(path[cs].minus(parent.transform.pos).normalised().times((double) event.args[0] * speed * 1.5));
					if(Vec2.dist(parent.transform.pos, path[cs]) < WANDER_DISTANCE)
						cs++;

					getParent().getComponent(Renderer.class).renderTransform.angle +=
							(atan2(target.getParent().transform.pos.x - parent.transform.pos.x,
									parent.transform.pos.y - target.getParent().transform.pos.y)
									- getParent().getComponent(Renderer.class).renderTransform.angle) * 5 * (double)event.args[0];
				}
				return;
			}

			if (target == null)
				initiateWander();

			//region sight
//			Graphics g = WindowManager.getGraphics();
			GameObject seen = null;
			for(float i = -1; i <= 1; i+=.66)
			{
				float f = (float) ((float)i * .4f + getParent().getComponent(Renderer.class).renderTransform.angle - PI/2);

//				g.setColor(Color.white);
				seen = Raycast.rayCast(getParent(), new WVec2(cos(f), sin(f)).normalised(), PERCEPTION);
				SVec2 p = Camera.getMain().toScreenCoords(parent.transform.pos);
				SVec2 d = Camera.toScreenScale(new WVec2(cos(f), sin(f)).normalised().times(PERCEPTION), Camera.getMain().scale);

				if(seen != null)
				{
//					g.setColor(Color.red);
					switch (seen.tag)
					{
						case "villager":
							if(seen.getComponent(Villager.class).witness)
								getAlerted();
							break;
						case "monster":
							if(seen.getComponent(Monster.class).hidden)break;
							getAlerted();
							break;
						case "murder":
							getAlerted();
							break;
					}
				}
//				g.drawLine((int)p.x, (int)p.y, (int)p.x + (int)d.x, (int)p.y + (int)d.y);
			}
			//endregion

			//region wandering cycle
			if(path!=null)
			{
				if(cs>=path.length)
				{
					if(Main.r.nextInt(100)==7)
					{
						State.getActiveState().removeObj(parent);
						return;
					}
					initiateWander();
					return;
				}

				parent.transform.pos = parent.transform.pos.plus(path[cs].minus(parent.transform.pos).normalised().times((double) event.args[0] * speed));
				if(Vec2.distSquared(parent.transform.pos, path[cs]) < WANDER_DISTANCE*WANDER_DISTANCE)
					cs++;

				//region lerping angle in the movement direction
				getParent().getComponent(Renderer.class).renderTransform.angle +=
						(atan2(target.getParent().transform.pos.x - parent.transform.pos.x,
								parent.transform.pos.y - target.getParent().transform.pos.y)
								- getParent().getComponent(Renderer.class).renderTransform.angle) * 3 * (double)event.args[0];
				//endregion
			}
			//endregion
			else
			{
				droningAngle+=(double)event.args[0];
				parent.transform.pos = parent.transform.pos.plus(new WVec2(cos(droningAngle), sin(droningAngle)).normalised().times((double)event.args[0] * speed));
			}
		}

	}
}
