import hashlib

def generatePBox(seed : str, size : int) -> list:
    """Generate P-Box using SHA-256 hash function."""
    p_box = list(range(size))
    for i in range(size):
        hash = hashlib.sha256(seed.encode() + bytes([i])).hexdigest()
        j = int(hash, 16) % (size-i)
        p_box[i], p_box[i+j] = p_box[i+j], p_box[i]
    
    return p_box

def permute(string_byte, p_box: list):

    byte_list = list(string_byte)
    string_bit = ''.join([bin(byte)[2:].zfill(8) for byte in byte_list])

    permuted_block = ""
    for i in range(len(p_box)):
        permuted_block += string_bit[p_box[i]-1]

    bit_list = [permuted_block[i:i+8] for i in range(0, len(permuted_block), 8)]
    result_byte_string = bytes([int(b, 2) for b in bit_list])

    return result_byte_string


def netralize(string_byte, p_box: list):
    byte_list = list(string_byte)
    string_bit = ''.join([bin(byte)[2:].zfill(8) for byte in byte_list])
    backagain_block = ["0" for i in range (128)]
    for i in range(len(p_box)):
        backagain_block[p_box[i]-1] = string_bit[i]
    
    result_string_bit = ''.join(backagain_block)
    
    bit_list = [result_string_bit[i:i+8] for i in range(0, len(result_string_bit), 8)]
    result_byte_string = bytes([int(b, 2) for b in bit_list])

    return result_byte_string
    
    
# p_box = generatePBox("H-2 Menuju UTS Semangat", 128) #128 sesuai dengan jumlah bit
# print(p_box)
# test_block = b'\x01\x23\x45\x67\x89\xab\xcd\xef\xfe\xdc\xba\x98\x76\x54\x32\x10'
# x = permute(test_block,p_box)
# y = netralize(x, p_box)
# print(test_block)
# print(x)
# print(y)

