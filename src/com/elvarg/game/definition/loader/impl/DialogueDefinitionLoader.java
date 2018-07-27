package com.elvarg.game.definition.loader.impl;

import java.io.FileReader;

import com.elvarg.game.GameConstants;
import com.elvarg.game.definition.loader.DefinitionLoader;
import com.elvarg.game.model.dialogue.Dialogue;
import com.elvarg.game.model.dialogue.DialogueExpression;
import com.elvarg.game.model.dialogue.DialogueManager;
import com.elvarg.game.model.dialogue.DialogueType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DialogueDefinitionLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {
		FileReader fileReader = new FileReader(file());
		JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(fileReader);
        Gson builder = new GsonBuilder().create();
        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);
            parse(reader, builder);
        }
        fileReader.close();
	}
	
	private void parse(JsonObject reader, Gson builder) {
		final int id = reader.get("id").getAsInt();
		final DialogueType type = DialogueType.valueOf(reader.get("type").getAsString());
		final DialogueExpression anim = reader.has("anim") ? DialogueExpression.valueOf(reader.get("anim").getAsString()) : null;
		final int lines = reader.get("lines").getAsInt();
		String[] dialogueLines = new String[lines];
		for(int i = 0; i < lines; i++) {
			dialogueLines[i] = reader.get("line" + (i+1)).getAsString();
		}
		final int next = reader.get("next").getAsInt();
		final int npcId = reader.has("npcId") ? reader.get("npcId").getAsInt() : -1;
		final String[] item = reader.has("item") ? (builder.fromJson(reader.get("item"), String[].class)) : null;
		
		Dialogue dialogue = new Dialogue() {
			@Override
			public int id() {
				return id;
			}

			@Override
			public DialogueType type() {
				return type;
			}

			@Override
			public DialogueExpression animation() {
				return anim;
			}

			@Override
			public String[] dialogue() {
				return dialogueLines;
			}

			@Override
			public int nextDialogueId() {
				return next;
			}

			@Override
			public int npcId() {
				return npcId;
			}

			@Override
			public String[] item() {
				return item;
			}
		};
		DialogueManager.dialogues.put(id, dialogue);
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "dialogues.json";
	}
}
