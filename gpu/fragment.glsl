#version 460

layout(location = 0) uniform vec2 bottom_right;
layout(location = 1) uniform vec2 view_size;

layout(location = 2) uniform int max_iterations;
layout(location = 3) uniform float tolerance;

layout(location = 4) uniform int root_count;
layout(location = 5) uniform sampler1D root_colors;
layout(location = 6) uniform float color_scaling;
layout(location = 7) uniform vec2 offset;
layout(location = 8) uniform vec2 roots[16];

in vec2 texture_coord;
out vec4 color;

vec2 complex_mul(vec2 a, vec2 b)
{
  return vec2(a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x);
}
vec2 complex_div(vec2 a, vec2 b)
{
  return vec2(dot(a, b), a.y*b.x - a.x*b.y) / dot(b, b);
}
void main()
{
  vec2 point = bottom_right + (texture_coord + offset) * view_size;
  int closest_root = -1;
  int iterations = 0;
  while (iterations < max_iterations && closest_root < 0) {
    vec2 f = point - roots[0];
    vec2 f_prime = point - roots[1];
    for (int j = 2; j < root_count; ++j)
      f_prime = complex_mul(f_prime, point - roots[j]);
    for (int i = 1; i < root_count; ++i) {
      f = complex_mul(f, point - roots[i]);
      vec2 a = point - roots[0];
      for (int j = 1; j < root_count; ++j)
        if (i != j)
          a = complex_mul(a, point - roots[j]);
      f_prime += a;
    }
    if (f_prime == vec2(0.f, 0.f)) discard;
    point -= complex_div(f, f_prime);
    for (int k = 0; k < root_count; ++k)
      if (dot(point - roots[k], point - roots[k]) < tolerance) {
        closest_root = k;
        break;
      }
    ++iterations;
  }
  if (closest_root < 0) {
    float best_distance = dot(point - roots[0], point - roots[0]);
    for (int k = 1; k < root_count; ++k) {
      float d = dot(point - roots[k], point - roots[k]);
      if (d < best_distance) {
        best_distance = d;
        closest_root = k;
      }
    }
  }
  color = color_scaling * texelFetch(root_colors, closest_root, 0) / pow(float(iterations),0.5);
  color.a = 1.f;
}

