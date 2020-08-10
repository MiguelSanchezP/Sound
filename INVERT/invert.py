print ('[0/4] Importing libraries')
import numpy as np
import matplotlib.pyplot as plt
import wave
import struct

channels = 1
sample_width = 2
frame_rate = 44100

wav = wave.open('waves/inverted.wav', 'wb')
f = open('exports/file.txt', 'r')
f2 = open('inverts/inverted.txt', 'w+')
frames = []
frames_raw = []

print ('[1/4] Append file values to variables')
for line in f:
	frames_raw.append(int(line))
	frames.append(int(line)*(-1))
	f2.write(str(int(line)*(-1)) +'\n')

print ('[2/4] Define the wave parameters')
wav.setnchannels(channels)
wav.setsampwidth(sample_width)
wav.setframerate(frame_rate)
wav.setnframes(len(frames))
wav.setcomptype('NONE', 'not compressed')

print ('[3/4] Write the wav file')
for frame in frames:
	wav.writeframes(struct.pack('h', int(frame)))

wav.close()
f.close()
f2.close()

print ('[4/4] Plotting')
x = np.linspace(0, len(frames)/frame_rate, num=len(frames))
plt.plot(x, frames, 'b')
plt.plot(x, frames_raw, 'r')
plt.title('Inversion of a sound file')
plt.xlabel('Time (s)')
plt.ylabel('Amplitude')
plt.show()
