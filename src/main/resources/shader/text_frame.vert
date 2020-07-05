#version 150 core

in vec2 position;
in vec4 color;

out vec4 Color;

uniform mat4 proj;

void main() {
    Color = color;
    gl_Position = proj * vec4(position, -0.9, 1.0);
}
