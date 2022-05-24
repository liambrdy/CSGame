#version 330 core

layout (location = 0) in vec2 a_Position;
layout (location = 1) in vec2 a_uvCoord;

layout (location = 0) out vec2 v_uvCoord;

void main() {
    v_uvCoord = a_uvCoord;
    gl_Position = vec4(a_Position, 0.0, 1.0);
}