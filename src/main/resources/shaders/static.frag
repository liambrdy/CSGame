#version 330 core

layout (location = 0) out vec4 color;

layout (location = 0) in vec2 v_uvCoord;

uniform sampler2D u_Texture;

void main() {
    color = vec4(0.8, 0.2, 0.2, 1.0);
//    color = texture(u_Texture, v_uvCoord);
}