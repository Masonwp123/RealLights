package com.masonwp123.reallights;

import com.masonwp123.reallights.client.ClientConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

/*
TODO:
rendertype_text.vsh
block.vsh
entity.vsh
item.vsh
particle.vsh
rendertype_leash.vsh
rendertype_text.vsh
rendertype_text_background.vsh
rendertype_text_intensity.vsh
terrain.vsh
 */

@Mod(RealLights.MODID)
public class RealLights {
    public static final String MODID = "reallights";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int MAX_LIGHTS = 1024;

    public RealLights(ModContainer container) {
        container.registerConfig(
                ModConfig.Type.CLIENT,
                ClientConfig.SPEC
        );
    }
}
