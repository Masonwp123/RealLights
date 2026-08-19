#version 330

// Four default layouts, shared, packed, std140, std430 (shared is default)
layout(std140) uniform RealLightsUniform {
    int numLights;
    vec3 cameraPos;
};

struct RealLight {
    vec3 position;
    float attenuation;
    vec3 color;
    float intensity;
};

uniform isamplerBuffer RealLightsTexelBuffer;

// Read an encoded float from the buffer
float readFloat(int byteOffset) {
    int b0 = texelFetch(RealLightsTexelBuffer, byteOffset + 0).r & 0xFF;
    int b1 = texelFetch(RealLightsTexelBuffer, byteOffset + 1).r & 0xFF;
    int b2 = texelFetch(RealLightsTexelBuffer, byteOffset + 2).r & 0xFF;
    int b3 = texelFetch(RealLightsTexelBuffer, byteOffset + 3).r & 0xFF;

    uint bits =
        uint(b0) |
        (uint(b1) << 8) |
        (uint(b2) << 16) |
        (uint(b3) << 24);

    return uintBitsToFloat(bits);
}

RealLight getLight(int index) {
    int base = index * 32;

    RealLight light;

    light.position = vec3(
            readFloat(base + 0),
            readFloat(base + 4),
            readFloat(base + 8)
    );

    light.attenuation = readFloat(base + 12);

    light.color = vec3(
            readFloat(base + 16),
            readFloat(base + 20),
            readFloat(base + 24)
    );

    light.intensity = readFloat(base + 28);

    return light;
}

vec4 mix_light(sampler2D lightMap, ivec2 uv, vec3 position) {
    vec3 contribution = vec3(0.0, 0.0, 0.0);
    for (int i = 0; i < numLights; i++) {
        RealLight light = getLight(i);

        // Calculate position from the vertex position to the light
        vec3 toLight = ((light.position - cameraPos) - position);

        // Calculate the attenuation (falloff) of the light
        float lightDistance = length(toLight);
        float attenuation = max(0.0, 1.0 - (lightDistance / light.attenuation));
        attenuation *= attenuation;

        // Calculate the lights total contribution
        vec3 lightContribution = light.color * attenuation * light.intensity;

        // Calculate luminance and only apply light in darkness
        float luminance = dot(contribution, vec3(0.2126, 0.7152, 0.0722));
        contribution += lightContribution * (1.0 - luminance);
    }

    // Add custom illumination to the darkness in lighting
    vec4 lighting = sample_lightmap(lightMap, uv);
    lighting.rgb += contribution * (1.0 - lighting.rgb);
    return lighting;
}