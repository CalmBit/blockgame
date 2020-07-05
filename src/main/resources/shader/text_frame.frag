#version 150 core

in vec4 Color;

out vec4 outColor;

uniform sampler2D tex;

void main() {
    vec4 color = vec4(Color);
    //if(color.a < 0.1)
        //discard;
    outColor = color;
}
