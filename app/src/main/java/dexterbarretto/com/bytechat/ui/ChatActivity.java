package dexterbarretto.com.bytechat.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

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

public class ChatActivity extends Activity {

    private ImageButton send_ImageButton;
    private EditText recipient_EditText;
    private EditText textMessage_EditText;
    private ListView conversation_ListView;

    private ArrayList<String> conversation_ArrayList = new ArrayList<String>();


    private AbstractXMPPConnection connection;
    private ChatManager chatManager;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        connection.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_demo);

        //Linking Layout Elements
        send_ImageButton = (ImageButton) findViewById(R.id.send_ImageButton);
        recipient_EditText = (EditText) findViewById(R.id.recipient_EditText);
        textMessage_EditText = (EditText) findViewById(R.id.textMessage_EditText);
        conversation_ListView = (ListView) findViewById(R.id.conversation_ListView);

        connection = ConnectionHolder.getConnection();

        send_ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textMessage_EditText.getText().toString().trim()!=null ||
                        textMessage_EditText.getText().toString().trim()!="")
                conversation_ArrayList.add(connection.getUser() + " : " + textMessage_EditText.getText().toString());
                sendMessage(recipient_EditText.getText().toString(), textMessage_EditText.getText().toString());
                textMessage_EditText.getText().clear();
                setListAdapter(ChatActivity.this);
            }
        });

        //Code to listen for Receiving Messages
        ChatManager.getInstanceFor(connection).addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        if (message.getType() == Message.Type.chat || message.getType() == Message.Type.normal) {
                            if (message.getBody() != null) {
                                conversation_ArrayList.add(message.getFrom() + " : " + message.getBody());
                                setListAdapter(ChatActivity.this);
                            }
                        }
                    }
                });
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.disconnect();
    }

    //Code to set List Adapter
    private void setListAdapter(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_item, conversation_ArrayList);
                conversation_ListView.setAdapter(adapter);
            }
        });
    }

    //Code to send Messages
    private void sendMessage(String id, String msg) {
        if (connection.isAuthenticated()) {
            chatManager = ChatManager.getInstanceFor(connection);
            Chat chat = chatManager.createChat(id, new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    System.out.println("Received message: " + message);
                }
            });
            try {
                chat.sendMessage(msg);
            } catch (SmackException.NotConnectedException e) {
                Log.i("[ChatActivity]", "Error Delivering block");
            }
        }
    }
}
