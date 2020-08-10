print ('[0/2] Importing libraries')
import matplotlib.pyplot as plt
import numpy as np

frame_rate = 44100
file = open ('file.txt', 'r')
y = []

print ('[1/2] Append values from file')
for line in file:
	y.append(int(line))
file.close()

print ('[2/2] Plotting')
x = np.linspace (0, len(y)/frame_rate, num=len(y))
plt.plot(x, y)
plt.title('Plot of file.txt')
plt.xlabel('Time (s)')
plt.ylabel('Amplitude')
plt.show()
