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
quotients = []

for i in range(3501):
	new_track_times.append(int(track_times[i]))
	new_analysis_times.append(int(analysis_times[i]))
	quotients.append(int(track_times[i])/int(analysis_times[i]))

plot1 = plt.figure(1)
plt.plot(new_analysis_times, new_track_times, 'r*')
plt.plot(quotients, 'b*')
plt.axis('equal')
plt.xlabel ("Analysis function execution time in ns")
plt.ylabel ("Additional functions execution time in ns")
plt.title ("Execution times")

data = [new_track_times, new_analysis_times, quotients]
quotients.sort()

print ("Mean: " + str(np.mean(quotients[quotients.index(np.percentile(quotients, 25)):quotients.index(np.percentile(quotients, 75))])))
print ("Median: " + str(np.median(quotients)))
print ("Second quartile: From " + str(np.percentile(quotients, 25)) + " to " + str(np.percentile(quotients, 50)))
print ("Third quartile: From " + str(np.percentile(quotients, 50)) + " to " + str(np.percentile(quotients, 75)))

plot2 = plt.figure(2)
plt.boxplot(data)
plt.xticks([1,2,3], ["Analysis", "Additional", "Quotients"])
plt.title ("Box plots")
plt.show()

