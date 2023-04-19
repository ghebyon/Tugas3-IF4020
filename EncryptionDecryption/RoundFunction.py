from KeyExpansion import KeyExpansion

ENCRYPT = 1
DECRYPT = 0

S_BOX = ['ee', 'e0', '9e', '56', '96', '29', 'ce', 'a9', '19', '14', '31', '02', '53', 'd0', '2a', 'ab', 
         '6e', '95', '1f', '27', 'c0', '90', '85', '33', 'a8', '66', '5a', '7e', '41', '40', '76', '84', 
         '8b', 'fa', '81', 'c8', '6b', '0f', 'c3', '30', '5f', '62', 'df', '3f', 'de', '2f', 'fc', '5d', 
         '9f', 'f4', '0d', 'e1', 'b3', 'f2', '92', 'e5', '34', 'd1', '06', '28', 'ca', 'c5', '60', 'c9', 
         '9a', '15', '13', '86', 'd6', '7b', 'd9', '8a', '25', '3b', '8c', '99', 'e6', 'f9', 'da', '42', 
         '35', '8f', 'a7', '70', 'cf', '22', '7f', '75', '97', 'e2', '9d', '2c', 'b2', 'ac', '0e', '88', 
         '3a', 'a6', 'be', 'f6', '7c', 'a3', 'b9', '26', '00', 'a2', 'ad', '93', '17', '51', 'b4', 'd3', 
         '91', 'a4', 'd4', '83', '77', '69', 'a0', 'ff', '0c', 'e4', 'a5', '43', 'c2', 'b0', '46', '1c', 
         '67', 'e9', '68', '6f', '09', '49', '2b', '47', 'dd', 'd2', 'fe', '54', '01', '04', '38', '10', 
         '72', 'b1', '4a', '23', '4b', 'c7', 'dc', '89', '21', 'aa', '94', '52', '71', '61', '45', '24', 
         '74', '44', '57', '6d', '0a', '03', '0b', '11', 'f7', 'ef', '82', '18', 'e8', '39', '48', 'f5', 
         'b7', 'd7', 'bb', '50', '20', '8d', '37', '4e', '07', 'b6', 'cb', 'ae', '78', 'bc', '55', 'b8', 
         '6c', 'af', '2d', 'ec', 'f3', '65', '1a', '59', 'f1', 'f0', '9b', 'fb', '73', 'cd', '3c', 'bf', 
         '2e', 'a1', '4f', '05', 'd5', '5b', '8e', '3e', 'fd', '5e', 'c1', '12', 'db', '4d', '6a', 'eb', 
         'f8', 'bd', '63', 'e7', '9c', 'ed', 'b5', '7d', '5c', '58', 'c6', '7a', '79', '64', '32', 'c4', 
         '98', '4c', 'cc', '87', 'e3', 'd8', '08', '16', '1d', '1e', '36', 'ba', '3d', '1b', 'ea', '80']

P_BOX = [29, 14, 25,  7, 15,  3, 27,  1, 
         31, 22,  9, 21, 10,  4, 13, 11, 
         16,  8, 20, 28, 30,  2, 23, 12, 
         26, 18,  5, 19,  0, 24, 17,  6]



class RoundFunction():

    def __init__(self, text, key, rounds, mode):
        self.plaintext = text if mode else None
        self.ciphertext = None if mode else text
        self.keyspace = key
        self.key = None
        self.rounds = rounds
        self.mode = mode


    def encrypt(self):
        cipher = self.plaintext
        for i in range(self.rounds):
            # print("iteration    : " + str(i))
            # print("current key  : " + self.keyspace[i+1])
            self.key = bytes.fromhex((self.keyspace[i+1])[2:])
            cipher = self.feistel_round(cipher)
        self.ciphertext = cipher


    def decrypt(self):
        plain = self.ciphertext
        for i in range(self.rounds):
            # print("iteration    : " + str(i))
            # print("current key  : " + self.keyspace[self.rounds-i])
            self.key = bytes.fromhex((self.keyspace[self.rounds-i])[2:])
            plain = self.feistel_round(plain)
        self.plaintext = plain


    def feistel_round(self, input):
        if self.mode:
            left_plain = input[:len(self.plaintext)//2]
            right_plain = input[len(self.plaintext)//2:]
            cipher = right_plain + xor_bytes(self.round_function(right_plain), left_plain)
            return cipher
        else:
            left_cipher = input[:len(self.ciphertext)//2]
            right_cipher = input[len(self.ciphertext)//2:]
            plain = xor_bytes(self.round_function(left_cipher), right_cipher) + left_cipher
            return plain


    def round_function(self, input):
        # permutation (switch odd and even bits)
        result = self.switch_bits(input)
        # expansion (expand 64 bits to 128 bits)
        result = self.expand_bits(result)
        # xor with key
        result = xor_bytes(result, self.key)
        # substitution (s-box)
        result = self.substitution(result)
        # permutation
        result = self.permutation(result)
        return result


    def switch_bits(self, input):
        # switch odd and even bit pairs
        even_pair = int.from_bytes(input, byteorder='big') & 0xCCCCCCCCCCCCCCCC
        odd_pair  = int.from_bytes(input, byteorder='big') & 0x3333333333333333
        return int.to_bytes((even_pair >> 1) | (odd_pair << 1), length=16, byteorder='big')


    def expand_bits(self, input):
        # expand 64 bits to 128 bits by XORing adjacent 4-bit chunks and put it between them
        expanded = b''
        input_int = int.from_bytes(input, byteorder='big')
        for i in range(0, input_int.bit_length(), 4):
            chunk1 = (input_int >> i) & 0b1111
            chunk2 = (input_int >> (i-4)%64) & 0b1111
            subtext1 = chunk1
            subtext2 = chunk1 ^ chunk2
            # print("1: ",chunk1, "   | 2: ",chunk2, "   | 3: ",subtext1, "   | 4: ",subtext2)
            expanded += int.to_bytes((subtext1 << 4) + subtext2, 1, byteorder='big')
        expanded = expanded[::-1]
        return expanded
    

    def compress_bits(self, input):
        # compress 128 bits to 64 bits by only taking the first 4 bits of each 8-bit chunk
        compressed = b''
        input_int = int.from_bytes(input, byteorder='big')
        for i in range(0, input_int.bit_length(), 16):
            chunk1 = (input_int >> (i+4)%128) & 0b1111
            chunk2 = (input_int >> (i+12)%128) & 0b1111
            # print("1: ",chunk1, "   | 2: ",chunk2)
            compressed += int.to_bytes((chunk2 << 4) + chunk1, 1, byteorder='big')
        compressed = compressed[::-1]
        return compressed
    

    def substitution(self, input):
        result = b''
        input_int = int.from_bytes(input, byteorder='big')
        for i in range(0, input_int.bit_length(), 8):
            chunk1 = (input_int >> i) & 0b1111          # col
            chunk2 = (input_int >> ((i+4)%128)) & 0b1111  # row
            index = chunk2 * 16 + chunk1
            result += bytes.fromhex(S_BOX[index])
        return result[::-1]


    def inverse_substitution(self, input):
        result = b''
        for i in range(len(input)):
            index = S_BOX.index(hex(input[i])[2:].zfill(2))
            row = index // 16
            col = index % 16
            result += int.to_bytes((row << 4) + col, 1, byteorder='big')
        return result
            
    
    def permutation(self, input):
        result = b''
        input_int = int.from_bytes(input, byteorder='big')
        permutation = [(input_int >> 4*i) & 0b1111 for i in P_BOX]
        permutation = permutation[::-1]
        reduced = [permutation[i] ^ permutation[31-i] for i in range(16)]
        for i in range(0, len(reduced), 2):
            result += int.to_bytes((reduced[i] << 4) + reduced[i+1], 1, byteorder='big')
        return result


def xor_bytes(b1, b2):
    # XOR two bytes objects and return a bytes object
    i1 = int.from_bytes(b1, 'big')
    i2 = int.from_bytes(b2, 'big')
    result = i1 ^ i2
    num_bytes = max(len(b1), len(b2))
    return result.to_bytes(num_bytes, 'big')




# TESTING
# KEY EXPANSION
external_key = "H-2 Menuju UTS Semangat Gaes!" ## Kunci 16 karakter atau lebih
key_expansion = KeyExpansion(external_key, 16)
key_expansion.makeRoundKey()
# print(key_expansion.s_box)
# print(key_expansion.roundKey)

# ENCRYPT
round_fuction = RoundFunction(b"halokamusiapahah", key_expansion.roundKey, 16, ENCRYPT)
round_fuction.encrypt()
# print(round_fuction.plaintext)
# print(round_fuction.ciphertext)

# DECRYPT
# round_fuction = RoundFunction(round_fuction.ciphertext, key_expansion.roundKey, 16, DECRYPT)
# round_fuction.decrypt()
# print(round_fuction.ciphertext)
# print(round_fuction.plaintext)
