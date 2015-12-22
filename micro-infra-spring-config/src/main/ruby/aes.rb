#!/usr/bin/env ruby

require 'openssl'

CIPHER = OpenSSL::Cipher::AES.new(256, :CBC)
SALT = ['deadbeef'].pack('H*')

def key(password)
    return OpenSSL::PKCS5.pbkdf2_hmac_sha1(password, SALT, 1024, 256)
end

def encrypt(plaintext, password)
    CIPHER.encrypt
    CIPHER.key = key(password)
    encrypted_bytes = CIPHER.update('0' * 16) + CIPHER.update(plaintext) + CIPHER.final
    return encrypted_bytes.unpack('H*')[0]
end

def decrypt(encrypted, password)
    CIPHER.decrypt
    CIPHER.key = key(password)
    decrypted_bytes = CIPHER.update([encrypted.sub(/^\{cipher\}/, '')].pack('H*')) + CIPHER.final
    return decrypted_bytes[16..-1]
end

def decrypt_file(file, password)
    cipherRegex = /\{cipher\}(\w+)/
    File.open(file).each do |line|
        cipher = cipherRegex.match(line)
        if cipher.nil?
            puts line
        else
            puts cipher.pre_match + decrypt(cipher[1], password) + cipher.post_match
        end
    end
end

case ARGV[0]
    when '-e'
        puts '"{cipher}' + encrypt(ARGV[1], ARGV[2]) + '"'
    when '-d'
        puts decrypt(ARGV[1], ARGV[2])
    when '-f'
        decrypt_file(ARGV[1], ARGV[2])
    else
        puts 'Valid options:'
        puts '    -e plaintext_secret master_password'
        puts '    -d encrypted_secret master_password'
        puts '    -f encrypted_file master_password'
end
