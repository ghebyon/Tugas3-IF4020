from ECDSA import ECDSA

from fastapi import FastAPI

app = FastAPI()

@app.get("/sign/{messages}")
def sign(messages : str, privateKey : str):
    ecdsa = ECDSA()
    message = ecdsa.strToInt(messages)
    privateKey = int(privateKey)
    signature = ecdsa.sign(message, privateKey)
    signed_message = message + "\n\nSIGNATURE_BEGIN\n" + hex(signature[0][0])[2:] + "\n" + hex(signature[0][1])[2:] + "\n" + hex(signature[1])[2:] + "\nSIGNATURE_END"
    return {"sign" : signed_message}

@app.get("/verify/{signed_messages}")
def verify(signed_message : str, publicKey : str):
    message = signed_message.split("\n\nSIGNATURE_BEGIN")[0]
    signature_string = signed_message.split("\n\nSIGNATURE_BEGIN")[1].split("\nSIGNATURE_END")[0].split("\n")
    signature = [[0, 0], 0]
    signature[0][0] = int(signature_string[1], 16)
    signature[0][1] = int(signature_string[2], 16)
    signature[1] = int(signature_string[3], 16)
    ecdsa = ECDSA()
    message = ecdsa.strToInt(message)
    publicKey = int(publicKey)
    valid = ecdsa.verify(message, publicKey, signature)
    return {"valid" : valid}

@app.get("/generateKeyPair")
def generateKeyPair():
    ecdsa = ECDSA()
    privateKey, publicKey = ecdsa.generateKeyPair()
    return {"privateKey" : str(privateKey), "publicKeyR" : str(publicKey[0]), "publicKeyS" : str(publicKey[1])}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000)