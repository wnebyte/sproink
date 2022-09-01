#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;
layout (location=3) in float aTexId;
layout (location=4) in float aEntityId;
layout (location=5) in float aStatic;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;

void main(){
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;
    if (aStatic == 1.0) {
        gl_Position = uProjection * vec4(aPos, 1.0);
    } else {
        gl_Position = uProjection * uView * vec4(aPos, 1.0);
    }
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];

out vec4 color;

void main() {
    int id = int(fTexId);
    if (id > 0) {
        color = fColor * texture(uTextures[id], fTexCoords);
    } else {
        color = fColor;
    }
}