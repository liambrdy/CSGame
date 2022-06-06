#type vert
#version 450 core

layout (location = 0) in vec2 a_V0;
layout (location = 1) in vec2 a_V1;
layout (location = 2) in vec2 a_V2;
layout (location = 3) in vec2 a_V3;
layout (location = 4) in vec2 a_T0;
layout (location = 5) in vec2 a_T1;
layout (location = 6) in vec2 a_T2;
layout (location = 7) in vec2 a_T3;

out vec2 v_TexCoord;

uniform mat4 u_Projection;

void main() {

    vec2 vertices[4] = vec2[](a_V0, a_V1, a_V2, a_V3);
    vec2 textures[4] = vec2[](a_T0, a_T1, a_T2, a_T3);

    v_TexCoord = textures[gl_VertexID];

    vec4 worldSpace = vec4(vertices[gl_VertexID], 0.0, 1.0);
    gl_Position = u_Projection * worldSpace;
}

#type frag
#version 450 core

layout (location = 0) out vec4 color;

in vec2 v_TexCoord;

uniform sampler2D u_SpriteSheet;

void main() {
    vec4 sampled = texture(u_SpriteSheet, v_TexCoord);
    color = sampled;
    //color = vec4(0.8, 0.2, 0.2, 0.5);
}