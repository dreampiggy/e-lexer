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

# Start

```bash
> Input the content-free grammar, you can use 'Enter' to separate lines.
Use :q to stop input.

$ S -> A
A -> B | a
B -> b

> Now, input the token you want to recognize and press enter.
Use EOF to stop.

$ a

> Parse result: true
```

# Method

> You can choose either LL(1) method or LR(1) method. We build a common `Parser` Interface to receive the object

```java
public interface Parser {
    public void init();
    public void log();
    public boolean parse(List<Symbol> tokens);
}

Parser parser = new LRParser(grammar);
```

## LL(1)

> First we need to generate `nullable` `first` `follow` and `select` set(some are also need for LR(1))
> 
> Next we just construct LL(1) table, to simplify, use ` Map<Symbol, Map<Symbol, List<Symbol>>` instead of two-dimentional array and for better time-performance

## LR(1)

> Use the generate closure method to calculate `canonical` and then use this to build LR(1) table
> 
> Current we build state into a big Object which contains all the `Production`, like this:

```java
public class State {
    private boolean marked = false;
    private Set<Production> closure;
    private static State start;
```
> Because the `Production` is useful during table generation, but after that, we can just use a single `int` to represent the state for better space-performance

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

### Left recursion(for LR(1))

S{1} -> S{1}A

![](http://latex.codecogs.com/gif.latex?$S_1 \\rightarrow S_1A$)

	$S_1 \rightarrow S_1A$

### Complicated
F{*} -> S{1}e\e | F123

![](http://latex.codecogs.com/gif.latex?$F^* \\rightarrow S_1e \\epsilon \\mid F123$)

	$F^* \rightarrow S_1e \epsilon \mid F123$
