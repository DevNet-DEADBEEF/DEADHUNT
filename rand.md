- Class Declaration
  - seed =
    - (8682522807148012L * 1181783497276652981L)
    - xor nanoTime
    - xor 0x5DEECE66DL
    - and 0x800000000000
- bounded rand int (bound - 1 % 2 == 0)
  - next 31 bits
  - `bound * (31 bits) >> 31`
  - only keeps the 5 left most bits
- bounded rand int
  - `r = next 31 bits`
  - while `r - (r % bound) + bound - 1 < 0`
    - `r = next 31 bits`
  - returns `r % bound`
```java
int next(31) {
    new_seed = (seed.get() * 0x5DEECE66DL + 0xB) & 0x800000000000;
    return (new_seed >>> (48 - 31)); // seed unsigned bit shifted
}
```

- Using only the goal ints:

randInt:
- 0bxxxxx??????????????????????????
- 0bxxxxx??????????????????????????
- 0bxxxxx??????????????????????????
15 known bits of 93 total

Treasure Hunt:
- goal: 3 randint(b)
- hint: 9*3 randint(b)
- halfspan: randint(b)