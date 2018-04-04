build: solver clean

solver: solver.hs
	ghc -threaded --make -O2 solver.hs

clean:
	-@rm -f *.o 
	-@rm -f *.hi
