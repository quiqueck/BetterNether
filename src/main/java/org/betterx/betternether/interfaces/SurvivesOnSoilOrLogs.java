package org.betterx.betternether.interfaces;

import org.betterx.bclib.interfaces.SurvivesOnTags;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface SurvivesOnSoilOrLogs extends SurvivesOnTags {
    List<TagKey<Block>> TAGS = List.of(CommonBlockTags.SOIL_OR_LOGS);

    @Override
    default List<TagKey<Block>> getSurvivableTags() {
        return TAGS;
    }
}