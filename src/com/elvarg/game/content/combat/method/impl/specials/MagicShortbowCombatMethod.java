package com.elvarg.game.content.combat.method.impl.specials;

import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.CombatType;
import com.elvarg.game.content.combat.hit.PendingHit;
import com.elvarg.game.content.combat.method.CombatMethod;
import com.elvarg.game.content.combat.ranged.RangedData.RangedWeaponData;
import com.elvarg.game.entity.impl.Character;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.GraphicHeight;
import com.elvarg.game.model.Priority;
import com.elvarg.game.model.Projectile;

public class MagicShortbowCombatMethod implements CombatMethod {

	private static final Animation ANIMATION = new Animation(1074, Priority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(250, GraphicHeight.HIGH, Priority.HIGH);

	@Override
	public CombatType getCombatType() {
		return CombatType.RANGED;
	}

	@Override
	public PendingHit[] getHits(Character character, Character target) {
		return new PendingHit[]{new PendingHit(character, target, this, true, 3), new PendingHit(character, target, this, true, 2)};
	}

	@Override
	public boolean canAttack(Character character, Character target) {
		Player player = character.getAsPlayer();

		//Check if current player's ranged weapon data is magic shortbow.
		if(!(player.getCombat().getRangedWeaponData() != null 
				&& player.getCombat().getRangedWeaponData() == RangedWeaponData.MAGIC_SHORTBOW)) {
			return false;
		}

		//Check if player has enough ammunition to fire.
		if(!CombatFactory.checkAmmo(player, 2)) {
			return false;
		}

		return true;
	}

	@Override
	public void preQueueAdd(Character character, Character target) {
		final Player player = character.getAsPlayer();

		CombatSpecial.drain(player, CombatSpecial.MAGIC_SHORTBOW.getDrainAmount());

		//Send 2 arrow projectiles
		new Projectile(player, target, 249, 40, 70, 43, 31, 0).sendProjectile();
		new Projectile(character, target, 249, 33, 74, 48, 31, 0).sendProjectile();

		//Remove 2 arrows from ammo
		CombatFactory.decrementAmmo(player, target.getPosition(), 2);
	}

	@Override
	public int getAttackSpeed(Character character) {
		return character.getBaseAttackSpeed() + 1;
	}

	@Override
	public int getAttackDistance(Character character) {
		return 6;
	}

	@Override
	public void startAnimation(Character character) {
		character.performAnimation(ANIMATION);
		character.performGraphic(GRAPHIC);
	}

	@Override
	public void finished(Character character) {

	}

	@Override
	public void handleAfterHitEffects(PendingHit hit) {

	}
}