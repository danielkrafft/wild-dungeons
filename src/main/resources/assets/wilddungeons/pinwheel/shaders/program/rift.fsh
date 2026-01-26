#version 330 core
#line 0 1
#line 3 0

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float GameTime;

uniform float FogEnd;
uniform vec4 FogColor;
in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
out vec4 fragColor;

/* ================= CONSTANTS ================= */

const vec2 Resolution = vec2(250.0, 250.0);

/* ================= SHADERTOY NOISE ================= */

vec4 permute_3d(vec4 x) {
    return mod(((x * 34.0) + 1.0) * x, 289.0);
}

vec4 taylorInvSqrt3d(vec4 r) {
    return 1.79284291400159 - 0.85373472095314 * r;
}

float simplexNoise3d(vec3 v) {
    const vec2 C = vec2(1.0 / 6.0, 1.0 / 3.0);
    const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);

    vec3 i  = floor(v + dot(v, C.yyy));
    vec3 x0 = v - i + dot(i, C.xxx);

    vec3 g = step(x0.yzx, x0.xyz);
    vec3 l = 1.0 - g;
    vec3 i1 = min(g.xyz, l.zxy);
    vec3 i2 = max(g.xyz, l.zxy);

    vec3 x1 = x0 - i1 + 1.0 * C.xxx;
    vec3 x2 = x0 - i2 + 2.0 * C.xxx;
    vec3 x3 = x0 - 1.0 + 3.0 * C.xxx;

    i = mod(i, 289.0);
    vec4 p = permute_3d(
        permute_3d(
            permute_3d(i.z + vec4(0.0, i1.z, i2.z, 1.0))
            + i.y + vec4(0.0, i1.y, i2.y, 1.0)
        )
        + i.x + vec4(0.0, i1.x, i2.x, 1.0)
    );

    float n_ = 1.0 / 7.0;
    vec3 ns = n_ * D.wyz - D.xzx;

    vec4 j = p - 49.0 * floor(p * ns.z * ns.z);

    vec4 x_ = floor(j * ns.z);
    vec4 y_ = floor(j - 7.0 * x_);

    vec4 x = x_ * ns.x + ns.yyyy;
    vec4 y = y_ * ns.x + ns.yyyy;
    vec4 h = 1.0 - abs(x) - abs(y);

    vec4 b0 = vec4(x.xy, y.xy);
    vec4 b1 = vec4(x.zw, y.zw);

    vec4 s0 = floor(b0) * 2.0 + 1.0;
    vec4 s1 = floor(b1) * 2.0 + 1.0;
    vec4 sh = -step(h, vec4(0.0));

    vec4 a0 = b0.xzyw + s0.xzyw * sh.xxyy;
    vec4 a1 = b1.xzyw + s1.xzyw * sh.zzww;

    vec3 p0 = vec3(a0.xy, h.x);
    vec3 p1 = vec3(a0.zw, h.y);
    vec3 p2 = vec3(a1.xy, h.z);
    vec3 p3 = vec3(a1.zw, h.w);

    vec4 norm = taylorInvSqrt3d(
        vec4(dot(p0, p0), dot(p1, p1), dot(p2, p2), dot(p3, p3))
    );

    p0 *= norm.x;
    p1 *= norm.y;
    p2 *= norm.z;
    p3 *= norm.w;

    vec4 m = max(
        0.6 - vec4(
        dot(x0, x0),
        dot(x1, x1),
        dot(x2, x2),
        dot(x3, x3)
        ),
        0.0
    );
    m *= m;

    return 42.0 * dot(
        m * m,
        vec4(
        dot(p0, x0),
        dot(p1, x1),
        dot(p2, x2),
        dot(p3, x3)
        )
    );
}

float fbm3d(vec3 x, int it) {
    float v = 0.0;
    float a = 0.5;
    vec3 shift = vec3(100.0);

    for (int i = 0; i < 32; i++) {
        if (i < it) {
            v += a * simplexNoise3d(x);
            x = x * 2.0 + shift;
            a *= 0.5;
        }
    }
    return v;
}

vec3 rotateZ(vec3 v, float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return vec3(
    v.x * c - v.y * s,
    v.x * s + v.y * c,
    v.z
    );
}

float facture(vec3 v) {
    vec3 n = normalize(v);
    return max(max(n.x, n.y), n.z);
}

vec3 emission(vec3 c, float s) {
    return c * s;
}

/* ================= MAIN ================= */

void main() {
    float Time = GameTime * 680.0;

    vec2 fragCoord = texCoord0 * Resolution;
    vec2 uv = (fragCoord * 2.0 - Resolution) / Resolution.y;

    float radius = length(uv);

    vec3 color = vec3(uv, 0.0);
    color.z += 0.8;

    color = normalize(color);
    color -= 0.5 * vec3(0.0, 0.0, Time);

    float angle = -log2(radius);
    color = rotateZ(color, angle);

    float frequency = 0.6;
    float distortion = 0.01;

    color.x = fbm3d(color * frequency + 0.0, 5) + distortion;
    color.y = fbm3d(color * frequency + 1.0, 5) + distortion;
    color.z = fbm3d(color * frequency + 2.0, 5) + distortion;

    vec3 noiseColor = color;

    noiseColor *= 2.0;
    noiseColor -= 0.1;
    noiseColor *= 0.188;
    noiseColor += vec3(uv, 0.0);

    float len = length(noiseColor);
    len = 0.770 - len;
    len *= 4.2;

    vec3 emissionColor = emission(vertexColor.rgb, len * 0.4);

    float fac = radius - facture(color + 0.32);
    fac += 0.1;
    fac *= 3.0;

    color = mix(emissionColor, vec3(fac), fac + 1.2);

    // === TRANSPARENCY FIX ===
    float alpha = smoothstep(1.0, 0.55, radius*1.1);
    float cir = smoothstep(1.0, 0.85, radius*1.8);

    vec4 ccc = vec4(color, alpha);

    if (alpha <= 0.001) ccc = vec4(0.,0.,0.,0.);
    if (cir <= 0.001) {
        if (color.r >= 0.35 && color.g >= 0.35 && color.b >= 0.35) ccc = vec4(0.,0.,0.,0.);
    }

    fragColor = ccc;
}