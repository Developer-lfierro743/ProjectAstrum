#version 450

layout(binding = 1) uniform sampler2D texSampler;

layout(location = 0) in vec2 fragTexCoord;
layout(location = 1) in float fragShade;
layout(location = 2) in float fragFog;

layout(location = 0) out vec4 outColor;

void main() {
    vec4 texColor = texture(texSampler, fragTexCoord);
    
    // Apply vertex shading (simulating Infiniminer's baked lighting)
    vec3 shadedColor = texColor.rgb * fragShade;
    
    // Mix with fog color (Legacy blue-ish fog)
    vec3 fogColor = vec3(0.5, 0.7, 1.0);
    outColor = vec4(mix(fogColor, shadedColor, fragFog), texColor.a);
    
    // Alpha testing for transparent blocks
    if (outColor.a < 0.5) discard;
}
