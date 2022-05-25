#version 330 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec2 a_uvCoord;
layout (location = 2) in vec3 a_Normal;

layout (location = 0) out vec2 v_uvCoord;
layout (location = 1) out vec3 v_Normal;

uniform mat4 u_Projection;
uniform mat4 u_View;

void main() {
    v_uvCoord = a_uvCoord;
    v_Normal = a_Normal;
    gl_Position = u_Projection * u_View * vec4(a_Position, 1.0);
}