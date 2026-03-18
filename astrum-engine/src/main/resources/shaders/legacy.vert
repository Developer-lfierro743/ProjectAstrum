#version 450

layout(binding = 0) uniform UniformBufferObject {
    mat4 model;
    mat4 view;
    mat4 proj;
    float fogDensity;
} ubo;

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inTexCoord;
layout(location = 2) in float inShade;

layout(location = 0) out vec2 fragTexCoord;
layout(location = 1) out float fragShade;
layout(location = 2) out float fragFog;

void main() {
    vec4 worldPos = ubo.model * vec4(inPosition, 1.0);
    gl_Position = ubo.proj * ubo.view * worldPos;
    
    fragTexCoord = inTexCoord;
    fragShade = inShade;
    
    // Distance-based fog (Linear)
    float dist = length((ubo.view * worldPos).xyz);
    fragFog = clamp((32.0 - dist) / (32.0 - 4.0), 0.0, 1.0); // 32 is max view dist in legacy
}
