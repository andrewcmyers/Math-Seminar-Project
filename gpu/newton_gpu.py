import pygame
from OpenGL.GL import *
from ctypes import *

WINDOW_WIDTH = 800
WINDOW_HEIGHT = 800

def load_shader_from_file(path, shader_type):
    file = open(path)
    shader_source = file.read()

    shader_id = glCreateShader(shader_type);
    glShaderSource(shader_id, shader_source);
    glCompileShader(shader_id)

    shader_compiled = glGetShaderiv(shader_id, GL_COMPILE_STATUS)
    if (shader_compiled != GL_TRUE):
        print("Failed to compile shader:\n%s\n" % shader_source)
        log = glGetShaderInfoLog(shader_id)
        print(log)
        glDeleteShader(shader_id)
        return 0
    return shader_id

num_roots = int(input("number of roots (max 10): "))
root_input = [input("root %d: " % (i+1)) for i in range(num_roots)]
roots = (c_float * (num_roots * 2))()
for i in range(num_roots):
    roots[2*i] = c_float(float(root_input[i].split(' ')[0]))
    roots[2*i+1] = c_float(float(root_input[i].split(' ')[1]))
min_real = c_float(float(input("minimum real: ")))
min_imag = c_float(float(input("minimum imaginary: ")))
size_real = c_float(float(input("real range: ")))
size_imag = c_float(float(input("imaginary range: ")))

tolerance = c_float(float(input("tolerance: ")))
max_iterations = c_int(int(input("max iterations: ")))
samples = int(input("supersampling: "))

pygame.init()
pygame.display.set_mode((WINDOW_WIDTH,WINDOW_HEIGHT),pygame.OPENGL|pygame.DOUBLEBUF)

pygame.display.set_caption("Newton GPU")

palette_data = (c_char * 30)(
    0xff, 0x00, 0x00,
    0x00, 0xff, 0x00,
    0x00, 0x00, 0xff,
    0xff, 0xff, 0x00,
    0x00, 0xff, 0xff,
    0xff, 0x00, 0xff,
    0xff, 0x80, 0x00,
    0x00, 0xff, 0x80,
    0x80, 0x00, 0xff,
    0xbb, 0xbb, 0xbb,
)
palette_texture = glGenTextures(1)
glBindTexture(GL_TEXTURE_1D, palette_texture)
glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
glTexStorage1D(GL_TEXTURE_1D, 1, GL_RGB8, 256)
glTexSubImage1D(GL_TEXTURE_1D, 0, 0, len(palette_data)/3, GL_RGB, GL_UNSIGNED_BYTE, palette_data)
glBindTexture(GL_TEXTURE_1D, 0)

vertex_shader = load_shader_from_file("vertex.glsl", GL_VERTEX_SHADER)
fragment_shader = load_shader_from_file("fragment.glsl", GL_FRAGMENT_SHADER)
if vertex_shader == 0 or fragment_shader == 0:
    glDeleteShader(vertex_shader)
    glDeleteShader(fragment_shader)
    print("Failed to create shaders")
program = glCreateProgram()
glAttachShader(program, vertex_shader)
glAttachShader(program, fragment_shader)
glLinkProgram(program)

link_status = glGetProgramiv(program, GL_LINK_STATUS)
if (link_status != GL_TRUE):
    print("Failed to link program")
    log = glGetProgramInfoLog(program)
    print(log)
    glDeleteShader(vertex_shader)
    glDeleteShader(fragment_shader)
    glDeleteProgram(program)
glDeleteShader(vertex_shader)
glDeleteShader(fragment_shader)

glUseProgram(program)

glClearColor(0.0, 0.0, 0.0, 1.0)
glClear(GL_COLOR_BUFFER_BIT)
glBindTexture(GL_TEXTURE_1D, palette_texture)

glUniform2f(0, min_real,min_imag)#bottom_right
glUniform2f(1, size_real,size_imag)#view_size
glUniform1i(2, max_iterations)#iterations
glUniform1f(3, tolerance)#tolerance
glUniform1i(4, num_roots)#root_count
glUniform1i(5, 0)#root_colors
glUniform1f(6, 1.0/samples/samples)#color_scaling
glUniform2fv(8,num_roots,byref(roots))#roots

glEnable(GL_BLEND)
glBlendFunc(GL_ONE, GL_ONE)
for x in range(samples):
    for y in range(samples):
        glUniform2f(7, 2.0*x/WINDOW_WIDTH/samples - 1.0/WINDOW_WIDTH, 2.0*y/WINDOW_HEIGHT/samples - 1.0/WINDOW_HEIGHT)#offset
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4)

pygame.display.flip()

running = True
while running:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

pygame.quit()
