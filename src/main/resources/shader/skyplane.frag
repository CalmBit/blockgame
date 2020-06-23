#version 150 core

in vec3 Color;
in vec3 FogColor;

in float fogDepth;

out vec4 outColor;

void main()
{
    float fogAmount = smoothstep(96.0, 128.0, fogDepth);
    vec4 color = vec4(Color, 1.0);

    outColor = mix(color, vec4(FogColor, 1.0f), fogAmount);
}