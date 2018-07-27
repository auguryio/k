package com.elvarg.game.definition.loader.impl;

import java.io.FileReader;

import com.elvarg.game.GameConstants;
import com.elvarg.game.definition.ItemDefinition;
import com.elvarg.game.definition.loader.DefinitionLoader;
import com.google.gson.Gson;

public class ItemDefinitionLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {
		FileReader reader = new FileReader(file());
		ItemDefinition[] defs = new Gson().fromJson(reader, ItemDefinition[].class);
		for(ItemDefinition def : defs) {
			ItemDefinition.definitions.put(def.getId(), def);
		}
		reader.close();
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "items.json";
	}
}
