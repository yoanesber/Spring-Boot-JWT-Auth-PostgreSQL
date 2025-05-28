# Generate private key
openssl genpkey -algorithm RSA -out privateKey.pem -pkeyopt rsa_keygen_bits:2048

# Extract public key
openssl rsa -pubout -in privateKey.pem -out publicKey.pem
