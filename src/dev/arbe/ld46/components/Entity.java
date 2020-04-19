package dev.arbe.ld46.components;

import dev.arbe.engine.GameObject;
import dev.arbe.engine.maths.vectors.Vec2;
import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.states.State;
import dev.arbe.ld46.GameManager;
import dev.arbe.ld46.components.effects.Effects;
import dev.arbe.ld46.components.physics.BasicAABB;
import dev.arbe.ld46.components.physics.Raycast;

import java.util.ArrayList;

public class Entity extends BasicAABB
{
	protected float speed = 1;
	public boolean alive = true;
	public void kill()
	{
		alive = false;
		GameManager.dead++;
		Effects.splatter(parent.transform.pos);
		State.getActiveState().removeObj(parent);
	}

	static final WVec2 NORTH = new WVec2(0, -1), SOUTH = new WVec2(0, 1), EAST = new WVec2(1, 0), WEST = new WVec2(-1, 0);

	public WVec2[] calculatePath(WVec2 target)
	{
		// i know this is smoothbrain, let me be, i've been up for 29 hours
		WVec2 seeker = parent.transform.pos;
		ArrayList<WVec2> path = new ArrayList<WVec2>();
		WVec2 dir = target.minus(seeker).normalised();
		WVec2 step = new WVec2();

		int iters = 0;
		while (Vec2.distSquared(seeker, target) > 1 && iters++ < 200)
		{
			for(int i = 0; i < 5; i++)
			{
				GameObject c = Raycast.rayCast(seeker, dir, .5f);
				if((c==null || c==parent || c.getComponent(BasicAABB.class)==null) && Vec2.distSquared(seeker.plus(dir), target) < Vec2.distSquared(seeker, target))
				{
					step = dir;
					seeker = seeker.plus(step);
					path.add(new WVec2(seeker.x, seeker.y));
					dir = target.minus(seeker).normalised();
					i = 5;
					continue;
				}
				switch (i)
				{
					case 0: dir = NORTH; break;
					case 1: dir = EAST; break;
					case 2: dir = SOUTH; break;
					case 3: dir = WEST; break;
					case 4:
						if(path.size()>0)
						{
							seeker = seeker.minus(step);
							path.remove(path.size()-1);
							dir = WEST;
						}
						else return null;
						break;
				}
			}
		}
		if(path.size()==0)return null;
		WVec2[] out = new WVec2[path.size()];
		for(int i = 0; i < out.length; i++) out[i] = path.get(i);
		return out;
	}
}
