package dev.arbe.ld46.components.physics;

import dev.arbe.engine.maths.vectors.WVec2;
import dev.arbe.engine.systems.GameEvent;

public class Body extends BasicAABB
{
	public static final float RESTITUTION = .5f;

	public float mass = 1;
	public float invMass() { return mass==0?0:1/mass; }
	public WVec2 vel = new WVec2();

	protected WVec2 prevMin;

	public void addForce(WVec2 f) { vel = vel.plus(f.divide(mass)); }
	public void accelerate(WVec2 a) { vel = vel.plus(a); }

	public void addGlobalForce(WVec2 f)
	{
		for(int i = 0; i < BasicAABB.allAABBs.size(); i++)
			if(BasicAABB.allAABBs.get(i) instanceof Body)
				((Body) BasicAABB.allAABBs.get(i)).addForce(f);
	}
	public void accelerateGlobally(WVec2 a)
	{
		for(int i = 0; i < BasicAABB.allAABBs.size(); i++)
			if(BasicAABB.allAABBs.get(i) instanceof Body)
				((Body) BasicAABB.allAABBs.get(i)).accelerate(a);
	}

	@Override
	public void resolveCol(BasicAABB other)
	{
		boolean ob = other instanceof Body;
		WVec2 n = this.prevMin.minus(this.min); // collision normal

		if(n.x==0 && n.y==0) //correcting in case the other initiated the collision
			if(ob)
				n = other.min.minus(((Body)other).prevMin);
			else
				n = parent.transform.pos.minus(other.getParent().transform.pos);

		//region deciding collision weights based on mass proportions
		float mSum = mass;
		if(ob) mSum += ((Body)other).mass;
		float thisAmt = mass/mSum;
		float otherAmt = ob?((Body) other).mass/mSum:0;
		//endregion

		//region applying calculated translations and forces
		this.parent.transform.pos = this.parent.transform.pos.plus(n.times(thisAmt));
		vel = (n.times(thisAmt * RESTITUTION));

		other.getParent().transform.pos = other.getParent().transform.pos.minus(n.times(otherAmt));
		if(ob)
			((Body) other).vel = (n.times(-otherAmt * RESTITUTION));
		//endregion

		if(overlaps(other)) //if this isn't enough, the collision gets resolved statically as well
			super.resolveCol(other);
	}

	@Override
	public void onEvent(GameEvent event)
	{
		if(event.type== GameEvent.eventType.frame)
		{
			parent.transform.pos = parent.transform.pos.plus(vel.times((double)event.args[0]));

			prevMin = min;
			min = parent.transform.pos.minus(parent.transform.scl.divide(2));
			max = parent.transform.pos.plus(parent.transform.scl.divide(2));
		}
	}
}
