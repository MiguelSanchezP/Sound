print ("[0/5] Importing libraries")
import struct
import wave
import numpy as np
import matplotlib.pyplot as plt

#VARIABLES
frequency = 261.63
frame_rate = 44100
duration = 5
channels = 1
sample_width = 2
amplitude = 1

print ('[1/5] Creating the values for the waves')
vals_nor = []
vals_inv = []
vals_mer = []
nor = wave.open('waves/normals/'+str(frequency)+'.wav', 'wb')
inv = wave.open('waves/inverses/'+str(frequency)+'.wav', 'wb')
mer = wave.open('waves/merges/'+str(frequency)+'.wav', 'wb')
for i in range (int(frame_rate*duration)):
	vals_nor.append(amplitude*np.sin(2*np.pi*frequency*(i/frame_rate)))
	vals_inv.append(amplitude*np.sin(2*np.pi*frequency*(i/frame_rate))*(-1))
	vals_mer.append(vals_nor[i]+vals_inv[i])

print ("[2/5] Defining waves' parameters")
nor.setnchannels(channels)
inv.setnchannels(channels)
mer.setnchannels(channels)
nor.setsampwidth(sample_width)
inv.setsampwidth(sample_width)
mer.setsampwidth(sample_width)
nor.setframerate(frame_rate)
inv.setframerate(frame_rate)
mer.setframerate(frame_rate)
nor.setnframes(len(vals_nor))
inv.setnframes(len(vals_inv))
mer.setnframes(len(vals_mer))
nor.setcomptype('NONE', 'not compressed')
inv.setcomptype('NONE', 'not compressed')
mer.setcomptype('NONE', 'not compressed')

print ("[3/5] Exporting the waves' raws")
raw_nor = open('raws/normals/'+str(frequency)+'.txt', 'w+')
raw_inv = open('raws/inverses/'+str(frequency)+'.txt', 'w+')
raw_mer = open('raws/merges/'+str(frequency)+'.txt', 'w+')
for i in range (int(frame_rate*duration)):
	raw_nor.write(str(vals_nor[i])+'\n')
	raw_inv.write(str(vals_inv[i])+'\n')
	raw_mer.write(str(vals_mer[i])+'\n')
raw_nor.close()
raw_inv.close()
raw_mer.close()

print ("[4/5] Writing the wav files")
for i in range (int(frame_rate*duration)):
	nor.writeframes(struct.pack('h', int(vals_nor[i]*32767)))
	inv.writeframes(struct.pack('h', int(vals_inv[i]*32767)))
	mer.writeframes(struct.pack('h', int(vals_mer[i]*32767)))
nor.close()
inv.close()
mer.close()

print ("[5/5] Plotting")
x = np.linspace(0, duration, num=int(frame_rate*duration))
plt.plot(x, vals_nor[:], 'b')
plt.plot(x, vals_inv[:], 'r')
plt.plot(x, vals_mer[:], 'g')
plt.title('Wave representation')
plt.xlabel('Time (s)')
plt.ylabel('Amplitude')
plt.show()
