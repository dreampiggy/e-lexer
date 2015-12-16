# e-lexer

> A sample lexer for formula-friendly input

# Build
[IntelliJ IDEA](https://www.jetbrains.com/idea/)

or

```bash
gradle clean
gradle build
```

# Grammar

S => A

![](http://latex.codecogs.com/gif.latex?S \\rightarrow A)

	$S \rightarrow A$
-
S => a | 1

![](http://latex.codecogs.com/gif.latex?$S \\rightarrow a \\mid 1$)

	$S \rightarrow a \mid 1$
-
F => \e

![](http://latex.codecogs.com/gif.latex?$F \\rightarrow \\epsilon$)

	$F \rightarrow \epsilon$
-
S{1} => F

![](http://latex.codecogs.com/gif.latex?$S_1 \\rightarrow F$)

	$S_1 \rightarrow F$
-
S{1} => S{1}A

![](http://latex.codecogs.com/gif.latex?$S_1 \\rightarrow F_1A$)

	$S_1 \rightarrow F_1A$
-
F{*} => S{1}e\e | F123

![](http://latex.codecogs.com/gif.latex?$F^* \\rightarrow S_1e \\epsilon \\mid F123$)

	$F^* \rightarrow S_1e \epsilon \mid F123$
