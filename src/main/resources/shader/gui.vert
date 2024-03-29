#version 150 core

in vec2 position;
in vec3 color;
in vec2 texcoord;

out vec3 Color;
out vec2 Texcoord;

uniform mat4 proj;

void main() {
    Color = color;
    Texcoord = texcoord;
    gl_Position = proj * vec4(position, -0.9, 1.0);
}
