#version 150

uniform sampler2D Sampler0;

uniform int Target;

in vec4 vertexColor;
in vec2 texCoord0;
flat in ivec2 texCoord2;

out vec4 fragColor;

void main()
{
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;

    if (color.a < 0.1)
    {
        discard;
    }

    fragColor = vec4(float(Target + texCoord2.x) / 255.0, 0.0, 0.0, 1.0);
}
