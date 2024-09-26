﻿using System.IdentityModel.Tokens.Jwt;
using System.Security.Cryptography;
using System.Text;
using System;
using System.Security.Cryptography;
using System.Text;
using Jose;
using Jose;
using Microsoft.IdentityModel.Tokens;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.OpenSsl;
using Newtonsoft.Json;

namespace Web
{
    public static class Cryptography
    {
        public static JweAlgorithm Algorithm = JweAlgorithm.RSA_OAEP_256;
        public static JweEncryption Encryption = JweEncryption.A256GCM;

        public static bool VerifyHmac(string data, string receivedHmac, string secretKey)
        {
            using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(secretKey));
            var computedHmac = Convert.ToBase64String(hmac.ComputeHash(Encoding.UTF8.GetBytes(data)));
            return computedHmac.Equals(receivedHmac);
        }

        public static string EncryptJson(RSAParameters publicKey, string json)
        {
            var rsa = new RSACng();
            rsa.ImportParameters(publicKey);
            var jwe = JWT.Encode(json, rsa, Algorithm, Encryption);
            return jwe;
        }

        public static string DecryptJson(RSAParameters privateKey, string encryptedJson)
        {
            var rsa = new RSACng();
            rsa.ImportParameters(privateKey);

            var json = JWT.Decode(encryptedJson, rsa, Algorithm, Encryption);
            return json;
        }
    }

    public static class KeyGeneratorUtil
    {
        public static (RSAParameters publicKey, RSAParameters privateKey) GenerateKeyPair()
        {
            using var rsa = new RSACryptoServiceProvider(2048);
            return (rsa.ExportParameters(false), rsa.ExportParameters(true));
        }

        public static string PublicKeyToString(RSAParameters publicKey)
        {
            var rsa = new RSACryptoServiceProvider();
            rsa.ImportParameters(publicKey);
            return Convert.ToBase64String(rsa.ExportSubjectPublicKeyInfo());
        }

        public static RSAParameters StringToPublicKey(string key)
        {
            var rsa = new RSACryptoServiceProvider();
            rsa.ImportSubjectPublicKeyInfo(Convert.FromBase64String(key), out _);
            return rsa.ExportParameters(false);
        }

        public static string PrivateKeyToString(RSAParameters privateKey)
        {
            var rsa = new RSACryptoServiceProvider();
            rsa.ImportParameters(privateKey);
            return Convert.ToBase64String(rsa.ExportPkcs8PrivateKey());
        }

        public static RSAParameters StringToPrivateKey(string key)
        {
            var rsa = new RSACryptoServiceProvider();
            rsa.ImportPkcs8PrivateKey(Convert.FromBase64String(key), out _);
            return rsa.ExportParameters(true);
        }
    }

    public class KeyPair(string privateKey, string publicKey)
    {
        public string PublicKey { get; set; } = publicKey;
        public string PrivateKey { get; set; } = privateKey;
    }
}
