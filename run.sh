#!/bin/bash
close() {
	trap - SIGINT SIGTERM
	echo Exit
	exit 1
}
trap close SIGINT SIGTERM

printf "Coefficients: "
read l1
l2=600
l3=1
l4=128
l5=2

while getopts ":gzic" opt; do
	case "$opt" in
	g)
		printf "Grid Size: "
		read l2
		;;
	z)
		printf "Zoom Factor: "
		read l3
		;;
	i)
		printf "Iterations: "
		read l4
		;;
	c)
		printf "Contrast: "
		read l5
		;;
	esac
done

printf "%s\n" "$l1" "$l2" $(bc -l <<< "($l2 * $l3 / 2)") "$l4" | ./solver +RTS -N 2>/dev/null | python3 ./render.py "$l4" "$l5" 2>/dev/null
echo Done
