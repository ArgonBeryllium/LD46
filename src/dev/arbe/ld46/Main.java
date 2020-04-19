package dev.arbe.ld46;

import dev.arbe.engine.AssetSet;
import dev.arbe.engine.Game;
import dev.arbe.engine.InitParams;
import dev.arbe.engine.states.State;
import dev.arbe.engine.systems.rendering.SpriteSheet;
import dev.arbe.engine.utils.files.FileUtils;
import dev.arbe.ld46.states.GameplayState;

import java.awt.*;
import java.util.Random;

public class Main
{
	public static AssetSet<SpriteSheet> sheets;
	public static Font font;
	public static Random r = new Random();

	public static void main(String[] args) throws Exception
	{
		sheets = new AssetSet<SpriteSheet>();

		sheets.addAsset("tiles", new SpriteSheet(FileUtils.loadImage("res/tileMap.png"), 16, 16));
		sheets.addAsset("monster_pulse", new SpriteSheet(FileUtils.loadImage("res/pulse.png"), 16, 16));
		sheets.addAsset("monster_bite", new SpriteSheet(FileUtils.loadImage("res/bite.png"), 16, 16));
		sheets.addAsset("people", new SpriteSheet(FileUtils.loadImage("res/people.png"), 16, 16));
		font = Font.createFont(Font.TRUETYPE_FONT, FileUtils.loadFile("res/FORCED_SQUARE.ttf"));


		InitParams ip = new InitParams();
		ip.fx_pix_count = 180;
		ip.states = new State[] { new GameplayState() };
		Game.init(ip);
		Game.run();
	}
}
