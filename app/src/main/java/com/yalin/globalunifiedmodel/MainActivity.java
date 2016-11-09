package com.yalin.globalunifiedmodel;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yalin.globalunifiedmodel.datacontroller.LoggingConsumer;
import com.yalin.globalunifiedmodel.metadata.UserModel;
import com.yalin.globalunifiedmodel.metadata.UserModelManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        UserModel userModel = new UserModel();
        userModel.name = "yalin";
        userModel.age = 1;
        UserModelManager.getInstance(this)
                .login(userModel, new LoggingConsumer<UserModel>(TAG, "login") {
                    @Override
                    public void success(UserModel value) {
                        // main thread
                    }
                });


    }

    public void start(View view) {
        new WriteThread(this).start();
        for (int i = 0; i < 10; i++) {
            new ReadThread(i, MainActivity.this).start();
        }
    }

    static class ReadThread extends Thread {
        private int id;
        private Context context;

        public ReadThread(int id, Context context) {
            this.id = id;
            this.context = context;
        }

        @Override
        public void run() {

            int time = 0;
            while (true) {
                ThreadLocal<UserModel> currentThreadUser = UserModelManager
                        .getInstance(context)
                        .getLoginUser();

//                Log.d(TAG, "Read Thread : " + id + " result : " + currentThreadUser.get().toString());

                time++;
                if (time > 100) {
                    break;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class WriteThread extends Thread {
        private Context context;

        public WriteThread(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            int time = 0;
            while (true) {
                ThreadLocal<UserModel> currentThreadUser = UserModelManager
                        .getInstance(context)
                        .getLoginUser();
                currentThreadUser.get().age = time;

                UserModelManager.getInstance(context)
                        .updateLoginUser(currentThreadUser.get());

//                Log.d(TAG, "Write Thread result : " + currentThreadUser.get().toString());

                time++;
                if (time > 10) {
                    break;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
