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
    private static final String IP = "192.168.0.104";
    private Socket clientSocket;
    private PrintWriter out;
    private Scanner in;

    private EditText editMsg;
    private EditText editName;
    private Button button1;
    private Button button2;

    private ArrayAdapter<String> adapter;
    private ListView listView;
    private ArrayList<String> msgArray = new ArrayList<>();

    public void kill() {
        flag = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editMsg = findViewById(R.id.editMsg);
        editName = findViewById(R.id.editName);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        listView = findViewById(R.id._dynamic1);
        final Background background = new Background();
        listView.setClickable(false);

        adapter = new ArrayAdapter<>(Main.this, android.R.layout.simple_expandable_list_item_1, msgArray);
        listView.setAdapter(adapter);

        final View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button1:
                        background.execute();
                        break;
                    case R.id.button2:
                        background.sendAll();
                        break;
                }
            }
        };

        button1.setOnClickListener(onClickListener);
        button2.setOnClickListener(onClickListener);

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


            try {
                clientSocket = new Socket(IP, PORT);
                in = new Scanner(clientSocket.getInputStream());
                out = new PrintWriter(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!flag) {

                String msgT;

                if (in.hasNext()) {
                    msgT = in.nextLine();
                    msgArray.add(0, msgT);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();

        }

        public void sendAll() {
            String msg;
            msg = editName.getText().toString() + ": " + editMsg.getText().toString();
            out.println(msg);
            out.flush();
            editMsg.setText("");
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!editMsg.getText().toString().equals(""))
            out.println("[ \"" + editName.getText().toString() + "\" ливнул]");
        else
            out.println("[Вышел ноунейм]");

        out.println("exit");
        out.flush();
        out.close();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        kill();
    }
}
