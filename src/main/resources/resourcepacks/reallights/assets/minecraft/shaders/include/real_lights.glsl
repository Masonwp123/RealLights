#version 330

// Four default layouts, shared, packed, std140, std430 (shared is default)
layout(std140) uniform RealLightsUniform {
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

vec4 mix_light(vec3 position) {
    vec3 contribution = vec3(0.0);
    for(int i = 0; i < 2; i++) {
        RealLight light = getLight(i);

        vec3 lightPosition = ((light.position - cameraPos) - position);
        float attenuation = clamp(1.0 - (length(lightPosition) / light.attenuation), 0.0, 1.0);
        contribution = contribution + clamp(attenuation,0,1) * (light.color * light.intensity);
    }
    contribution = clamp(contribution,0,1);
    return vec4(contribution.r,contribution.g,contribution.b, 0.0);
}