#version 330

vec4 sample_lightmap(sampler2D lightMap, ivec2 uv) {
    return texture(lightMap, clamp((uv / 256.0) + 0.5 / 16.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));
}

// This import ensures that we don't get errors from shaders that are tagged for use with RealLights (but may or may not have implemented it)
// It also ensures that shaders from other mods and future updates can easily be implemented
#ifdef USE_REAL_LIGHTS
    #moj_import <minecraft:real_lights.glsl>
#endif
