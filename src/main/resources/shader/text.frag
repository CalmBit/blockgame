#version 150 core

in vec2 Texcoord;

out vec4 outColor;

uniform sampler2D tex;

void main() {
    vec4 color = texture(tex, Texcoord);
    if(color.a < 0.1)
        discard;
    outColor = color;
}
