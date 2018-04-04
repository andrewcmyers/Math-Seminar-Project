## Haskell Version
Generates a png representation of the Newton-Raphson convergence of the complex plane toward the roots of a polynomial. Use run.sh to generate the image file, output.png (may take several minutes). The -g, -z, -i, and -c options can be used to specify grid size, zoom, iterations, and contrast, respectively.

Ex.

`./run.sh`

`Coefficients: 1 0 0 -1`

Requires bash, python3, numpy, scipy, and Pillow.

## Python Version

Checkout the python branch (very slow).

## GPU

Requires pygame and PyOpenGL.

Does not create images yet.

`python newton_gpu.py`
