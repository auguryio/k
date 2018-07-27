package com.elvarg.game.definition.loader.impl;

import java.io.FileReader;

import com.elvarg.game.GameConstants;
import com.elvarg.game.World;
import com.elvarg.game.definition.loader.DefinitionLoader;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.model.Position;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {

		try (FileReader in = new FileReader(file())) {
			JsonParser parser = new JsonParser();
			JsonArray array = (JsonArray) parser.parse(in);
			Gson builder = new GsonBuilder().create();

			for (int i = 0; i < array.size(); i++) {
				JsonObject reader = (JsonObject) array.get(i);
				int id = reader.get("npcId").getAsInt();
				int xPos = reader.get("xPos").getAsInt();
				int yPos = reader.get("yPos").getAsInt();
				int height = reader.get("height").getAsInt();
				int walkType = reader.get("walkType").getAsInt();
				int maxHit = reader.get("maxHit").getAsInt();
				int attack = reader.get("attack").getAsInt();
				int defence = reader.get("defence").getAsInt();
				World.getAddNPCQueue().add(new NPC(id, new Position(xPos, yPos, height)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "newspawns.json";
	}
}
