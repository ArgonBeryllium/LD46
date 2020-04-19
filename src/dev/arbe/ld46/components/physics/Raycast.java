package dev.arbe.ld46.components.physics;

import dev.arbe.engine.GameObject;
import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.states.State;


public class Raycast
{
	static final float STEP_SIZE = .4f;

	public static GameObject rayCast(GameObject origin, WVec2 dir, float dist)
	{
		for(float r = 0; r < dist; r += STEP_SIZE)
			for(GameObject o : State.getActiveState().objs)
			{
				if(o==origin)continue;
				if(BasicAABB.contains(o.transform, origin.transform.pos.plus(dir.times(r))))
					return o;
			}
		return null;
	}
	public static GameObject rayCast(WVec2 origin, WVec2 dir, float dist)
	{
		for(float r = STEP_SIZE; r < dist; r += STEP_SIZE)
			for(GameObject o : State.getActiveState().objs)
			{
				if(BasicAABB.contains(o.transform, origin.plus(dir.times(r))))
					return o;
			}
		return null;
	}
}