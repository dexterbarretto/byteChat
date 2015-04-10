package dexterbarretto.com.bytechat.model;

import org.jivesoftware.smack.AbstractXMPPConnection;

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

public class ConnectionHolder {
    private static AbstractXMPPConnection connection;

    public static AbstractXMPPConnection getConnection() {
        return connection;
    }

    public static void setConnection(AbstractXMPPConnection connection) {
        ConnectionHolder.connection = connection;
    }
}
