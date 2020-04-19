package dev.arbe.ld46;

import dev.arbe.engine.AssetSet;
import dev.arbe.engine.Game;
import dev.arbe.engine.InitParams;
import dev.arbe.engine.WindowManager;
import dev.arbe.engine.states.State;
import dev.arbe.engine.systems.rendering.SpriteSheet;
import dev.arbe.engine.utils.files.FileUtils;
import dev.arbe.ld46.states.BasementState;
import dev.arbe.ld46.states.GameplayState;
import dev.arbe.ld46.states.endings.Arrest;
import dev.arbe.ld46.states.endings.Devoured;
import dev.arbe.ld46.states.endings.Friday;
import dev.arbe.ld46.states.MenuState;

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
		sheets.addAsset("splatter", new SpriteSheet(FileUtils.loadImage("res/splatter.png"), 32, 32));
		sheets.addAsset("alert", new SpriteSheet(FileUtils.loadImage("res/alert.png"), 16, 16));

		font = Font.createFont(Font.TRUETYPE_FONT, FileUtils.loadFile("res/FORCED_SQUARE.ttf"));

		InitParams ip = new InitParams();
		ip.winTitle = "DangerPet";
		ip.fx_pix_count = 200;
		ip.states = new State[] { new MenuState(), new GameplayState(), new BasementState(), new Devoured(), new Arrest(), new Friday() };

		Game.init(ip);
		WindowManager.getFrame().setIconImage(sheets.getAsset("splatter").sprites[3]);
		try { Game.run(); }
		catch (Exception e) { e.printStackTrace(); }
	}
}
