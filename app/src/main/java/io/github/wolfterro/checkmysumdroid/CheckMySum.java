/*
MIT License

Copyright (c) 2017 Wolfgang Almeida

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package io.github.wolfterro.checkmysumdroid;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Wolfterro on 19/05/2017.
 */

public class CheckMySum {

    // DEBUG
    // =====
    public static String status = "";

    private Uri file;
    private String algorithm;
    private boolean isUpper;
    private ContentResolver cr;

    // Construtor da classe
    // ====================
    public CheckMySum(Uri file, String algorithm, boolean isUpper, ContentResolver cr) {
        this.file = file;
        this.algorithm = algorithm;
        this.isUpper = isUpper;
        this.cr = cr;
    }

    // Método inicial da classe.
    // Retorna a hash depois de computada.
    // ===================================
    public String init() {
        byte[] hashByte;
        String hash;

        hashByte = computeHashByte();
        hash = hashByteToString(hashByte);
        if(isUpper) {
            return hash.toUpperCase();
        }
        else {
            return hash;
        }
    }

    // Método responsável por computar a hash do arquivo.
    // ==================================================
    protected byte[] computeHashByte() {
        InputStream f = null;
        try {
            f = cr.openInputStream(file);

            byte[] buffer = new byte[1024];
            try {
                MessageDigest m = MessageDigest.getInstance(algorithm);
                int read;

                do {
                    read = f.read(buffer);
                    if(read > 0) {
                        m.update(buffer, 0, read);
                    }
                } while (read != -1);
                f.close();
                return m.digest();
            }
            catch (NoSuchAlgorithmException e) {
                status = "NO_SUCH_ALGORITHM";
                return null;
            }

        }
        catch (FileNotFoundException e) {
            status = "FILE_NOT_FOUND";
            return null;
        }
        catch (IOException e) {
            status = "IO_ERROR";
            return null;
        }
    }

    // Convertendo a hash de byte[] para String em hexadecimal.
    // ========================================================
    protected String hashByteToString(byte[] hashByte) {
        if (hashByte != null) {
            String hash = "";
            for (int i = 0; i < hashByte.length; i++) {
                hash += Integer.toString( (hashByte[i] & 0xff) + 0x100, 16).substring(1);
            }
            status = "SUCCESS";
            return hash;
        }
        else {
            return "";
        }
    }
}
