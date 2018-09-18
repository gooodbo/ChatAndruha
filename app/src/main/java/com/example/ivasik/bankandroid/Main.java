package com.example.ivasik.bankandroid;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Main extends AppCompatActivity {

    private boolean flag = false;
    private static final int PORT = 8888;
    private static final String IP = "127.0.0.1";
    private Socket clientSocket;
    private PrintWriter out;
    private Scanner in;

    private EditText editMsg;
    private EditText editName;
    private Button button;
    private ArrayList<String> msg;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editMsg = findViewById(R.id.editMsg);
        editName = findViewById(R.id.editName);
        button = findViewById(R.id.button1);
        msg = new ArrayList<>();
        listView = findViewById(R.id._dynamic1);

        //    Background background = new Background();


        /*

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, msg);
        listView.setAdapter(adapter);
        editMsg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        msg.add(0, editName.getText().toString() + ": " + editMsg.getText().toString());
                        adapter.notifyDataSetChanged();
                        editMsg.setText("");

                        return true;
                    }
                return false;
            }
        });

           */


        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Background background = new Background();
                background.execute();

            }
        };

        //  background.execute();
        button.setOnClickListener(onClickListener);

    }

    @SuppressLint("StaticFieldLeak")
    private class Background extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            Toast.makeText(Main.this, "Пошло подключение...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final ArrayList<String> msgArray = new ArrayList<>();
            String msgT;

            try {
                clientSocket = new Socket(IP, PORT);
                in = new Scanner(clientSocket.getInputStream());
                out = new PrintWriter(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!flag) {

                if (in.hasNext()) {
                    msgT = in.nextLine();
                    msgArray.add(0, msgT);
                }
                adapter = new ArrayAdapter<>(Main.this, android.R.layout.simple_expandable_list_item_1, msgArray);
                listView.setAdapter(adapter);
                editMsg.setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (event.getAction() == KeyEvent.ACTION_DOWN)
                            if (keyCode == KeyEvent.KEYCODE_ENTER) {

                                msg.add(0, editName.getText().toString() + ": " + editMsg.getText().toString());
                                out.println(msg.toString());
                                adapter.notifyDataSetChanged();
                                editMsg.setText("");
                                return true;
                            }

                        return false;
                    }
                });

            }
            return null;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

