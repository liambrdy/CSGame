#type vert
#version 450 core

layout (location = 0) in vec2 a_V0;
layout (location = 1) in vec2 a_V1;
layout (location = 2) in vec2 a_V2;
layout (location = 3) in vec2 a_V3;
layout (location = 4) in vec4 a_C0;
layout (location = 5) in vec4 a_C1;
layout (location = 6) in vec4 a_C2;
layout (location = 7) in vec4 a_C3;

out vec2 v_TextureCoord;
out vec4 v_Color;

uniform mat4 u_Projection;

void main() {

    vec2 vertices[4] = vec2[](a_V0, a_V1, a_V2, a_V3);
    vec4 colors[4] = vec4[](a_C0, a_C1, a_C2, a_C3);
    vec2 uvs[4] = vec2[](vec2(0.0, 1.0), vec2(0.0, 0.0), vec2(1.0, 1.0), vec2(1.0, 0.0));

    v_Color = colors[gl_VertexID];
    v_TextureCoord = uvs[gl_VertexID];

    vec4 worldSpace = vec4(vertices[gl_VertexID], 0.0, 1.0);
    gl_Position = u_Projection * worldSpace;
}

#type frag
#version 450 core

layout (location = 0) out vec4 color;

in vec2 v_TextureCoord;
in vec4 v_Color;

uniform sampler2D u_Texture;

void main() {
    vec4 sampled = texture(u_Texture, v_TextureCoord);
    color = v_Color * sampled;
    //color = vec4(0.8, 0.2, 0.2, 0.5);
}