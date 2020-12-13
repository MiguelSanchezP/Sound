import matplotlib.pyplot as plt
import numpy as np

f = open ('./track.txt', 'r')
f2 = open ('./analysis.txt', 'r')

track_times = []
analysis_times = []

for line in f:
	track_times.append(line)
for line in f2:
	analysis_times.append(line)

new_track_times = []
new_analysis_times = []
quocients = []

for i in range(3500):
	new_track_times.append(int(track_times[i]))
	new_analysis_times.append(int(analysis_times[i]))
	quocients.append(int(track_times[i])/int(analysis_times[i]))

plot1 = plt.figure(1)
plt.plot(new_analysis_times, new_track_times, 'r*')
plt.plot(quocients, 'b*')
plt.axis('equal')

data = [new_track_times, new_analysis_times, quocients]

plot2 = plt.figure(2)
plt.boxplot(data)
plt.show()

