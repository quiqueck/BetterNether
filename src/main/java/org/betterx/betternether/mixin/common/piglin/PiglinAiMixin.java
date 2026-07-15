package org.betterx.betternether.mixin.common.piglin;

import net.minecraft.world.entity.monster.piglin.PiglinAi;

import org.spongepowered.asm.mixin.Mixin;

// TODO(1.21.7): revisit piglin nether-armor handling.
// In 1.21.2+ PiglinAi#isWearingGold was removed/renamed to #isWearingSafeArmor, which no longer
// inspects the armor material Holder (the old Holder#is(Holder) injection point is gone). Whether an
// armor piece pacifies piglins is now driven entirely by the #PIGLIN_SAFE_ARMOR item tag. The former
// behaviour (config option piglinIgnoreNetherArmor making Cincinnasite/Nether Ruby/Flaming Ruby count
// as safe) should be reimplemented data-side by adding those items to the minecraft:piglin_safe_armor
// tag, which lives outside the mixin/ package. This mixin is intentionally a no-op so it compiles and
// applies cleanly without a stale/silently-failing injector.
@Mixin(PiglinAi.class)
public class PiglinAiMixin {
}
