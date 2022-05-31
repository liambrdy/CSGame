#type vert
#version 450 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec2 a_uvCoord;
layout (location = 2) in vec3 a_Normal;

out vec2 v_uvCoord;
out vec3 v_Normal;
out vec3 v_Pos;

uniform mat4 u_Projection;
uniform mat4 u_View;
uniform mat4 u_Transform;

void main() {
    v_uvCoord = a_uvCoord;
    v_Normal = a_Normal;
    v_Pos = vec3(vec4(a_Position, 1.0));
    gl_Position = u_Projection * u_View * u_Transform * vec4(a_Position, 1.0);
}

#type frag
#version 450 core

layout (location = 0) out vec4 color;

in vec2 v_uvCoord;
in vec3 v_Normal;
in vec3 v_Pos;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

uniform sampler2D u_Texture;
uniform vec3 u_ViewPos;
uniform Material u_Material;

uniform vec3 u_LightPositions[4];
uniform vec3 u_LightColors[4];
uniform vec3 u_LightAttenuations[4];

const vec3 lightAmbient = vec3(0.2, 0.2, 0.2);
const vec3 lightDiffuse = vec3(0.5, 0.5, 0.5);
const vec3 lightSpecular = vec3(1.0, 1.0, 1.0);

const vec3 lightDirection = vec3(0.0, -1.0, 0.0);

void main() {
    vec3 ambient = lightAmbient * u_Material.ambient;

    vec3 norm = normalize(v_Normal);

    vec3 totalDiff = vec3(0.0);
    vec3 totalSpec = vec3(0.0);

    for (int i = 0; i < 4; i++) {
        float dist = length()
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = lightDiffuse * (diff * u_Material.diffuse);

        vec3 viewDir = normalize(u_ViewPos - v_Pos);
        vec3 reflectDir = reflect(-lightDir, norm);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), u_Material.shininess);
        vec3 specular = lightSpecular * (spec * u_Material.specular);
    }

    vec3 result = (ambient + diffuse + specular);
    color = vec4(result, 1.0);

    //color = texture(u_Texture, v_uvCoord);
}