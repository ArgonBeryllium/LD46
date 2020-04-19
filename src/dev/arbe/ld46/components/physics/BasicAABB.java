package dev.arbe.ld46.components.physics;

import dev.arbe.engine.maths.Transform;
import dev.arbe.engine.maths.vectors.Vec2;
import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.systems.GameEvent;
import dev.arbe.engine.systems.scriptable.Scriptable;

import java.util.ArrayList;

public class BasicAABB extends Scriptable
{
	public static ArrayList<BasicAABB> allAABBs = new ArrayList<BasicAABB>();

	public WVec2 min, max;

	public boolean isStatic = false;
	public boolean isPassive = false;

	public BasicAABB()
	{
		allAABBs.add(this);
	}

	@Override
	public void onParented()
	{
		min = parent.transform.pos.minus(parent.transform.scl.divide(2));
		max = parent.transform.pos.plus(parent.transform.scl.divide(2));
	}

	@Override
	public void onParentDestroyed()
	{
		allAABBs.remove(this);
		super.onParentDestroyed();
	}

	public static void handleCols()
	{
		for(int i = 0; i < allAABBs.size(); i++)
		{
			if(allAABBs.get(i)==null)
			{
				allAABBs.remove(i);
				i--;
				continue;
			}
			BasicAABB b1 = allAABBs.get(i);
			for(int j = i+1; j < allAABBs.size(); j++)
			{
				if(allAABBs.get(j)==null)
				{
					allAABBs.remove(j);
					j--;
					continue;
				}
				BasicAABB b2 = allAABBs.get(j);
				if(b1.overlaps(b2))
				{
					if(!b1.isPassive && !b2.isPassive)
						b1.resolveCol(b2);

					b1.onCollision(b2);
					b2.onCollision(b1);
				}
			}
		}
	}
	public static void handleCols(double delta)
	{
		for(int i = 0; i < allAABBs.size(); i++)
		{
			if(allAABBs.get(i)==null)
			{
				allAABBs.remove(i);
				i--;
				continue;
			}
			BasicAABB b1 = allAABBs.get(i);
			for(int j = i+1; j < allAABBs.size(); j++)
			{
				if(allAABBs.get(j)==null)
				{
					allAABBs.remove(j);
					j--;
					continue;
				}
				BasicAABB b2 = allAABBs.get(j);
				if(b1.overlaps(b2))
				{
					if(!b1.isPassive && !b2.isPassive)
						b1.resolveCol(b2, delta);

					b1.onCollision(b2);
					b2.onCollision(b1);
				}
			}
		}
	}

	public void resolveCol(BasicAABB other)
	{
		/*
		//collision normal
		WVec2 n = this.prevMin.minus(this.min);

		//check if the other initiated the collision and correct the normal if so
		if(n.x==0 && n.y==0)
			n = other.min.minus(other.prevMin);
		*/
		WVec2 n = parent.transform.pos.minus(other.parent.transform.pos);

		//region deciding the resolution weights
		float thisAmt = .5f, otherAmt = .5f;

		if(this.isStatic)
		{
			thisAmt = 0;
			otherAmt = 1;
		}
		if(other.isStatic)
		{
			otherAmt = 0;
			thisAmt *= 2;
		}
		//endregion

		thisAmt *= .05f;
		otherAmt *= .05f;

		this.parent.transform.pos = this.parent.transform.pos.plus(n.times(thisAmt));
		other.parent.transform.pos = other.parent.transform.pos.minus(n.times(otherAmt));
	}
	public void resolveCol(BasicAABB other, double delta)
	{
		/*
		//collision normal
		WVec2 n = this.prevMin.minus(this.min);

		//check if the other initiated the collision and correct the normal if so
		if(n.x==0 && n.y==0)
			n = other.min.minus(other.prevMin);
		*/
		WVec2 n = parent.transform.pos.minus(other.parent.transform.pos);

		//region deciding the resolution weights
		float thisAmt = .5f, otherAmt = .5f;

		if(this.isStatic)
		{
			thisAmt = 0;
			otherAmt = 1;
		}
		if(other.isStatic)
		{
			otherAmt = 0;
			thisAmt *= 2;
		}
		//endregion

		thisAmt *=	3 * delta;
		otherAmt *= 3 * delta;

		this.parent.transform.pos = this.parent.transform.pos.plus(n.times(thisAmt));
		other.parent.transform.pos = other.parent.transform.pos.minus(n.times(otherAmt));
	}
	
	public boolean overlaps(BasicAABB other)
	{
		return max.x > other.min.x && min.x < other.max.x &&
				max.y > other.min.y && min.y < other.max.y;
	}
	public boolean overlaps(WVec2 p)
	{
		return max.x > p.x && min.x < p.x &&
				max.y > p.y && min.y < p.y;
	}

	public static boolean contains(Transform t, WVec2 p)
	{
		return p.x > t.pos.x - t.scl.x/2 && p.x < t.pos.x + t.scl.x/2 &&
				p.y > t.pos.y - t.scl.y/2 && p.y < t.pos.y + t.scl.y/2;
	}

	public void onCollision(BasicAABB other) {}

	@Override
	public void onEvent(GameEvent event)
	{
		if(event.type == GameEvent.eventType.frame)
		{
			min = parent.transform.pos.minus(parent.transform.scl.divide(2));
			max = parent.transform.pos.plus(parent.transform.scl.divide(2));
		}
	}
}
