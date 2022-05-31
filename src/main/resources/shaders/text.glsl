#type vert
#version 450 core

layout (location = 0) in vec2 a_Position;
layout (location = 1) in vec2 a_TexCoord;
layout (location = 2) in vec4 a_Color;

out vec2 v_TexCoord;
out vec4 v_Color;

uniform mat4 u_Projection;

void main() {
    v_TexCoord = a_TexCoord;
    v_Color = a_Color;
    gl_Position = u_Projection * vec4(a_Position, 0.0, 1.0);
}

#type frag
#version 450 core

layout (location = 0) out vec4 color;

in vec2 v_TexCoord;
in vec4 v_Color;

uniform sampler2D u_FontTexture;

void main() {
    vec4 sampled = vec4(1.0, 1.0, 1.0, texture(u_FontTexture, v_TexCoord).r);
    color = sampled;
}