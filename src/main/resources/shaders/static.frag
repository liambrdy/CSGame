#version 330 core

layout (location = 0) out vec4 color;

layout (location = 0) in vec2 v_uvCoord;
layout (location = 1) in vec3 v_Normal;

uniform sampler2D u_Texture;

void main() {
    color = vec4(v_Normal, 1.0);
    //color = texture(u_Texture, v_uvCoord);
}