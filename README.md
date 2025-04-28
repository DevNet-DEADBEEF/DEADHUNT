# Project

20x20x20 Cube of 9 hints and 1 goal

## Algorithm Overview

- Random start
- Walk to first hint
- Get list of coords of prev hint positions
- Walk through list until we find the goal

## Random walk methods (100k iterations each)

==== Balloon ====<br>
Avg steps: 3989.3402 (10.660)<br>
==== Ballon Inverse ====<br>
Avg steps: 4002.53577 (-2.536)<br>
==== Dumb ====<br>
Avg steps: 4006.89292 (-6.893)<br>
==== Dumb Rand ====<br>
Avg steps: 3997.26943 (2.731)<br>
==== Rand Sample ====<br>
Avg steps: 7993.41377 (-3993.414)<br>

## Steps to first hint

Using the balloon method, this compares the number of steps required to find the first of 9 hints

==== Center ====<br>
Avg steps: 727.27534<br>
==== Random ====<br>
Avg steps: 722.96177<br>

These first steps to find the first hint take ~75% of the total steps.
The optimization of the rest of the algorithm results in 
changing of about 25% of the final score.
Unfortunately, the first process is pretty much unable to be optimized.
With that assumption and that it takes 723 steps to find the first hint, 
the maximum score is ~147.275.

## Impact of +5 Score bonus

Regular score function is as follows:

```
        100000 
f(x) =  ------
         x + 1

f(steps) = score
```

Steps with score bonus function

```
        100005 - x 
g(x) =  ---------- 
           x - 5

g(score + 5) = steps
```

- At 1k steps, the diff in steps is ~5% and dropping
- At 8k steps, the diff is only ~40%

The percent difference equation roughly follows

```
        35                             
p(x) = ----  *  (x  -  8000)  +  40.005
       7000

p(steps) = % diff * 100
```

This also assumes the `run()` functions always finds the goal