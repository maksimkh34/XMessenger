using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using Context;
using Web;

namespace Model
{
    public class MainModel
    {
        public async Task InitMainModel()
        {
            Config.Init();
            var tempStcKeys = KeyGeneratorUtil.GenerateKeyPair();
            var response = await NetManager.Send("TPKeyRequest:" + KeyGeneratorUtil.PublicKeyToString(tempStcKeys.publicKey));

            if (response.StatusCode != 200)
            {
                // show msg that server response is bad
                return;
            }
            var decryptedJsonStr = Cryptography.DecryptJson(tempStcKeys.privateKey, response.Message)!;
            NetManager.DeviceId = decryptedJsonStr[..8];
            NetManager.HmacKey = NetManager.DeviceId;
            NetManager.PublicKeyToServer = decryptedJsonStr[8..];

            var publicKey = RegisterPermKeys();
            NetManager.StcPermPublicKey = publicKey;
        }

        private static RSAParameters RegisterPermKeys()
        {
            var stcPermKeyPair = KeyGeneratorUtil.GenerateKeyPair();
            NetManager.StcPermPrivateKey = stcPermKeyPair.privateKey;
            return stcPermKeyPair.publicKey;
        }
    }
}
