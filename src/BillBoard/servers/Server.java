package BillBoard.servers;

import org.mariadb.jdbc.internal.com.Packet;
import org.w3c.dom.Document;

import java.awt.BorderLayout;
import java.io.*;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JTextArea;


import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;

public class Server extends JFrame {

    // This line below for DB connection //

    Connection instance = null;

    Statement statement = null;


    String ID_check = null;
    String PW_check = null;

    String[] usernames = new String[20]; //Store username
    String[] passwords = new String[20]; //Store password

    String create_permission = null;
    String edit_permission = null;
    String list_permission = null;
    String schedule_permission = null;

    public static int serverPort = 12345;
    public static ServerSocket serverSocket;
    public static Socket socket;
    public static String serverId = "127.0.0.1";
    public static InetAddress ia;
    public static BufferedReader in = null;
    public static PrintWriter out;


    public void GetUsername() throws SQLException {
        ResultSet resultSet = statement.executeQuery("Select * from userdata"); // This resultset for get id and password from db
        resultSet.next();

        ResultSet resultSet2 = statement.executeQuery("SELECT COUNT(*) FROM userdata"); // This will be use to count row number of table
        resultSet2.next();

        int length = resultSet2.getInt(1);


        for(int i = 0; i < length ; i ++){
            usernames[i] = resultSet.getString("ID");
            passwords[i] = resultSet.getString("PASSWORD");
            resultSet.next();
        }
    }

    public void DBConnection() throws SQLException {

        Properties props = new Properties();
        FileInputStream in = null;

        try {
            in = new FileInputStream("db.props");
            props.load(in);
            in.close();
            // specify the data source, username and password
            String url = props.getProperty("jdbc.url");
            String username = props.getProperty("jdbc.username");
            String password = props.getProperty("jdbc.password");
            String schema = props.getProperty("jdbc.schema");

            // get a connection
            instance = DriverManager.getConnection(url + "/" + schema, username,
                    password);
            //create billboard table
            Statement stmt = instance.createStatement();
            StringBuilder sb = new StringBuilder();
            String sql = sb.append("create table if not exists billboard (")
                    .append("name varchar(20),")
                    .append("bgColor varchar(10),")
                    .append("MessageText varchar(100),")
                    .append("MessageColor varchar(10),")
                    .append("InformationText varchar(100),")
                    .append("InformationColor varchar(10),")
                    .append("URL varchar(300)")
                    .append(");").toString();
            stmt.execute(sql);




        } catch (SQLException sqle) {
            System.err.println(sqle);
        } catch (FileNotFoundException fnfe) {
            System.err.println(fnfe);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        statement = instance.createStatement();

        String query = "Create table if not exists userdata (ID varchar(20),PASSWORD varchar(20),createPermission int,editPermission int, schedulePermission int)";
        String query2 = "insert into userdata values ('start','1',0,0,0)";

        statement.executeQuery(query);
        statement.executeQuery(query2);

        GetUsername();


        ResultSet resultSet2 = statement.executeQuery("SELECT COUNT(*) FROM userdata"); // This will be use to count row number of table
        resultSet2.next();
        int length = resultSet2.getInt(1);

        String admin = "admin";
        String admin_password = "302114";

        String start = "new";

        for(int i = 0; i < length; i ++){
            if(usernames[i].equals("admin")){
                start = "old";
            }
        }

        // If there is no administration id, make id
        if(start == "new"){
            String admin_query = "Insert into userdata values " + "(" + "'" + admin + "'" + "," + "'" + admin_password + "'" + "," + "1" + "," + "1" + "," + "1" +")";
            statement.executeQuery(admin_query);
        }



    }
    private JTextArea textArea;

    private static void initClient() throws IOException {


        serverSocket = new ServerSocket(serverPort);
        ia = InetAddress.getByName(serverId);
        socket = new Socket(ia, serverPort);
    }


    private void doProcess() throws IOException, SQLException {


        socket = serverSocket.accept();
        try {

            socket = serverSocket.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

            while (true) {

            }

        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }




    private String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void sendMessage(String message) throws IOException {
        out.println(message);
        out.flush();
    }


    public Server() throws IOException, SQLException {
        DBConnection();

        initClient();
        doProcess();
    }

    public static void main(String[] args) throws IOException, SQLException {
        Server server = new Server();
    }
}
