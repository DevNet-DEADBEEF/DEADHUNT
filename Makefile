JAVAC=javac
JAVA=java
CLASSES=Main.java TreasureHunt.java StudentAgent.java
CLASSFILES=$(CLASSES:.java=.class)

all: compile run

compile:
	$(JAVAC) $(CLASSES)

run:
	$(JAVA) Main

clean:
	rm -f *.class
