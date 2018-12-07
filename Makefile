JCC = javac
JFLAGS = -g
default: keywordcounter.class

keywordcounter.class: keywordcounter.java
	$(JCC) $(JFLAGS) keywordcounter.java
clean:
	rm -f *.class
