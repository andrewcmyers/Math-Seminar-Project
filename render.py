import sys
from scipy.misc import imsave
from random import random

try:
    def new_color():
        return [int(random() * 256) for _ in range(3)]

    colors = {}
    image = []
    for (i, x) in enumerate(sys.stdin):
        for y in x.split():
            pixel = [p for p in y[1:-1].split(',')]
            symbol = pixel[0]
            if symbol not in colors:
                colors[symbol] = new_color()
            if i == len(image):
                image.append([])
            image[i].append([c * ((int(pixel[1]) / float(sys.argv[1])) ** float(sys.argv[2])) for c in colors[symbol]])

    imsave("output.png", image)
except:
    pass
