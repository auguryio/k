package com.elvarg.game.content.combat.method;

import com.elvarg.game.content.combat.CombatType;
import com.elvarg.game.content.combat.hit.PendingHit;
import com.elvarg.game.entity.impl.Character;

public interface CombatMethod {

	
	/**
	 * Checks if this combat method can perform the upcoming attack.
	 * @param character
	 * @param target
	 * @return
	 */
	public abstract boolean canAttack(Character character, Character target);
	public abstract void preQueueAdd(Character character, Character target);
	public abstract int getAttackSpeed(Character character);
	public abstract int getAttackDistance(Character character);
	public abstract void startAnimation(Character character);
	public abstract CombatType getCombatType();
	public abstract PendingHit[] getHits(Character character, Character target);
	public abstract void finished(Character character);
	public abstract void handleAfterHitEffects(PendingHit hit);
	
}
