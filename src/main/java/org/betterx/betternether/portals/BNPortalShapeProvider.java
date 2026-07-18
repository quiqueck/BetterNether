package org.betterx.betternether.portals;

/**
 * Implemented on {@link net.minecraft.world.level.portal.PortalShape} via a mixin so a
 * {@link BNPortalShape} (BetterNether's flexible, flood-fill portal detection) can be attached
 * to a vanilla portal shape instance and drive its validity / completion / block placement.
 */
public interface BNPortalShapeProvider {
    void bn_setShape(BNPortalShape shape);

    BNPortalShape bn_getShape();
}
