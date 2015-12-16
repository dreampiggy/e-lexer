# e-lexer

[![License](https://img.shields.io/dub/l/vibe-d.svg)](https://opensource.org/licenses/MIT)
[![Star](https://img.shields.io/github/stars/lizhuoli1126/e-lexer.svg?style=social&label=Star)](https://github.com/lizhuoli1126/e-lexer)

> A sample lexer for formula-friendly input

# Build
[IntelliJ IDEA](https://www.jetbrains.com/idea/)

Click "Build" button

**or**

```bash
ant comlile
```

# Test
> ensure the test folder exist in your project

[IntelliJ IDEA](https://www.jetbrains.com/idea/)

Click "Test" button

**or**

```bash
ant runtest
```

# Grammar


### Simple
S -> A

![](http://latex.codecogs.com/gif.latex?S \\rightarrow A)

	$S \rightarrow A$

### Or ops
S -> a | 1

![](http://latex.codecogs.com/gif.latex?$S \\rightarrow a \\mid 1$)

	$S \rightarrow a \mid 1$

### Epsilon
F -> \e

![](http://latex.codecogs.com/gif.latex?$F \\rightarrow \\epsilon$)

	$F \rightarrow \epsilon$

### Non-terminal to non-terminal
S{1} -> F

![](http://latex.codecogs.com/gif.latex?$S_1 \\rightarrow F$)

	$S_1 \rightarrow F$

### Left recursion

S{1} -> S{1}A

![](http://latex.codecogs.com/gif.latex?$S_1 \\rightarrow S_1A$)

	$S_1 \rightarrow S_1A$

### Complicated
F{*} -> S{1}e\e | F123

![](http://latex.codecogs.com/gif.latex?$F^* \\rightarrow S_1e \\epsilon \\mid F123$)

	$F^* \rightarrow S_1e \epsilon \mid F123$
