package org.betterx.betternether.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EntityNagaProjectile extends Mob {
    private static final int MAX_LIFE_TIME = 60; // 3 seconds * 20 ticks
    private int lifeTime = 0;

    public EntityNagaProjectile(EntityType<? extends EntityNagaProjectile> type, Level world) {
        super(type, world);
        this.xpReward = 0;
    }

    public void setParams(LivingEntity owner, Entity target) {
        this.setPos(getX(), getEyeY() - this.getBbHeight(), getZ());
        Vec3 dir = target.position().add(0, target.getBbHeight() * 0.25, 0).subtract(position()).normalize().scale(2);
        this.setDeltaMovement(dir);
        this.xo = getX() - dir.x;
        this.yo = getY() - dir.y;
        this.zo = getZ() - dir.z;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
    }

    // Replicates the movement of the removed vanilla FlyingMob.travel(...)
    @Override
    public void travel(Vec3 vec3) {
        if (this.isLocalInstanceAuthoritative()) {
            if (this.isInWater()) {
                this.moveRelative(0.02f, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8f));
            } else if (this.isInLava()) {
                this.moveRelative(0.02f, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
            } else {
                float f = 0.91f;
                if (this.onGround()) {
                    f = this.level()
                            .getBlockState(this.getBlockPosBelowThatAffectsMyMovement())
                            .getBlock()
                            .getFriction() * 0.91f;
                }
                float g = 0.16277137f / (f * f * f);
                f = 0.91f;
                if (this.onGround()) {
                    f = this.level()
                            .getBlockState(this.getBlockPosBelowThatAffectsMyMovement())
                            .getBlock()
                            .getFriction() * 0.91f;
                }
                this.moveRelative(this.onGround() ? 0.1f * g : 0.02f, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(f));
            }
        }
        this.calculateEntityAnimation(false);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 128;
    }

    @Override
    public void tick() {
        super.tick();
        level().addParticle(ParticleTypes.LARGE_SMOKE,
                getX() + random.nextGaussian() * 0.2,
                getY() + random.nextGaussian() * 0.2,
                getZ() + random.nextGaussian() * 0.2,
                0, 0, 0
        );
        level().addParticle(ParticleTypes.SMOKE,
                getX() + random.nextGaussian() * 0.2,
                getY() + random.nextGaussian() * 0.2,
                getZ() + random.nextGaussian() * 0.2,
                0, 0, 0
        );

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, (entity) -> {
            return entity.isAlive() && entity instanceof LivingEntity;
        });
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }

        lifeTime++;
        if (lifeTime > MAX_LIFE_TIME)
            effectKill();

        if (isSame(this.xo, this.getX()) && isSame(this.yo, this.getY()) && isSame(this.zo, this.getZ()))
            effectKill();
    }

    private boolean isSame(double a, double b) {
        return Math.abs(a - b) < 0.1;
    }

    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.BLOCK) {
            for (int i = 0; i < 10; i++) {
                level().addParticle(
                        ParticleTypes.LARGE_SMOKE,
                        getX() + random.nextGaussian() * 0.5,
                        getY() + random.nextGaussian() * 0.5,
                        getZ() + random.nextGaussian() * 0.5,
                        random.nextGaussian() * 0.2,
                        random.nextGaussian() * 0.2,
                        random.nextGaussian() * 0.2
                );
            }
            effectKill();
        } else if (type == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            if (entity != this && entity instanceof LivingEntity && !(entity instanceof EntityNaga)) {
                LivingEntity living = (LivingEntity) entity;
                if (!(living.hasEffect(MobEffects.WITHER))) {
                    living.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
                    living.hurt(living.damageSources().generic(), 1.0F);
                }
                effectKill();
            }
        }
    }

    private void effectKill() {
        for (int i = 0; i < 10; i++) {
            level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0xFFFFFFFF),
                    getX() + random.nextGaussian() * 0.5,
                    getY() + random.nextGaussian() * 0.5,
                    getZ() + random.nextGaussian() * 0.5,
                    0.1, 0.1, 0.1
            );
        }
        this.discard();
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return false;
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("life", lifeTime);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        lifeTime = input.getIntOr("life", 0);
    }
}
