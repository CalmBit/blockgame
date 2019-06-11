#version 150 core

in vec4 Color;

out vec4 outColor;

uniform sampler2D tex;

void main() {
    outColor = Color;
}
