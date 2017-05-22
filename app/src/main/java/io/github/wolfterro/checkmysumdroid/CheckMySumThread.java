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

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;

/**
 * Created by Wolfterro on 21/05/2017.
 */

// Thread principal do cálculo de hash
// ===================================
public class CheckMySumThread extends Thread {

    private CheckMySum check;           // Objeto da classe de cálculo de hash
    private ProgressDialog computing;   // Caixa de diálogo é mostrada enquanto o cálculo é feito
    private Context activity;           // Activity que chamou esta classe
    private EditText editText2;         // Caixa de texto usada para mostrar o resultado

    public String hash = "";            // Resultado como String pública

    public CheckMySumThread(CheckMySum check, ProgressDialog computing,
                            Context activity, EditText editText2) {

        this.check = check;
        this.computing = computing;
        this.activity = activity;
        this.editText2 = editText2;
    }

    @Override
    public void run() {
        hash = check.init();
        handler.sendEmptyMessage(0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message m) {
            computing.dismiss();

            if(hash != "") {
                editText2.setText(hash);
            }
            else {
                Toast.makeText(activity, activity.getString(R.string.errorComputingHash),
                        Toast.LENGTH_SHORT).show();
            }
            // DEBUG
            // =====
            // Toast.makeText(activity, check.status, Toast.LENGTH_SHORT).show();
        }
    };
}
