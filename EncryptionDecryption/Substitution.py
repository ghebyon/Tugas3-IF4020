import hashlib

def generateSBox(seed : str, size : int) -> list:
    """
        Generate S-Box menggunakan fungsi hash SHA-256.
        Generate dilakukan juga dengan memastikan bahwa
        element S-Box bernilai unik
    """
    s_box = []
    for i in range(size):
        hash = hashlib.sha256(seed.encode() + bytes([i])).hexdigest()
        candidate_elmt = format(int(hash[:2], 16), '02x')
        if (candidate_elmt not in s_box):
            s_box.append(candidate_elmt)

    for i in range(size):
        candidate_elmt = format(i,'02x')
        if(candidate_elmt not in s_box):
            s_box.append(candidate_elmt)
    # print(len(s_box) == len(set(s_box)))
    return s_box

def inverseSBox(s_box : list):
    '''
        S-Box inverse dibuat dengan membuat list 
        yang berisi indeks pada S-Box asli
    '''
    s_box_inverse = []
    for i in range(len(s_box)):
        s_box_inverse.append(s_box.index(format(i,'02x')))
    
    return s_box_inverse

def substitute(string_byte, s_box:list):
    result = b''
    for b in string_byte:
        result += bytes.fromhex(s_box[b])
    return result

def reverse(string_byte, s_box:list):
    result = b''
    for b in string_byte:
        result += bytes([s_box.index(format(b,'02x'))])
        #result += bytes.fromhex(format(s_box.index(bytes(b).hex()),'02x'))
    return result
    

# s_box = generateSBox("H-2 Menuju UTS Semangat", 256)
# s_box_inverse = inverseSBox(s_box)
# test_text = b"Berserah diri keawedawedawedpada Tuhan"

# x = substitute(test_text,s_box)
# y = reverse(x, s_box)

'''
    Substitusi dilakukan dengan iterasi nilai byte pada teks.
    Dan digunakan sebagai indeks untuk mendapatkan nilai pada
    S-Box
'''
# substituted_block = [s_box[b] for b in test_text]
# print(s_box)
# print(len(s_box))
# print(s_box.index(215))
# print(s_box_inverse)