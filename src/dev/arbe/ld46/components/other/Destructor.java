package dev.arbe.ld46.components.other;

import dev.arbe.engine.states.State;
import dev.arbe.engine.systems.GameEvent;
import dev.arbe.engine.systems.scriptable.Scriptable;

public class Destructor extends Scriptable
{
	public float life;

	public Destructor(float life_) { life = life_; }

	@Override
	public void onEvent(GameEvent event)
	{
		if(event.type == GameEvent.eventType.frame)
		{
			life -= (float)(double)event.args[0];
			if(life<=0)
				State.getActiveState().removeObj(parent);
		}
	}
}
