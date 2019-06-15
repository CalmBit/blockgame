#version 150 core

in vec3 position;

out vec3 Color;
out vec3 FogColor;
out float fogDepth;


uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
uniform vec3 color;
uniform vec3 fogColor;


void main()
{
    Color = color;
    FogColor = fogColor;
    gl_Position = proj * view * model * vec4(position, 1.0);

    fogDepth = -(view * model * vec4(position,1.0)).z * 2.5;
}