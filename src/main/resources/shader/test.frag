#version 150 core

in vec3 Color;
in vec2 Texcoord;

in vec3 FogColor;

in float fogDepth;

out vec4 outColor;

uniform sampler2D tex;

void main()
{
    float fogAmount = smoothstep(102.4, 128.0, fogDepth);
    vec4 color = texture(tex, Texcoord) * vec4(Color, 1.0);
    if(color.a < 0.1)
        discard;
    outColor = mix(color, vec4(FogColor, 1.0f), fogAmount);
}