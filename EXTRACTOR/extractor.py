print ('[0/6] Importing libraries')
import struct
import wave
import numpy as np
import matplotlib.pyplot as plt

#VARIABLES
frame_rate = 44100

print ('[1/6] Getting wave frames')
sound = wave.open('waves/sound.wav', 'r')
huge_mess = str(sound.readframes(sound.getnframes()))
sound.close()
big_mess = huge_mess[2:len(huge_mess)-1]
little_mess = []
i = 0

print ('[2/6] Splitting frames int a bytes array')
while i < len(big_mess):
	if big_mess[i] == '\\' and big_mess[i+1] == 'x':
		little_mess.append(big_mess[i:i+4])
		i = i+4
	elif big_mess[i] == '\\' and big_mess[i+1] != 'x':
		little_mess.append(big_mess[i:i+2])
		i = i+2
	elif big_mess[i] != '\\':
		little_mess.append(big_mess[i])
		i = i+1

print ('[3/6] Grouping bytes array by 2-byte size chunks')
tiny_mess=[]
for i in range(int(len(little_mess)/2)):
	s = little_mess[2*i]+little_mess[2*i+1]
	tiny_mess.append(s)
no_longer_mess = []

print ('[4/6] Decoding the bytes into ints')
for tm in tiny_mess:
	no_longer_mess.append(int.from_bytes(bytes(bytes(tm, 'ISO-8859-1').decode('unicode-escape'), 'ISO-8859-1'), 'little'))
sin_vals=[]
print ('[5/6] Converting to sinusodial normal values')
for nlm in no_longer_mess:
	if nlm > 32767:
		nlm = nlm-2*32768
	sin_vals.append(nlm/32768)
f = open('exports/exported.txt', 'w+')
for sv in sin_vals:
	f.write(str(sv)+'\n')
f.close()

print ('[6/6] Plotting')
plt.title('Wave representation')
plt.xlabel('Time')
plt.ylabel('Amplitude')
x = np.linspace (0, len(sin_vals)/frame_rate, num=len(sin_vals))
plt.plot (x, sin_vals[:])
plt.show()
