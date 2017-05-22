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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText editText1; // Caminho do arquivo
    private EditText editText2; // Hash gerada

    private Spinner spinner;    // Algoritmos disponíveis

    private CheckBox checkbox;  // Gerar Hash com letras maiúsculas

    private Button button1;     // Abrir arquivo
    private Button button2;     // Gerar hash
    private Button button3;     // Copiar Hash

    private static final int FILE_SELECT_CODE = 0;  // Código de retorno
    private Uri filenameUri = null;                 // Caminho do arquivo
    private String filename = null;                 // Nome do arquivo

    private String hash = null;     // Hash do arquivo resgatada do editText2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = (EditText)findViewById(R.id.editText1);
        editText2 = (EditText)findViewById(R.id.editText2);

        spinner = (Spinner)findViewById(R.id.spinner);
        checkbox = (CheckBox)findViewById(R.id.checkBox);

        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);

        // Selecionando arquivo para verificação de Hash
        // =============================================
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(i, FILE_SELECT_CODE);
                // onActivityResult() é chamado
            }
        });

        // Verificando Hash do arquivo e retornando ao usuário
        // ===================================================
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filename != null) {
                    ContentResolver cr = getApplicationContext().getContentResolver();
                    CheckMySum sum = new CheckMySum(filenameUri, spinner.getSelectedItem().toString(),
                            checkbox.isChecked(), cr);

                    // Iniciando o processo de geração de hash
                    // =======================================
                    ProgressDialog computing = new ProgressDialog(MainActivity.this);
                    computing.setTitle(getString(R.string.generatingHash));
                    computing.setMessage(getString(R.string.pleaseStandBy));
                    computing.setCancelable(false);
                    computing.show();

                    // Iniciando Thread...
                    // ===================
                    CheckMySumThread cmst = new CheckMySumThread(sum, computing, MainActivity.this,
                            editText2);
                    cmst.start();
                }
            }
        });

        // Copiando a hash gerada para a clipboard do sistema
        // ==================================================
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hash = editText2.getText().toString();

                if(!hash.equals("")) {
                    ClipboardManager clipboard = (ClipboardManager)getSystemService(
                            Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(null, hash);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(MainActivity.this, getString(R.string.warningHashCopy),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, getString(R.string.warningNothingToCopy),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Resultado ao selecionar arquivo
    // ===============================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == FILE_SELECT_CODE) {
            if(resultCode == RESULT_OK) {
                filenameUri = intent.getData();
                filename = getFilename();

                editText1.setText(filename);
            }
            else {
                Toast.makeText(MainActivity.this, getString(R.string.errorOpeningFile),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(MainActivity.this, getString(R.string.errorSelectingFile),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Resgatando o nome do arquivo
    // ============================
    protected String getFilename() {
        String uristr = filenameUri.toString();
        File f = new File(uristr);
        String path = f.getAbsolutePath();
        String filename = "";

        if(uristr.startsWith("content://")) {
            Cursor c = null;
            try {
                c = getApplicationContext().getContentResolver().query(filenameUri, null, null,
                        null, null);
                if(c != null && c.moveToFirst()) {
                    filename = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                c.close();
            }
        }
        else {
            filename = f.getName();
        }
        return filename;
    }
}