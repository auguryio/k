package com.elvarg.game.definition.loader.impl;

import java.io.FileReader;

import com.elvarg.game.GameConstants;
import com.elvarg.game.definition.NpcDropDefinition;
import com.elvarg.game.definition.loader.DefinitionLoader;
import com.google.gson.Gson;

public class NpcDropDefinitionLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {
		FileReader reader = new FileReader(file());
		NpcDropDefinition[] defs = new Gson().fromJson(reader, NpcDropDefinition[].class);
		for(NpcDropDefinition def : defs) {
			for(int npcId : def.getNpcIds()) {
				NpcDropDefinition.definitions.put(npcId, def);
			}
		}
		reader.close();
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "npc_drops.json";
	}
}
