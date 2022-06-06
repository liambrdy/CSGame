#type vert
#version 450 core

layout (location = 0) in vec2 a_V0;
layout (location = 1) in vec2 a_V1;
layout (location = 2) in vec4 a_C1;
layout (location = 3) in vec4 a_C2;

out vec4 v_Color;

uniform mat4 u_Projection;

void main() {
    vec2 vertices[2] = vec2[](a_V0, a_V1);
    vec4 colors[2] = vec4[](a_C1, a_C2);

    v_Color = colors[gl_VertexID];

    vec4 worldSpace = vec4(vertices[gl_VertexID], 0.0, 1.0);
    gl_Position = u_Projection * worldSpace;
}

#type frag
#version 450 core

layout (location = 0) out vec4 color;

in vec4 v_Color;

void main() {
    color = v_Color;
}