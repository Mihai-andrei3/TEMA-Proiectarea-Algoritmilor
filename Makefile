# Exemplu de Makefile pentru soluții scrise în Java.

.PHONY: build clean

build: Supercomputer.class Ferate.class Teleportare.class Magazin.class

# Nu compilați aici, nici măcar ca dependențe de reguli.
run-p1:
	java Supercomputer
run-p2:
	java Ferate
run-p3:
	java Teleportare
run-p4:
	java Magazin

# Schimbați numele surselor și ale binarelor (peste tot).
Supercomputer.class: Supercomputer.java
	javac $^
Ferate.class: Ferate.java
	javac $^
Teleportare.class: Teleportare.java
	javac $^
Magazin.class: Magazin.java
	javac $^

# Vom șterge fișierele bytecode compilate.
clean:
	rm -f *.class
