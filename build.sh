#!/bin/bash
ghc -threaded --make -O2 solver.hs
rm *.o 2>/dev/null
rm *.hi 2>/dev/null
