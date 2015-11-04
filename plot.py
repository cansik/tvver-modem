import os
import matplotlib.pyplot as plt
from matplotlib import colors


def readData(filename):
    with open('/Users/cansik/git/fhnw/tvvr/tvver-modem/' + filename) as f:
        lines = f.readlines()
        values = map(lambda l: l.strip().split(','), lines)

        values = filter(lambda v: len(v) > 1, values)

        xv = map(lambda v: v[0], values)
        yv = map(lambda v: v[1], values)

        return xv,yv


graph_colors = ['b', 'r', 'g', 'k', 'm']

maxY = 0
i = 1;
while(os.path.exists('plot%s.data' % i)):
    xv, yv = readData('plot%s.data' % i)
    maxY = len(yv)
    plt.plot(xv, yv, 'b.-', label='Plot %s' % i, color=graph_colors[i-1])
    i += 1

plt.ylim([-1.2,1.2])
plt.xlim([0,maxY])
plt.ylabel('amplitude')
plt.xlabel('time')
plt.legend(loc='best')
plt.grid()
plt.show()
