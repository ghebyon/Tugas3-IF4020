import time
from RoundFunction import RoundFunction
from KeyExpansion import KeyExpansion
import Permutation as perm
import Substitution as sub
import sys
from ECDSA import ECDSA

def encrypt(messages : str, key : str):
    plaintext = messages.encode()

    # Key expansion
    key_expansion = KeyExpansion(key, 16)
    key_expansion.makeRoundKey()
    
    # Split plaintext into 16 bytes chunks
    plaintext_chunks = split_text(plaintext)
    ciphertext = b''
    
    # P-BOX and S-BOX
    p_box = perm.generatePBox(key, 128)
    s_box = sub.generateSBox(key, 256)

    # Encrypt each chunk
    i = 0
    for plaintext in plaintext_chunks:
        
        # Permutation 
        plaintext = perm.permute(plaintext, p_box)
        
        # Round function
        round_function = RoundFunction(plaintext, key_expansion.roundKey, 16, 1)
        round_function.encrypt()
        ciphertext_chunks = round_function.ciphertext
        
        # Substitution
        ciphertext_chunks = sub.substitute(ciphertext_chunks, s_box)
        
        ciphertext += ciphertext_chunks
        i += 1
    return ciphertext.hex()


def decrypt(messages : str, key : str):
    ciphertext = messages
    
    # Key expansion
    key_expansion = KeyExpansion(key, 16)
    key_expansion.makeRoundKey()
    
    # Split ciphertext into 16 bytes chunks
    ciphertext_chunks = split_text(bytes.fromhex(ciphertext))
    plaintext = b''

    # P-BOX and S-BOX
    p_box = perm.generatePBox(key, 128)
    s_box = sub.generateSBox(key, 256)
    
    # Decrypt each chunk
    i = 0
    for ciphertext in ciphertext_chunks:
        
        # Reverse Substitution
        ciphertext = sub.reverse(ciphertext, s_box)
        
        # Round function
        round_function = RoundFunction(ciphertext, key_expansion.roundKey, 16, 0)
        round_function.decrypt()
        plaintext_chunks = round_function.plaintext
        
        # Reverse Permutation
        plaintext_chunks = perm.netralize(plaintext_chunks, p_box)

        plaintext += plaintext_chunks
        i += 1
    return plaintext.hex()

def split_text(plaintext):
    # Split plaintext into 16 bytes chunks
    chunks = []
    for i in range(0, len(plaintext), 16):
        chunks.append(plaintext[i:i+16].ljust(16, b'\x00'))
    return chunks

def sign(messages : str, privateKey : str):
    ecdsa = ECDSA()
    message = ecdsa.strToInt(messages)
    privateKey = int(privateKey)
    signature = ecdsa.sign(message, privateKey)
    signed_message = messages + "\n\nSIGNATURE_BEGIN\n" + hex(signature[0][0])[2:] + "\n" + hex(signature[0][1])[2:] + "\n" + hex(signature[1])[2:] + "\nSIGNATURE_END"
    return signed_message


def verify(signed_message : str, publicKey1 : str, publicKey2 : str):
    message = signed_message.split("\n\nSIGNATURE_BEGIN")[0]
    signature_string = signed_message.split("\n\nSIGNATURE_BEGIN")[1].split("\nSIGNATURE_END")[0].split("\n")
    signature = [[0, 0], 0]
    signature[0][0] = int(signature_string[1], 16)
    signature[0][1] = int(signature_string[2], 16)
    signature[1] = int(signature_string[3], 16)
    ecdsa = ECDSA()
    message = ecdsa.strToInt(message)
    publicKey1 = int(publicKey1)
    publicKey2 = int(publicKey2)
    publicKey = [publicKey1, publicKey2]
    valid = ecdsa.verify(message, publicKey, signature)
    return valid

def generateKeyPair():
    ecdsa = ECDSA()
    privateKey, publicKey = ecdsa.generateKeyPair()
    return [str(privateKey), str(publicKey[0]), str(publicKey[1])]




