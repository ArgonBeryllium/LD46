package dev.arbe.ld46.components;

import dev.arbe.engine.GameObject;
import dev.arbe.engine.maths.vectors.Vec2;
import dev.arbe.engine.systems.GameEvent;
import dev.arbe.engine.systems.rendering.Renderer;
import dev.arbe.engine.systems.rendering.TexturedRenderer;
import dev.arbe.engine.systems.scriptable.Scriptable;
import dev.arbe.engine.systems.sound.SoundSystem;
import dev.arbe.ld46.GameManager;
import dev.arbe.ld46.Main;
import dev.arbe.ld46.components.effects.Effects;
import dev.arbe.ld46.components.other.AOE;

import static java.lang.Math.atan2;

public class Monster extends Scriptable
{
	GameObject player;

	public boolean hidden = false;
	public HidingPlace place;

	public Monster(GameObject player_)
	{
		player = player_;
	}

	@Override
	public void onParented()
	{
		super.onParented();
		parent.tag = "monster";
	}

	@Override
	public void onEvent(GameEvent event)
	{
		if(event.type == GameEvent.eventType.frame)
			update((double) event.args[0]);
	}

	public void update(double delta)
	{
		if(hidden) parent.transform.pos = place.getParent().transform.pos;
		else if(Vec2.dist(parent.transform.pos, player.transform.pos)>1.2)
			parent.transform.pos = Vec2.lerp(parent.transform.pos, player.transform.pos, (float)delta);

		getParent().getComponent(Renderer.class).renderTransform.angle += delta * 3 * -getParent().getComponent(Renderer.class).renderTransform.angle;
	}

	public void hide(HidingPlace hp)
	{
		place = hp;
		place.getParent().getComponent(TexturedRenderer.class).sprite = Main.sheets.getAsset("tiles").sprites[6];
		hp.occupied = true;
		hidden = true;
		SoundSystem.playSFX(Main.sounds.getAsset("hide"));
	}
	public void unhide()
	{
		place.occupied = false;
		place.getParent().getComponent(TexturedRenderer.class).sprite = Main.sheets.getAsset("tiles").sprites[5];
		place = null;
		hidden = false;
	}

	public void attack(Villager target)
	{
		if(hidden)
			unhide();

		getParent().getComponent(Renderer.class).renderTransform.angle =
				atan2(target.getParent().transform.pos.x - parent.transform.pos.x,
						-(target.getParent().transform.pos.y - parent.transform.pos.y));

		parent.transform.pos = target.getParent().transform.pos;

		target.kill();
		Effects.splatter(parent.transform.pos);
		AOE.create(parent.transform.pos, Villager.PERCEPTION * 1.5f, "murder", 1.5f);
	}
}
