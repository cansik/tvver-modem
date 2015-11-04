import os
import matplotlib.pyplot as plt

def readData(filename):
    with open('/Users/cansik/git/fhnw/tvvr/tvver-modem/' + filename) as f:
        lines = f.readlines()
        values = map(lambda l: l.strip().split(','), lines)

        values = filter(lambda v: len(v) > 1, values)

        xv = map(lambda v: v[0], values)
        yv = map(lambda v: v[1], values)

        return xv,yv


xv1, yv1 = readData('plot.data')
plt.plot(xv1, yv1, 'b.-')

if(os.path.exists('plot2.data')):
    xv2, yv2 = readData('plot2.data')
    plt.plot(xv2, yv2, 'r.-')

plt.ylim([-1,1])
plt.xlim([0,len(yv1)])
plt.ylabel('amplitude')
plt.xlabel('time')
plt.grid()
plt.show()
