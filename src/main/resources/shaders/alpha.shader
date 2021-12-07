#shader vert
#version 120

void main(void) {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    // pos = gl_Vertex.xyz;
    // normal = gl_Normal;
}

#shader frag
// the lack of version definition is on purpose as I don't know what webgl version shadertoy uses
// will fix later when i figure it out :P
uniform sampler2D sampler;

void main(void) {
    vec4 color = texture(sampler, gl_TexCoord[0].xy);

    if (color.a <= 0.1) {
        discard; // discard fragments so that they are not written to the framebuffer, they wouldn't be visiable anyways!
    }

    gl_FragColor = color;
}