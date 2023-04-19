# sbox =   [0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
#           0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
#           0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
#           0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
#           0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
#           0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
#           0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
#           0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
#           0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
#           0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
#           0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
#           0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
#           0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
#           0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
#           0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
#           0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16]
# ## S-Box nanti bisa diganti, masih pakai yang AES S-BOX
import hashlib

class KeyExpansion:
    def __init__(self, externalKey, countRound):
        self.seed = externalKey
        self.externalKey = externalKey[0:16]
        self.countRound = countRound
        self.roundKey = []
        self.rawCurrentKey = []
        self.s_box = []
        self.generateSBox()
        self.initCurrentKey()
        self.concatCurrentKey()

    def initCurrentKey (self):
        count = 0
        temp = []
        for extKey in self.externalKey:
            if(count != 4) :
                temp.append(hex(ord(extKey))[2:])
                count += 1
            else:
                self.rawCurrentKey.append(temp)
                temp = []
                temp.append(hex(ord(extKey))[2:])
                count = 1

        self.rawCurrentKey.append(temp)

    def generateSBox(self) -> list:
        """
            Generate S-Box menggunakan fungsi hash SHA-256.
            Generate dilakukan juga dengan memastikan bahwa
            element S-Box bernilai unik
        """
        temp_box = []
        for i in range(256):
            hash = hashlib.sha256(self.seed.encode() + bytes([i])).hexdigest()
            candidate_elmt = int(hash[:2], 16)
            if (candidate_elmt not in temp_box):
                self.s_box.append(candidate_elmt)
                temp_box.append(candidate_elmt)

        for i in range(256):
            if(i not in temp_box):
                self.s_box.append(i)
                temp_box.append(i)
        
    
    def getKey(self, round):                # round = round ke berapa          
        return self.roundKey[round]         #Return Current Key 128 bit with representation hexadecimal string f'0x'
    
    def concatCurrentKey(self):
        concat = f'0x'
        for outer in self.rawCurrentKey :
            for inner in outer :
                concat = f'{concat}{inner}'
        self.roundKey.append(concat)
    
    def shiftKey(self):
        shiftedKey = []
        shiftedKey.append(self.rawCurrentKey[0])
        for i in range(1, len(self.rawCurrentKey)):
            shiftedKey.append(self.rawCurrentKey[i][i:] + self.rawCurrentKey[i][:i])
        return shiftedKey
        

    def subtitution(self):
        shiftedKey = self.shiftKey()
        temp = []
        subtitutionKey = []
        for outer in shiftedKey:
            for inner in outer :
                temp.append(f'{self.s_box[int(inner, 16)]:x}')
            subtitutionKey.append(temp)
            temp = []
        return subtitutionKey
    
    def xorSubAndCurrent(self):
        subsKey = self.subtitution()
        temp = []
        xorKey = []

        for i in range(len(subsKey[0])):             ## XOR elemen subskey ke 0 dan ke 3 untuk mendapatakan xorkey ke 0
            xorValue = int(subsKey[0][i], 16) ^ int(subsKey[3][i], 16) ^ i
            if(len(f'{xorValue:x}') == 1):
                temp.append(f'{0:x}{xorValue:x}')
            elif(len(f'{xorValue:x}') == 2):
                temp.append(f'{xorValue:x}')
        xorKey.append(temp)
        temp = []

        for i in range(1, len(subsKey)):            ## XOR elemen xorkey ke 0 dan subskey ke 1 untuk mendatkan xorkey ke 1 dst.
            for j in range(len(subsKey)):
                xorValue = int(subsKey[i][j], 16) ^ int(xorKey[len(xorKey)-1][j], 16) ^ j
                if(len(f'{xorValue:x}') == 1):
                    temp.append(f'{0:x}{xorValue:x}')
                elif(len(f'{xorValue:x}') == 2):
                    temp.append(f'{xorValue:x}')
            xorKey.append(temp)
            temp = []

        return xorKey
    
    def makeRoundKey(self):
        for i in range(self.countRound):
            xorKey = self.xorSubAndCurrent()
            self.rawCurrentKey = xorKey
            self.concatCurrentKey()
            
# external_key = "H-2 Menuju UTS Semangat Gaes!" ## Kunci 16 karakter atau lebih
# key_expansion = KeyExpansion(external_key, 16)
# key_expansion.makeRoundKey()
# print(key_expansion.s_box)
# print(key_expansion.roundKey)
# print("Internal Key Round 4 = ", key_expansion.getKey(4))


