print ('[0/3] Importing libraries')
import struct
import wave

frame_rate = 44100
channels = 1
sample_width = 2
f = open('file.txt', 'r')
vals = []


print ('[1/3] Append values from file')
for line in f:
	 vals.append(int(line))
f.close()

print ('[2/3] Declare parameters of the wave')
wav = wave.open('waves/export.wav', 'wb')
wav.setnchannels(channels)
wav.setsampwidth(sample_width)
wav.setframerate(frame_rate)
wav.setnframes(len(vals))
wav.setcomptype('NONE', 'not compressed')

print ('[3/3] Write the wav file')
for v in vals:
	wav.writeframes(struct.pack('h', v))
wav.close()

