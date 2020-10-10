import matplotlib.pyplot as plt

f = open ('FFTfrequencies.txt', 'r')
f2 = open ('FFTvalues.txt', 'r')

values = []
frequencies = []
for line in f:
	frequencies.append(int(line))
for line in f2:
	values.append(float(line))
f.close()
f2.close()

plt.stem(frequencies, values, markerfmt=' ', use_line_collection=True)
plt.xlabel('Frequency [Hz]')
plt.ylabel('Amplitude')
plt.title('FFT of a recording')
plt.show()
