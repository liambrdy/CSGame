#type vert
#version 450 core

layout (location = 0) in vec2 a_V0;
layout (location = 1) in vec2 a_V1;
layout (location = 2) in vec2 a_V2;
layout (location = 3) in vec2 a_V3;
layout (location = 4) in int a_SpriteIndex;

out vec2 v_TexCoord;
flat out int v_SpriteIndex;

uniform mat4 u_Projection;

const vec2 uvs[4] = vec2[](vec2(0.0, 0.0), vec2(0.0, 1.0), vec2(1.0, 0.0), vec2(1.0, 1.0));

void main() {
    v_TexCoord = uvs[gl_VertexID];
    v_SpriteIndex = a_SpriteIndex;

    vec2 vertices[4] = vec2[](a_V0, a_V1, a_V2, a_V3);
    gl_Position = u_Projection * vec4(vertices[gl_VertexID], 0.0, 1.0);
}

#type frag
#version 450 core

layout (location = 0) out vec4 color;

in vec2 v_TexCoord;
flat in int v_SpriteIndex;

struct SpriteSheet {
    sampler2D sheet;
    float columns;
    float rows;
    int count;
};

uniform SpriteSheet u_SpriteSheet;

void main() {
    float cols = u_SpriteSheet.columns;
    float rows = u_SpriteSheet.rows;

    vec2 pos = vec2(v_SpriteIndex % int(cols), int(v_SpriteIndex / rows));
    vec2 uv = vec2((v_TexCoord.x / cols) + pos.x * (1.0 / cols),
                   (v_TexCoord.y / rows) + pos.y * (1.0 / rows));
    vec4 sampled = texture(u_SpriteSheet.sheet, uv);
    color = sampled;
    color = vec4(1,0,0,1);
}