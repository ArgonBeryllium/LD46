package dev.arbe.ld46.components;

import dev.arbe.engine.maths.vectors.Vec2;
import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.systems.GameComponent;
import dev.arbe.engine.systems.GameEvent;
import dev.arbe.ld46.Main;

import java.util.ArrayList;

public class WanderPoint extends GameComponent
{
	protected static ArrayList<WanderPoint> wanderPoints = new ArrayList<WanderPoint>();
	protected static ArrayList<WanderPoint> policePoints = new ArrayList<WanderPoint>();
	public static WanderPoint getRandomPoint()
	{
		return wanderPoints.get(Main.r.nextInt(wanderPoints.size()));
	}
	public static WanderPoint getClosestPolicePoint(WVec2 p)
	{
		float d = Float.MAX_VALUE;
		int o = 0;
		for(int i = 0; i < policePoints.size(); i++)
			if(Vec2.distSquared(policePoints.get(i).getParent().transform.pos, p)<d)
			{
				d = (float) Vec2.distSquared(policePoints.get(i).getParent().transform.pos, p);
				o = i;
			}
		return policePoints.get(o);
	}

	public WanderPoint()
	{
		wanderPoints.add(this);
	}
	public WanderPoint(boolean policePoint)
	{
		if(policePoint)
			policePoints.add(this);
		else
			wanderPoints.add(this);
	}
	@Override
	public void onParentDestroyed() { wanderPoints.remove(this); }

	@Override
	public void onEvent(GameEvent event) {}
}
