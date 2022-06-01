#type vert
#version 450 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec2 a_uvCoord;
layout (location = 2) in vec3 a_Normal;

out vec2 v_uvCoord;
out vec3 v_Normal;
out vec4 v_Pos;

uniform mat4 u_Projection;
uniform mat4 u_View;
uniform mat4 u_Transform;

void main() {
    vec4 worldPosition = u_Transform * vec4(a_Position, 1.0);
    vec4 positionRelativeToCam = u_View * worldPosition;
    gl_Position = u_Projection * positionRelativeToCam;

    v_uvCoord = a_uvCoord;
    v_Normal = (u_Transform * vec4(a_Normal, 0.0)).xyz;
    v_Pos = worldPosition;
}

#type frag
#version 450 core

layout (location = 0) out vec4 color;

in vec2 v_uvCoord;
in vec3 v_Normal;
in vec4 v_Pos;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

uniform mat4 u_View;

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

const float shineDamper = 32.0;
const float reflectivity = 1.0;

void main() {
    vec3 norm = normalize(v_Normal);
    vec3 unitVectorToCamera = normalize((inverse(u_View) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - v_Pos.xyz);

    vec3 totalDiff = vec3(0.0);
    vec3 totalSpec = vec3(0.0);

    for (int i = 0; i < 4; i++) {
        vec3 toLightVector = u_LightPositions[i] - v_Pos.xyz;
        float dist = length(toLightVector);
        vec3 att = u_LightAttenuations[i];
        float attFactor = att.x + (att.y * dist) + (att.z * dist * dist);

        vec3 unitLightVector = normalize(toLightVector);
        float ndot1 = dot(norm, unitLightVector);
        float brightness = max(ndot1, 0.0);
        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, norm);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, u_Material.shininess);
        totalDiff += (brightness * u_LightColors[i]) / attFactor;
        totalSpec += (dampedFactor * reflectivity * u_LightColors[i]) / attFactor;
    }

    float ambientBrightness = 0.2;
    totalDiff = max(totalDiff, ambientBrightness);

    vec4 sampled = texture(u_Texture, v_uvCoord);

    vec3 result = totalDiff * u_Material.diffuse * sampled.xyz + totalSpec * u_Material.specular * u_Material.ambient*ambientBrightness;
    color = vec4(result, 1.0);

    //color = texture(u_Texture, v_uvCoord);
}