#version 460

vec2 vertices[4] = vec2[](
  vec2(0.f, 0.f),
  vec2(1.f, 0.f),
  vec2(1.f, 1.f),
  vec2(0.f, 1.f)
);

out vec2 texture_coord;
void main()
{
  gl_Position = vec4(2.f*vertices[gl_VertexID] - 1.f, 0.f, 1.f);
  texture_coord = vertices[gl_VertexID];
}
