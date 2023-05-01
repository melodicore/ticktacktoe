#ifdef GL_ES
	precision mediump float;
	precision mediump int;
#endif

uniform sampler2D u_texture;
uniform float u_smoothing;
varying vec4 v_color;
varying vec2 v_texCoords;
const float outlineDistance = 0.425;
const vec4 outlineColor = vec4(0,0,0,1);

void main() {
	if (u_smoothing > 0.0) {
		float smoothing = 0.25 / u_smoothing;
		float distance = texture2D(u_texture, v_texCoords).a;
		float outlineFactor = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
		vec4 color = mix(outlineColor, v_color, outlineFactor);
    float alpha = smoothstep(outlineDistance - smoothing, outlineDistance + smoothing, distance);
		gl_FragColor = vec4(color.rgb, alpha * v_color.a);
	} else {
		gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
	}
}
