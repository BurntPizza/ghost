ghost
=====

#### The programming language that's barely there

Jokes aside, **ghost** is the working name of a small programming language I'm developing primarily for fun.

#### Example
<hr>
Compute and print the 20th Fibonacci number:
```
[dup [drop 1] [dup 2 - fib swap 1 - fib +] rot3 3 < if] fib def
20 fib print
```

#### Basics
<hr>
Ghost is a concatenative, stack based language partly inspired by [Forth](http://en.wikipedia.org/wiki/Forth_(programming_language)).
This means that all program elements are functions that take a stack as an argument, and return a stack as a result.
The returned stack is not necessarily the same stack that was consumed, although there is no semantic difference between the two models. There is a mathematical difference though: treating the consumed stack and returned stack as different means that most functions will be pure, even if the modify the stack. This has clever and interesting consequences.
For more about stack languages: http://evincarofautumn.blogspot.com/2012/02/why-concatenative-programming-matters.html

A program is simply the composition of its constituent functions, as are functions themselves, if they are composite functions.
Thus, to facilitate easy composition, ghost's syntax is in postfix notation:
```
1 2 + print
```
This short program consists of four functions: `1`, `2`, `+`, and `print`. Since they are in postfix notation, they are evaluated left to right: `1` pushes an number onto the stack, as does `2`, `+` pops two numbers, adds them, and pushes the result back, and `print` peeks the top of the stack and prints it to standard out. This program will output `3`.


\< further documentation to come \>
