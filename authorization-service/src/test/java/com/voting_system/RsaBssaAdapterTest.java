package com.voting_system;

import static org.junit.jupiter.api.Assertions.*;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

import authorization.application.dto.GenerateKeyPair;
import authorization.infrastructure.adapter.out.adapters.RsaBssaAdapter;

public class RsaBssaAdapterTest {
    private final RsaBssaAdapter rsaBssaAdapter = new RsaBssaAdapter();
    @BeforeAll 
    static void setup() {
        if(Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Nested
    @DisplayName("Test for generateKeys method")
    class generateKeysTest {
        @Test 
        @DisplayName ("Should generate a valid key pair")
        void shouldGenerateValidKeyPair() throws Exception {
            GenerateKeyPair keyPair = rsaBssaAdapter.generateKeys();
            assertNotNull(keyPair, "The generated key pair should not be null");
            assertNotNull(keyPair.getPrivateKey(), "The generated private key should not be null");
            assertNotNull(keyPair.getPublicKey(), "The generated public key should not be null");

            KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(keyPair.getPublicKey()));
            assertEquals(3072, publicKey.getModulus().bitLength(), "The generated public key should have a modulus of 3072 bits");
        }
    }

    @Nested
    @DisplayName("Test for sign method")
    class signTest {
        @Test
        @DisplayName("Should generate a valid blind signature")
        void shouldGenerateValidBlindSignature(TestReporter testReporter) throws Exception {
            GenerateKeyPair keyPair = rsaBssaAdapter.generateKeys();
            String message = "Test message";
            String blindSignature = rsaBssaAdapter.sign(keyPair.getPrivateKey(), message.getBytes());
            System.out.println("Blind Signature: " + blindSignature);
            assertNotNull(blindSignature, "The generated blind signature should not be null");
        }

        @Test
        @DisplayName ("Should verify if the blinded message and the private key in hex format produce the same signature")
        void shouldMatchExpectedBlindSignature() throws Exception {
            String privateKeyHex = "0d43242aefe1fb2c13fbc66e20b678c4336d20b1808c558b6e62ad16a287077180b177e1f01b12f9c6cd6c52630257ccef26a45135a990928773f3bd2fc01a313f1dac97a51cec71cb1fd7efc7adffdeb05f1fb04812c924ed7f4a8269925dad88bd7dcfbc4ef01020ebfc60cb3e04c54f981fdbd273e69a8a58b8ceb7c2d83fbcbd6f784d052201b88a9848186f2a45c0d2826870733e6fd9aa46983e0a6e82e35ca20a439c5ee7b502a9062e1066493bdadf8b49eb30d9558ed85abc7afb29b3c9bc644199654a4676681af4babcea4e6f71fe4565c9c1b85d9985b84ec1abf1a820a9bbebee0df1398aae2c85ab580a9f13e7743afd3108eb32100b870648fa6bc17e8abac4d3c99246b1f0ea9f7f93a5dd5458c56d9f3f81ff2216b3c3680a13591673c43194d8e6fc93fc1e37ce2986bd628ac48088bc723d8fbe293861ca7a9f4a73e9fa63b1b6d0074f5dea2a624c5249ff3ad811b6255b299d6bc5451ba7477f19c5a0db690c3e6476398b1483d10314afd38bbaf6e2fbdbcd62c3ca9797a420ca6034ec0a83360a3ee2adf4b9d4ba29731d131b099a38d6a23cc463db754603211260e99d19affc902c915d7854554aabf608e3ac52c19b8aa26ae042249b17b2d29669b5c859103ee53ef9bdc73ba3c6b537d5c34b6d8f034671d7f3a8a6966cc4543df223565343154140fd7391c7e7be03e241f4ecfeb877a051";
            String blinded_message_hex = "aa3ee045138d874669685ffaef962c7694a9450aa9b4fd6465db9b3b75a522bb921c4c0fdcdfae9667593255099cff51f5d3fd65e8ffb9d3b3036252a6b51b6edfb3f40382b2bbf34c0055e4cbcc422850e586d84f190cd449af11dc65545f5fe26fd89796eb87da4bda0c545f397cddfeeb56f06e28135ec74fd477949e7677f6f36cfae8fd5c1c5898b03b9c244cf6d1a4fb7ad1cb43aff5e80cb462fac541e72f67f0a50f1843d1759edfaae92d1a916d3f0efaf4d650db416c3bf8abdb5414a78cebc97de676723cb119e77aea489f2bbf530c440ebc5a75dccd3ebf5a412a5f346badd61bee588e5917bdcce9dc33c882e39826951b0b8276c6203971947072b726e935816056ff5cb11a71ca2946478584126bb877acdf87255f26e6cca4e0878801307485d3b7bb89b289551a8b65a7a6b93db010423d1406e149c87731910306e5e410b41d4da3234624e74f92845183e323cf7eb244f212a695f8856c675fbc3a021ce649e22c6f0d053a9d238841cf3afdc2739f99672a419ae13c17f1f8a3bc302ec2e7b98e8c353898b7150ad8877ec841ea6e4b288064c254fefd0d049c3ad196bf7ffa535e74585d0120ce728036ed500942fbd5e6332c298f1ffebe9ff60c1e117b274cf0cb9d70c36ee4891528996ec1ed0b178e9f3c0c0e6120885f39e8ccaadbb20f3196378c07b1ff22d10049d3039a7a92fe7efdd95d";
            String expectedBlindSignature = "3f4a79eacd4445fca628a310d41e12fcd813c4d43aa4ef2b81226953248d6d00adfee6b79cb88bfa1f99270369fd063c023e5ed546719b0b2d143dd1bca46b0e0e615fe5c63d95c5a6b873b8b50bc52487354e69c3dfbf416e7aca18d5842c89b676efdd38087008fa5a810161fcdec26f20ccf2f1e6ab0f9d2bb93e051cb9e86a9b28c5bb62fd5f5391379f887c0f706a08bcc3b9e7506aaf02485d688198f5e22eefdf837b2dd919320b17482c5cc54271b4ccb41d267629b3f844fd63750b01f5276c79e33718bb561a152acb2eb36d8be75bce05c9d1b94eb609106f38226fb2e0f5cd5c5c39c59dda166862de498b8d92f6bcb41af433d65a2ac23da87f39764cb64e79e74a8f4ce4dd567480d967cefac46b6e9c06434c3715635834357edd2ce6f105eea854ac126ccfa3de2aac5607565a4e5efaac5eed491c335f6fc97e6eb7e9cea3e12de38dfb315220c0a3f84536abb2fdd722813e083feda010391ac3d8fd1cd9212b5d94e634e69ebcc800c4d5c4c1091c64afc37acf563c7fc0a6e4c082bc55544f50a7971f3fb97d5853d72c3af34ffd5ce123998be5360d1059820c66a81e1ee6d9c1803b5b62af6bc877526df255b6d1d835d8c840bebbcd6cc0ee910f17da37caf8488afbc08397a1941fcc79e76a5888a95b3d5405e13f737bea5c78d716a48eb9dc0aec8de39c4b45c6914ad4a8185969f70b1adf46";

            byte[] privateKeyBytes = Hex.decode(privateKeyHex);
            byte[] messageBytes = Hex.decode(blinded_message_hex);
            byte[] expectedBlindSignatureBytes = Hex.decode(expectedBlindSignature);

            String blindSignature = rsaBssaAdapter.sign(privateKeyBytes, messageBytes);
            String expectedBlindSignatureBase64 = Base64.getEncoder().encodeToString(expectedBlindSignatureBytes);

            assertEquals(expectedBlindSignatureBase64, blindSignature, "The generated blind signature should match the expected blind signature");
        }
    }
}
