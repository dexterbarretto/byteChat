package dexterbarretto.com.bytechat.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import dexterbarretto.com.bytechat.BuildConfig;
import dexterbarretto.com.bytechat.R;
import dexterbarretto.com.bytechat.model.ConnectionHolder;

/**
 *
 * Created by Dexter on 10-04-2015.
 * email: barrettodexter@yahoo.com
 *
 * This code is provided on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, including, without limitation, any warranties or conditions of
 * TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
 * You are solely responsible for determining the appropriateness of using or redistributing the code
 * and assume any risks associated with Your exercise of permissions under this License.
 *
 */


public class LoginActivity extends Activity {

    private EditText username_EditText;
    private EditText password_EditText;
    private Button login_Button;

    private AbstractXMPPConnection connection;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Linking Layout Elements
        username_EditText = (EditText) findViewById(R.id.username_EditText);
        password_EditText = (EditText) findViewById(R.id.password_EditText);
        login_Button = (Button) findViewById(R.id.login_Button);

        //OnClick Listener for login_Button
        login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = username_EditText.getText().toString();
                password = password_EditText.getText().toString();

                if (username.trim() != null || username.trim()!="" && password.trim() != null || password.trim()!="") {
                    if (NetworkCheck() == true) {
                        new connect().execute();
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Please check your network connectivity!",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this,"Username or Password Empty!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void Authenticate() {
        if (NetworkCheck() == false) {
            Toast.makeText(this, "Could not connect with server", Toast.LENGTH_LONG).show();
        }

        if (connection != null && !connection.isAuthenticated()) {
            Toast.makeText(this, "Wrong Username or Password", Toast.LENGTH_LONG).show();
        }

        if (connection != null && connection.isAuthenticated()) {
            Intent activity_chat_Intent = new Intent(LoginActivity.this, ChatActivity.class);
            ConnectionHolder.setConnection(connection);
            startActivity(activity_chat_Intent);
        }

    }

    private boolean NetworkCheck() {
        boolean check = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            check = true;
        }
        return check;
    }

    private class connect extends AsyncTask<Void, Void, Void> {

        final int SERVER_PORT = 5222;//Service's Port
        final String SERVER_HOST = "talk.google.com";//Server's IP or Host Domain Name
        final String SERVER_NAME = "gmail.com";//Service's IP or Name
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(LoginActivity.this, "Connecting...", "Please wait...", false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (connection == null || !connection.isAuthenticated()) {
                // Create the configuration for this new connection
                XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder()
                        //Setting Username & Password
                        .setUsernameAndPassword(username, password)
                                //Setting Service's Name
                        .setServiceName(SERVER_NAME)
                                //Setting Host or Server IP
                        .setHost(SERVER_HOST)
                                //Setting Service Ports
                        .setPort(SERVER_PORT)
                                //Setting and using default Presence TODO: Create custom presence
                        .setSendPresence(true)
                                //Enabling Debugging
                        .setDebuggerEnabled(BuildConfig.DEBUG);


                connection = new XMPPTCPConnection(config.build());
                try {
                    // Connect to the server
                    connection.connect();
                    Log.i("[LoginActivity]", "Connected to Server");
                    // Log into the server
                    connection.login();
                    Log.i("[LoginActivity]", "Credentials Authenticated");


                } catch (SmackException e) {
                    Log.i("[LoginActivity-Smk>Ex]", "SmackException :" + e);

                } catch (IOException e) {
                    Log.i("[LoginActivity-IO>Ex]", "IOException :" + e);

                } catch (XMPPException e) {
                    Log.i("[LoginActivity-XMPP>Ex]", "XMPPException :" + e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            Authenticate();
        }
    }

}


