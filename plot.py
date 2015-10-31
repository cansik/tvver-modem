import os
import matplotlib.pyplot as plt

with open('/Users/cansik/git/fhnw/tvvr/tvver-modem/plot.data') as f:
    lines = f.readlines()
    values = map(lambda l: l.strip().split(','), lines)

    values = filter(lambda v: len(v) > 1, values)

    xv = map(lambda v: v[0], values)
    yv = map(lambda v: v[1], values)

    plt.plot(xv, yv, 'b.-')
    plt.ylim([-1,1])
    plt.xlim([0,len(yv)])
    plt.ylabel('amplitude')
    plt.xlabel('time')
    plt.grid()
    plt.show()
