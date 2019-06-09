#version 150 core

in vec3 Color;
in vec2 Texcoord;

in float fogDepth;

out vec4 outColor;

uniform sampler2D tex;

void main()
{
    float fogAmount = smoothstep(32.0, 50.0, fogDepth);
    vec4 color = texture(tex, Texcoord) * vec4(Color, 1.0);
    if(color.a < 0.1)
        discard;
    outColor = color; //mix(color, vec4(0.529f, 0.808f, 0.980f, 1.0f), fogAmount);
}