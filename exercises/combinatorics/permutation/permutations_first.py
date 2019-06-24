# python3 -m permutations_first --sequence 12 --count 5

from pprint import pprint
from timeit import Timer
from operator import mul
from functools import reduce
from math import factorial
import argparse

import itertools

def permutations(itter, count):
    """
    The first try for realization calculate all combinations of some
    sequence for some count of count.

    Count of results:

        С (count/len(itter)) = !len(itter) / (!(len(itter) - count))

    Big O:

        O(!N)
    """
    result = []
    itter = list(itter)
    i = 0

    for _ in range(factorial(len(itter))):
        itter[i], itter[i + 1] = itter[i + 1], itter[i]
        result.append(tuple(itter))
        i += 1
        if i == len(itter) - 1:
            i = 0

    return set([item[:count] for item in result])


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--sequence', type=list, default='ABС')
    parser.add_argument('--count', type=int, default=2)

    args = parser.parse_args()

    print('\nResult:\n')
    pprint(permutations(args.sequence, args.count))

    # a performance
    print("\nPerformance tests:\n")

    def _test(value):
        time = Timer(lambda: permutations(value, 2)).repeat(repeat=10, number=1)
        response = sum(time) / len(time)

        print(f'    Time {value}: ', response)

        return response

    first_value = '12345'
    response_first = _test(first_value)

    second_value = '12345678'
    response_secound = _test(second_value)

    print(
        "   ",
        "Total real: ",
        round(response_secound / response_first),
        'Total predict',
        round(
            factorial(len(second_value)) /
            factorial(len(first_value))
        ),
        '\n'
    )
