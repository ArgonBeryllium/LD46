package dev.arbe.ld46.components.other;

import dev.arbe.engine.GameObject;
import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.states.State;
import dev.arbe.ld46.components.physics.BasicAABB;

public class AOE
{
	public static void create(WVec2 pos, float size, String tag, float duration)
	{
		GameObject a = State.getActiveState().createObj(new GameObject());
		a.transform.pos = pos;

		BasicAABB b = new BasicAABB();
		b.isPassive = true;

		a.transform.scl = new WVec2(size, size);
		a.addComponent(b);

		a.addComponent(new Destructor(duration));
	}
}
