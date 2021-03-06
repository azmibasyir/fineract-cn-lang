/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.lang.security;

import org.apache.fineract.cn.lang.DateConverter;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.Clock;
import java.time.LocalDateTime;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class RsaKeyPairFactory {

  private RsaKeyPairFactory() {
  }

  public static void main(String[] args) {
    KeyPairHolder keyPair = RsaKeyPairFactory.createKeyPair();

    String style = (args != null && args.length > 0) ?args[0] :"";
    if ("SPRING".equalsIgnoreCase(style)) {
      System.out.println("system.publicKey.exponent=" + keyPair.getPublicKeyExp());
      System.out.println("system.publicKey.modulus=" + keyPair.getPublicKeyMod());
      System.out.println("system.publicKey.timestamp=" + keyPair.getTimestamp());
      System.out.println("system.privateKey.modulus=" + keyPair.getPrivateKeyMod());
      System.out.println("system.privateKey.exponent=" + keyPair.getPrivateKeyExp());
    }
    else if ("UNIX".equalsIgnoreCase(style)) {
      System.out.println("PUBLIC_KEY_EXPONENT=" + keyPair.getPublicKeyExp());
      System.out.println("PUBLIC_KEY_MODULUS=" + keyPair.getPublicKeyMod());
      System.out.println("PUBLIC_KEY_TIMESTAMP=" + keyPair.getTimestamp());
      System.out.println("PRIVATE_KEY_MODULUS=" + keyPair.getPrivateKeyMod());
      System.out.println("PRIVATE_KEY_EXPONENT=" + keyPair.getPrivateKeyExp());
    }
  }

  public static KeyPairHolder createKeyPair() {
    try {
      final KeyFactory keyFactory = KeyFactory.getInstance("RSA");

      final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      final KeyPair keyPair = keyPairGenerator.genKeyPair();

      final RSAPublicKeySpec rsaPublicKeySpec =
          keyFactory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
      final RSAPrivateKeySpec rsaPrivateKeySpec =
          keyFactory.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);

      final String keyTimestamp = createKeyTimestampNow();
      final RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(rsaPublicKeySpec);
      final RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(rsaPrivateKeySpec);

      return new KeyPairHolder(keyTimestamp, publicKey, privateKey);
    } catch (final NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException("RSA problem.");
    }
  }

  private static String createKeyTimestampNow() {
    final String timestamp = DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC()));
    final int index = timestamp.indexOf(".");
    final String timestampWithoutNanos;
    if (index > 0) {
      timestampWithoutNanos = timestamp.substring(0, index);
    }
    else {
      timestampWithoutNanos = timestamp;
    }
    return timestampWithoutNanos.replace(':', '_');
  }

  public static class KeyPairHolder {
    private final String timestamp;
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public KeyPairHolder(final String timestamp, final RSAPublicKey publicKey, final RSAPrivateKey privateKey) {
      super();
      this.timestamp = timestamp;
      this.publicKey = publicKey;
      this.privateKey = privateKey;
    }


    public RSAPublicKey publicKey() {
      return publicKey;
    }

    public RSAPrivateKey privateKey() {
      return privateKey;
    }

    public String getTimestamp() {
      return timestamp;
    }

    public BigInteger getPublicKeyMod() {
      return publicKey.getModulus();
    }

    public BigInteger getPublicKeyExp() {
      return publicKey.getPublicExponent();
    }

    public BigInteger getPrivateKeyMod() {
      return privateKey.getModulus();
    }

    public BigInteger getPrivateKeyExp() {
      return privateKey.getPrivateExponent();
    }
  }
}
