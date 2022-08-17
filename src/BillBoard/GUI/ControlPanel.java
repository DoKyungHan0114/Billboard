package BillBoard.GUI;


import org.w3c.dom.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

// This class for create calendar
class AwtCalendar extends JFrame implements ItemListener, ActionListener {

    Font fnt = new Font("SansSerif", Font.BOLD, 20);

    Calendar now = Calendar.getInstance(); // today's date
    int nowYear, nowMonth;
    int lastDay, week; // last day , last day of month

    // component for header
    JPanel headerPnl = new JPanel();
    JButton previousBut = new JButton("<");
    JButton nextBut = new JButton(">");
    JButton dayBtn = new JButton();
    JLabel yearLbl = new JLabel("year");
    JLabel monthLbl = new JLabel("month");
    JComboBox<Integer> yearCho = new JComboBox<Integer>();
    JComboBox<Integer> monthCho = new JComboBox<Integer>();

    JPanel centerPnl = new JPanel(new BorderLayout());
    JPanel weekTitlePnl = new JPanel(new GridLayout(1, 7, 2, 2));
    JPanel dayPnl = new JPanel(new GridLayout(0, 7, 2, 2));

    public AwtCalendar() {
        setLayout(new BorderLayout());
        nowYear = now.get(Calendar.YEAR); // current year
        nowMonth = now.get(Calendar.MONTH) + 1; //current month = 0 , so + 1
        setYear(nowYear); // create JComboBox of year
        setMonth(nowMonth); // create JComboBox of month

        headerPnl.setBackground((new Color(0, 150, 150)));
        headerPnl.add(previousBut);
        previousBut.setFont(fnt);
        headerPnl.add(yearCho);
        yearCho.setFont(fnt);
        headerPnl.add(yearLbl);
        yearLbl.setFont(fnt);
        headerPnl.add(monthCho);
        monthCho.setFont(fnt);
        headerPnl.add(monthLbl);
        monthLbl.setFont(fnt);
        headerPnl.add(nextBut);
        nextBut.setFont(fnt);



        add(headerPnl, BorderLayout.NORTH);
        weekTitleSet();
        centerPnl.add(weekTitlePnl, BorderLayout.NORTH);
        centerPnl.add(dayPnl, BorderLayout.CENTER);
        dateInfo();
        setSpace(week);
        setDay();

        add(centerPnl, BorderLayout.CENTER);
        setSize(600, 400);
        setLocation(300,300);
        setLocationRelativeTo(null);
        setVisible(true);

        //register event
        yearCho.addItemListener(this);
        monthCho.addItemListener(this);
        previousBut.addActionListener(this);
        nextBut.addActionListener(this);
        dayBtn.addActionListener(this);
    }

    public void itemStateChanged(ItemEvent ie) {
        dayListStart();

    }

    public void actionPerformed(ActionEvent ae) {
        String eventVal = ae.getActionCommand();
        nowYear = (Integer) yearCho.getSelectedItem(); // set of new date
        nowMonth = (Integer) monthCho.getSelectedItem(); // set of new month
        if (eventVal.equals("<")) {
            if (nowMonth == 1) {
                nowYear--;
                nowMonth = 12;

            } else {
                nowMonth--;
            }
        } else if (eventVal.equals(">")) {
            if (nowMonth == 12) {
                ;
                nowYear++;
                nowMonth = 1;
            } else {
                nowMonth++;
            }
        }


        yearCho.setSelectedItem(nowYear);
        monthCho.setSelectedItem(nowMonth);

        dayListStart();

    }

    public void start() {
        setSize(500, 300);
        setVisible(true);
    }

    public void dayListStart() {
        nowYear = (Integer) yearCho.getSelectedItem();
        nowMonth = (Integer) monthCho.getSelectedItem();
        dayPnl.setVisible(false);
        dayPnl.removeAll(); // remove JLabel components of penel

        now.set(nowYear, nowMonth - 1, 1); // set selected year and month in JCombobox
        dateInfo();
        setSpace(week); // print out space
        setDay(); // print out day
        dayPnl.setVisible(true);
        setVisible(true); // frame reloading
        yearCho.removeItemListener(this); // remove event
        yearCho.removeAll(); // remove yearCho list
        setYear(nowYear);
        yearCho.addItemListener(this); // event start
    }
    // create year
    public void setYear(int year) {
        for (int y = year - 100; y < year + 100; y++) {
            yearCho.addItem(y);
        }yearCho.setSelectedItem(year);
    }
    // create month
    public void setMonth(int month) {
        for (int m = 1; m < 12; m++) {
            monthCho.addItem(m);
        }
        monthCho.setSelectedItem(month);
    }
    // set the day of week
    public void weekTitleSet() {
        String weekList[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        JLabel weekTitleLbl[] = new JLabel[weekList.length];
        for (int i = 0; i < weekList.length; i++) {
            weekTitleLbl[i] = new JLabel(weekList[i], JLabel.CENTER);
            weekTitleLbl[i].setBackground(Color.LIGHT_GRAY);
            if (i == 0) weekTitleLbl[i].setForeground(Color.red);

            if (i == 6) weekTitleLbl[i].setForeground(Color.blue);
            weekTitlePnl.add(weekTitleLbl[i]);
            weekTitleLbl[i].setFont(fnt);
        }
    }
    // set space
    public void setSpace(int w) {
        for (int cnt = 1; cnt < w; cnt++) {
            dayPnl.add(new JLabel(""));
        }
    }
    //set days
    public void setDay() {
        JLabel dayLbl[] = new JLabel[lastDay];
        for(int day=1; day<=lastDay; day++){
            JButton dayBtn = new JButton(String.valueOf(day));
            dayLbl[day-1] = new JLabel(String.valueOf(day),JLabel.CENTER);
            now.set(nowYear, nowMonth-1, day);
            int dayWeek = now.get(Calendar.DAY_OF_WEEK);
            if(dayWeek==1) dayLbl[day-1].setForeground(Color.red);
            if(dayWeek==7) dayLbl[day-1].setForeground(Color.blue);

            dayPnl.add(dayBtn);

            dayBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    setVisible(false);


                }
            });
        }
    }

    // print out date
    public void dateInfo() {
        lastDay = now.getActualMaximum(Calendar.DATE);
        now.set(Calendar.DATE, 1);
        week = now.get(Calendar.DAY_OF_WEEK);
    }

}

// From this line, Control panel GUI
public class ControlPanel extends JFrame {

    public ControlPanel() throws IOException {
    }


    public static class LOGIN_CLIENT extends JFrame {

        // Declare Global Variable for sending Data to server
        public static String id = ""; // Username string at login
        public static char[] pw = null; // Password at login
        public static String Reg_id = ""; // Username at Register
        public static char[] Reg_pw1 = null; // Password at Register

        // These variables for checking permission once if user login
        public int CreatePermission = 0;
        public int EditPermission = 0;
        public int SchedulePermission = 0;

        // These variables for login process
        String[] usernames = new String[20]; //Store every username from database
        String[] passwords = new String[20]; //Store every password from database

        int[] cPermission = new int[20]; // Create permission
        int[] ePermission = new int[20]; // Edit permission
        int[] sPermission = new int[20]; // Schedule permission


        //Create billboard section variables//
        String getText;
        String getTextColor;
        String getBgColor;
        String getName;
        String getPicture;
        String getMessageText; // message text string at create a billboard
        String getMessageColor; // message color string at create a billboard
        String getInforColor;// information color string at create a billboard
        String getInforText; // information text string at create a billboard
        String getURL; //url string at create a billboard

        //List billboard section variables
        String E_getText;
        String E_getTextColor;
        String E_getBgcolor;
        String E_getName;
        String E_getPicture;
        String E_getInfoColor;
        String E_getInfoText;

        // These lines below for DB connection //
        Connection instance = null; //to store connecntion variables

        Statement statement = null; //For query which is communication with database

        // These lines for socket communications with server
        public static Socket socket = null;

        public static BufferedReader in = null;

        public static PrintWriter out = null;

        public static InetAddress ia = null;

        //initialise the client before connect
        public static void initClient() throws IOException {
            ia = InetAddress.getByName("127.0.0.1");
            socket = new Socket(ia, 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        }
        //Function to send msg to server
        public void sendMessage(String message) throws IOException {
            out.println(message);
            out.flush();
        }

        //Function to recieve message from server
        private String receiveMessage() throws IOException {
            return in.readLine();
        }

        // Processing communicate with server
        private void doProcess() throws IOException {
            new Thread(() -> {
                try {
                    while (true){
                        if( socket.isClosed()) break;

                        String msg = receiveMessage();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // Datebase connection
        public void DBConnection() throws SQLException {
            Properties props = new Properties();
            FileInputStream in = null;
            try {
                in = new FileInputStream("./db.props");
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
            } catch (SQLException sqle) {
                System.err.println(sqle);
            } catch (FileNotFoundException fnfe) {
                System.err.println(fnfe);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            statement = instance.createStatement();
            GetUsername();
        }

        // Get username from database
        public void GetUsername() throws SQLException {
            ResultSet resultSet = statement.executeQuery("Select * from userdata"); // This resultset for get id and password from db
            resultSet.next();

            ResultSet resultSet2 = statement.executeQuery("SELECT COUNT(*) FROM userdata"); // This will be use to count row number of table
            resultSet2.next();

            int length = resultSet2.getInt(1); // Count number of the rows of table

            for(int i = 0; i < length ; i ++){
                usernames[i] = resultSet.getString("ID");
                passwords[i] = resultSet.getString("PASSWORD");
                cPermission[i] = resultSet.getInt(3);
                ePermission[i] = resultSet.getInt(4);
                sPermission[i] = resultSet.getInt(5);
                resultSet.next();
            }


        }

        // LOGIN GUI //

        public LOGIN_CLIENT() throws SQLException, IOException {

            // Connect server and database with GUI
            DBConnection();
            doProcess();


            int width = 600; //Width of program
            int height = 400; // height of program

            JPanel panel = new JPanel(); // Create panel variable for user login
            JPanel Rpanel = new JPanel(); // Rpanel is for Registering member
            JPanel conpanel = new JPanel(); // Create panel variable for control panel
            JPanel Userinfo_Panel = new JPanel(); //Create panel variable for User info section
            JPanel Schedule_Panel = new JPanel(); // Panel for Schedule
            JPanel createPanel = new JPanel(); // Panel for create billboard section
            JPanel List_Panel = new JPanel(); // Panel for list section
            JPanel List_Edit_Panel = new JPanel(); // Panel for list editing section
            JPanel Edit_panel = new JPanel(); // Panel for edit user section
            JPanel Schedule_Panel1 = new JPanel(); // Panel for schedule billboard

            String title = "Control Panel"; // Set a title name
            setTitle(title);

            // Set all layouts to null to customise locations
            panel.setLayout(null);
            Rpanel.setLayout(null);
            conpanel.setLayout(null);
            Userinfo_Panel.setLayout(null);
            Schedule_Panel.setLayout(null);
            createPanel.setLayout(null);
            List_Panel.setLayout(null);
            Edit_panel.setLayout(null);
            List_Edit_Panel.setLayout(null);
            Schedule_Panel1.setLayout(null);

            //***** LOGIN SECTION *****//
            // Declare the button,text field and password field and then set the locations of them
            JLabel label_id = new JLabel("User ID: ");
            JLabel label_password = new JLabel("Password: ");
            JTextField ID = new JTextField(20);
            JPasswordField Password = new JPasswordField(20);
            JButton loginBtn = new JButton("LOGIN");
            label_id.setBounds(160, 125, 90, 15);
            label_password.setBounds(160, 165, 90, 15);
            ID.setBounds(230, 120, 200, 20);
            Password.setBounds(230, 160, 200, 20);
            loginBtn.setBounds(270, 200, 100, 30);

            // Add to the panel
            panel.add(label_id);
            panel.add(ID);
            panel.add(label_password);
            panel.add(Password);
            panel.add(loginBtn);

            add(panel); // Add panel to Jframe

            //*******Control Panel Section*******//
            // Declare the button,text field and password field and then set the locations of them
            JButton createBtn = new JButton("Create Billboards");
            JButton listBtn = new JButton("List Billboards");
            JButton scheduleBtn = new JButton("Schedule");
            JButton editBtn = new JButton("Edit Users");
            JButton logoutBtn = new JButton("Logout");
            JButton REGISTER = new JButton("Create user");

            createBtn.setBounds(90,40,150,30);
            listBtn.setBounds(90,150,150,30);
            scheduleBtn.setBounds(350,150,150,30);
            editBtn.setBounds(350,40,150,30);
            logoutBtn.setBounds(350,260,150,30);
            REGISTER.setBounds(90, 260, 150, 30);

            // Add to control panel
            conpanel.add(createBtn);
            conpanel.add(listBtn);
            conpanel.add(scheduleBtn);
            conpanel.add(editBtn);
            conpanel.add(logoutBtn);
            conpanel.add(REGISTER);



            //***** Schedule SECTION *****//
            JButton sButton1 = new JButton("Return");
            Schedule_Panel.add(sButton1);
            sButton1.setBounds(470, 300, 100, 30);

            //**** JFrame Section ****//
            //Default setting for Jframe
            setVisible(true);
            setSize(width, height);
            setLocationRelativeTo(null);
            setResizable(false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Login button to communicate
            loginBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    String clean = " delete from userdata where ID = 'start'";
                    try {
                        statement.executeQuery(clean);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    id = ID.getText(); // Get ID from test field

                    pw = Password.getPassword(); // Get password from text fr
                    String New_password = new String(pw);

                    // Error handling
                    if (id.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Please type your username", "ERROR!", JOptionPane.PLAIN_MESSAGE);
                    }
                    if(New_password.length() == 0){
                        JOptionPane.showMessageDialog(null, "Please type your password", "ERROR!", JOptionPane.PLAIN_MESSAGE);
                    }

                    // Login process
                    ResultSet result = null;
                    try {
                        result = statement.executeQuery("SELECT COUNT(*) FROM userdata");
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        result.next();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    int length = 0;
                    try {
                        length = result.getInt(1);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    // Login check
                    for(int i = 0; i < length; i++){
                        if(usernames[i].equals(id) && passwords[i].equals(New_password)){
                            panel.setVisible(false);
                            add(conpanel);
                            conpanel.setVisible(true);
                            ID.setText("");
                            Password.setText("");
                            CreatePermission = cPermission[i];
                            EditPermission = ePermission[i];
                            SchedulePermission = sPermission[i];
                        }
                    }
                }
            });

            // This section is for create billboard user
            REGISTER.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if(id.equals("admin")){
                        CreatePermission = 0;
                        EditPermission = 0;
                        SchedulePermission = 0;
                        conpanel.setVisible(false); // Turn off the control panel main
                        Rpanel.setVisible(true); // Turn on the create user panel

                        JButton username_create = new JButton("Create!");
                        JLabel label_id1 = new JLabel("User ID: ");
                        JLabel label_password1 = new JLabel("Password: ");
                        JLabel Intro = new JLabel("Create user");
                        Intro.setFont(Intro.getFont().deriveFont(20.0f));
                        JTextField ID1 = new JTextField(20);

                        JCheckBox createbox = new JCheckBox("Create");
                        createbox.setBounds(200,180,80,20);
                        JCheckBox editbox = new JCheckBox("Edit");
                        editbox.setBounds(300,180,50,20);
                        JCheckBox schedulebox = new JCheckBox("Schedule");
                        schedulebox.setBounds(380,180,100,20);

                        JPasswordField Password1 = new JPasswordField(20);
                        label_id1.setBounds(160, 105, 90, 15);
                        label_password1.setBounds(160, 145, 90, 15);
                        ID1.setBounds(230, 100, 200, 20);
                        Password1.setBounds(230, 140, 200, 20);
                        Intro.setBounds(230, 0, 200, 100);
                        username_create.setBounds(200, 230, 100, 20);
                        JLabel Permission = new JLabel("Permission");
                        Permission.setBounds(80,180,100,20);
                        JButton cancel = new JButton("Cancel");
                        cancel.setBounds(330,230,100,20);

                        Rpanel.add(cancel);
                        Rpanel.add(label_id1);
                        Rpanel.add(ID1);
                        Rpanel.add(createbox);
                        Rpanel.add(editbox);
                        Rpanel.add(schedulebox);
                        Rpanel.add(Permission);
                        Rpanel.add(label_password1);
                        Rpanel.add(Password1);
                        Rpanel.add(Intro);
                        Rpanel.add(username_create);

                        add(Rpanel);

                        createbox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                CreatePermission = 1;
                            }
                        });

                        editbox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                EditPermission = 1;
                            }
                        });
                        schedulebox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                SchedulePermission = 1;
                            }
                        });


                        username_create.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                                Reg_id = ID1.getText();
                                Reg_pw1 = Password1.getPassword();
                                String pw1 = new String(Reg_pw1);

                                // Error handling
                                if(Reg_id.length() == 0 || pw1.length() == 0){
                                    JOptionPane.showMessageDialog(null,"Please fill out the box","ERROR", JOptionPane.PLAIN_MESSAGE);
                                }
                                else{
                                    // Save Id and password in database
                                    String  insertdb = "Insert into userdata values " + "(" + "'" + Reg_id + "'" + "," + "'" + pw1 + "'" + "," + CreatePermission + "," + EditPermission + ","+ SchedulePermission +")";

                                    try {
                                        statement.executeQuery(insertdb);
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                    JOptionPane.showMessageDialog(null,"Success","Username created!", JOptionPane.PLAIN_MESSAGE);
                                    try {
                                        GetUsername();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }

                                    Rpanel.setVisible(false);
                                    conpanel.setVisible(true);
                                    ID1.setText("");
                                    Password1.setText("");

                                    //Reset the permissions
                                    CreatePermission = 0;
                                    EditPermission = 0;
                                    SchedulePermission = 0;
                                    try {
                                        GetUsername();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                }
                            }
                        });

                        // Return to control panel
                        cancel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Rpanel.setVisible(false);
                                conpanel.setVisible(true);
                                ID1.setText("");
                                Password1.setText("");
                            }
                        });

                    }else {
                        JOptionPane.showMessageDialog(null,"Administrator only","Error", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });

            createBtn.addActionListener(e -> {

                if(CreatePermission == 1 || id.equals("admin")){
                    conpanel.setVisible(false);
                    add(createPanel);
                    createPanel.setVisible(true);
                    //Type a name to create a billboard
                    JLabel nameLabel = new JLabel("Name :");
                    JTextField name = new JTextField(20);
                    nameLabel.setBounds(180,45,90,20);
                    name.setBounds(230,45,90,20);

                    //Type a background color to create a billboard
                    JLabel bgColorLabel = new JLabel("Background color :");
                    JTextField bgColor = new JTextField(20);
                    bgColorLabel.setBounds(110,85,120,20);
                    bgColor.setBounds(230,85,200,20);

                    //Type message texts to create a billboard
                    JLabel messageTextLabel = new JLabel("Message Text :");
                    JTextField message = new JTextField(20);
                    messageTextLabel.setBounds(135,125,90,20);
                    message.setBounds(230,125,200,20);

                    //Type a message color to create a billboard
                    JLabel messageColorLabel = new JLabel("Message color :");
                    JTextField messageColor = new JTextField(20);
                    messageColorLabel.setBounds(130,165,90,20);
                    messageColor.setBounds(230,165,200,20);

                    //Type information texts to create a billboard
                    JLabel inforTextLabel = new JLabel("Information Text :");
                    JTextField inforText = new JTextField(20);
                    inforTextLabel.setBounds(120,205,110,20);
                    inforText.setBounds(230,205,200,20);

                    //Type a information color to creating a billboard
                    JLabel inforColorLabel = new JLabel("Information color : ");
                    JTextField inforColor = new JTextField(20);
                    inforColorLabel.setBounds(120,245,130,20);
                    inforColor.setBounds(230,245,200,20);

                    //Get a picture to create a billboard
                    JLabel pictureLabel = new JLabel("URL : ");
                    JTextField picture = new JTextField(20);
                    pictureLabel.setBounds(200,285,90,20);
                    picture.setBounds(230,285,200,20);

                    //Button to get the color and texts
                    JButton submitBtn = new JButton("Submit");
                    submitBtn.setBounds(470,260,100,30);

                    //Button to return
                    JButton returnBtn = new JButton("Return");
                    returnBtn.setBounds(470,300,100,30);

                    // when user click the submit button, it gets the data from the text fields
                    submitBtn.addActionListener(e12 -> {
                        getName = name.getText();
                        getBgColor = bgColor.getText();
                        getMessageText = message.getText();
                        getMessageColor = messageColor.getText();
                        getInforText = inforText.getText();
                        getInforColor = inforColor.getText();
                        getURL = picture.getText();
                        //if the name text field is null, it gives a message to fill it
                        if( getName.length() ==0 ){
                            JOptionPane.showMessageDialog(null, "Please fill the name to create billboard","ERROR!",JOptionPane.PLAIN_MESSAGE);
                        }
                        else{
                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder;
                            try {

                                dBuilder = dbFactory.newDocumentBuilder();
                                Document doc = dBuilder.newDocument();
                                Element rootElement = doc.createElement("billboard");
                                if (getBgColor.length() != 0) {
                                    rootElement.setAttribute("background", getBgColor);
                                    // append root element to document
                                } else {
                                    rootElement.setAttribute("background", "#FFFFFF");
                                }
                                doc.appendChild(rootElement);
                                //create a message element
                                if (getMessageText.length() != 0) {
                                    Element element_message = doc.createElement("message");
                                    element_message.setTextContent(getMessageText);
                                    if (getMessageColor.length() != 0) {
                                        element_message.setAttribute("colour", getMessageColor);
                                    }
                                    rootElement.appendChild(element_message);
                                }
                                //create an information element from the text field
                                if (getInforText.length() != 0) {
                                    Element element_information = doc.createElement("information");
                                    element_information.setTextContent(getInforText);
                                    if (getInforColor.length() != 0) {
                                        element_information.setAttribute("colour", getInforColor);
                                    }
                                    rootElement.appendChild(element_information);
                                }
                                // create a picture element from the text field
                                if (getURL.length() != 0) {
                                    Element element_picture = doc.createElement("picture");
                                    element_picture.setAttribute("url", getURL);
                                    rootElement.appendChild(element_picture);
                                }
                                // write the data into xml
                                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                Transformer transformer = transformerFactory.newTransformer();
                                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                                DOMSource source = new DOMSource(doc);
                                StreamResult file = new StreamResult(new File(getName + ".xml"));
                                transformer.transform(source, file);
                                //give a message that user successfully created the billboard
                                JOptionPane.showMessageDialog(null, "You have successfully created a billboard", "SUCCESS!", JOptionPane.PLAIN_MESSAGE);
                                new Home.BillboardViewer(getName + ".xml");
                                PreparedStatement st = null;
                                try {
                                    String sql = "insert into billboard values(?,?,?,?,?,?,?)";
                                    st = instance.prepareStatement(sql);
                                    st.setString(1, getName);
                                    st.setString(2, getBgColor);
                                    st.setString(3, getMessageText);
                                    st.setString(4, getMessageColor);
                                    st.setString(5, getInforText);
                                    st.setString(6, getInforColor);
                                    st.setString(7, getURL);
                                    st.executeUpdate();
                                } catch (Exception e3) {
                                    e3.printStackTrace();
                                } finally {
                                 st.close();
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    });
                    //add name label
                    createPanel.add(name);
                    createPanel.add(nameLabel);

                    //add background color label
                    createPanel.add(bgColor);
                    createPanel.add(bgColorLabel);

                    //add message label
                    createPanel.add(messageColorLabel);
                    createPanel.add(messageColor);
                    createPanel.add(messageTextLabel);
                    createPanel.add(message);

                    //add information label
                    createPanel.add(inforColor);
                    createPanel.add(inforColorLabel);
                    createPanel.add(inforText);
                    createPanel.add(inforTextLabel);

                    //add picture label
                    createPanel.add(pictureLabel);
                    createPanel.add(picture);

                    //add submit and return button
                    createPanel.add(submitBtn);
                    createPanel.add(returnBtn);

                    returnBtn.addActionListener(e1 -> {
                        createPanel.setVisible(false);
                        conpanel.setVisible(true);
                    });


                }
                else  if (CreatePermission == 0){
                    JOptionPane.showMessageDialog(null,"You Don't have permission","ERROR", JOptionPane.PLAIN_MESSAGE);
                }

            });

            listBtn.addActionListener(e -> {

                conpanel.setVisible(false);
                add(List_Panel);
                List_Panel.setVisible(true);

                // importing list of created billboard names from server

                List Files1 = new ArrayList();
                String Billboard_Table = "select * from billboard";
                try {

                    Statement Billboard_Table_St = instance.createStatement();
                    ResultSet Billboard_Table_RS = Billboard_Table_St.executeQuery(Billboard_Table);


                    while(Billboard_Table_RS.next()){
                        String name = Billboard_Table_RS.getString("name");
                        Files1.add(new String(name));
                    }
                    Billboard_Table_St.close();
                    Billboard_Table_RS.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // creating list with billboard names
                JList ListBox = new JList(Files1.toArray());
                JScrollPane ScrollListBox = new JScrollPane(ListBox);
                ScrollListBox.setBounds(120, 70, 400, 190);
                ListBox.setVisibleRowCount(4);
                ListBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                //selected billboard name
                JLabel Selected_list = new JLabel();
                Selected_list.setBounds(120,50,90,15);

                ListBox.addListSelectionListener(new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting()) {

                            Selected_list.setText(String.valueOf(ListBox.getSelectedValuesList()));
                        }
                    }
                });

                //  edit billboard button
                JButton ListEditBtn = new JButton("Edit");
                ListEditBtn.setBounds(120,270,100,20);

                //  delete billboard button
                JButton ListDeleteBtn = new JButton("Delete");
                ListDeleteBtn.setBounds(120,300,100,20);

                // select list button
                JButton RefreshBtn = new JButton("Refresh");
                RefreshBtn.setBounds(50,70,70,20);

                // label for the list
                JLabel ListLabel = new JLabel("List : ");
                ListLabel.setBounds(50,50,90,15);

                // add return button
                JButton returnBtn_list = new JButton("Return");
                returnBtn_list.setBounds(470,300,100,30);

                List_Panel.add(ListLabel);
                List_Panel.add(ScrollListBox);
                List_Panel.add(ListEditBtn);
                List_Panel.add(ListDeleteBtn);
                List_Panel.add(RefreshBtn);
                List_Panel.add(returnBtn_list);
                List_Panel.add(Selected_list);

                // returnBtn function
                returnBtn_list.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List_Panel.setVisible(false);
                        conpanel.setVisible(true);
                    }
                });
                // DeleteBtn function
                ListDeleteBtn.addActionListener(e15 -> {

                    List x = ListBox.getSelectedValuesList();
                    String fileName = String.valueOf(x);
                    String z = fileName.replace("[","");
                    String z1 = z.replace("]","");

                    String Delete_File = "delete from Billboard where name='" + z1 +"'";

                    if(x != null){
                        Statement Delete_Billboard_St = null;
                        try {
                            Delete_Billboard_St = instance.createStatement();
                            Delete_Billboard_St.executeUpdate(Delete_File);
                            //ResultSet Delete_Billboard_RS = Delete_Billboard_St.executeQuery(Billboard_Table);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(null, "You have successfully deleted the file", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                        List_Panel.revalidate();
                        List_Panel.repaint();
                    }else{
                        JOptionPane.showMessageDialog(null,"There is no selected file");
                    }
                });

                // ListEditBtn function
                ListEditBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List_Panel.setVisible(false);
                        add(List_Edit_Panel);
                        List_Edit_Panel.setVisible((true));

                        // Get a name for creating a billboard from the user
                        JLabel E_nameLabel = new JLabel("Name : ");
                        E_nameLabel.setBounds(180, 45, 90, 20);
                        JTextField E_name = new JTextField(20);
                        E_name.setBounds(230, 45, 90, 20);
                        List_Edit_Panel.add(E_name);
                        List_Edit_Panel.add(E_nameLabel);

                        //Type a text color for creating a billboard from the user
                        JLabel E_txtColorLabel = new JLabel("Message color : ");
                        JTextField E_txtColorText = new JTextField(20);
                        E_txtColorLabel.setBounds(130, 165, 90, 20);
                        E_txtColorText.setBounds(230, 165, 90, 20);
                        List_Edit_Panel.add(E_txtColorLabel);
                        List_Edit_Panel.add(E_txtColorText);

                        //Type a background color for creating a billboard from the user
                        JLabel E_bgColorLabel = new JLabel("Background color : ");
                        JTextField E_bgColorText = new JTextField(20);
                        E_bgColorLabel.setBounds(110, 85, 120, 20);
                        E_bgColorText.setBounds(230, 85, 200, 20);
                        List_Edit_Panel.add(E_bgColorLabel);
                        List_Edit_Panel.add(E_bgColorText);

                        //Type texts for creating a billboard from the user
                        JLabel E_textLabel = new JLabel("Message Text : ");
                        JTextField E_text = new JTextField(20);
                        E_textLabel.setBounds(135, 125, 90, 20);
                        E_text.setBounds(230, 125, 200, 20);
                        List_Edit_Panel.add(E_text);
                        List_Edit_Panel.add(E_textLabel);

                        //Get a picture for creating a billboard
                        JLabel E_pictureLabel = new JLabel("URL : ");
                        JTextField E_picture = new JTextField(20);
                        E_pictureLabel.setBounds(200, 285, 90, 20);
                        E_picture.setBounds(230, 285, 200, 20);
                        List_Edit_Panel.add(E_pictureLabel);
                        List_Edit_Panel.add(E_picture);

                        //Information text
                        JLabel E_InformationTxtLabel = new JLabel("Information Text :");
                        JTextField E_InformationTxt = new JTextField(20);
                        E_InformationTxtLabel.setBounds(120,205,130,20);
                        E_InformationTxt.setBounds(230,205,200,20);
                        List_Edit_Panel.add(E_InformationTxt);
                        List_Edit_Panel.add(E_InformationTxtLabel);

                        //Information text color
                        JLabel E_InformationColorLabel = new JLabel("Information Color :");
                        JTextField E_InformationColor = new JTextField(20);
                        E_InformationColorLabel.setBounds(120,245,130,20);
                        E_InformationColor.setBounds(230,245,200,20);
                        List_Edit_Panel.add(E_InformationColorLabel);
                        List_Edit_Panel.add(E_InformationColor);

                        //Button to get the color and texts
                        JButton E_submitBtn = new JButton("Edit");
                        E_submitBtn.setBounds(470, 260, 100, 30);
                        List_Edit_Panel.add(E_submitBtn);

                        //Button to return
                        JButton E_returnBtn = new JButton("Return");
                        E_returnBtn.setBounds(470, 300, 100, 30);
                        List_Edit_Panel.add(E_returnBtn);

                        E_returnBtn.addActionListener(e1 -> {
                            List_Edit_Panel.setVisible(false);
                            List_Panel.setVisible(true);
                        });

                        //Selected Billboard Name
                        String Sel_Bill = String.valueOf(ListBox.getSelectedValuesList());
                        String z = Sel_Bill.replace("[","");
                        String Sel_Billboard = z.replace("]","");
                        //System.out.println(Sel_Billboard);

                        // query
                        String Q_Bgcolor = "Select bgColor from Billboard where name='" + Sel_Billboard+"'";
                        String Q_MessageTxt = "Select MessageText from Billboard where name='" + Sel_Billboard+"'";
                        String Q_MessageColor = "Select MessageColor from Billboard where name='" + Sel_Billboard+"'";
                        String Q_InfoTxt = "Select InformationText from Billboard where name='" + Sel_Billboard+"'";
                        String Q_InfoColor = "Select InformationColor from Billboard where name='" + Sel_Billboard+"'";
                        String Q_Picture = "Select URL from Billboard where name='" + Sel_Billboard+"'";
                        //System.out.println(Q_Bgcolor);

                        // Select bgColor from Billboard where name='ex1'
                        // Select bgColor from Billboard where name='ex1'

                        //importing stored info from Billboard to text field
                        String Import_Billboard = "Select * from Billboard";

                        try {
                            Statement Import_Billboard_St = instance.createStatement();
                            ResultSet Import_Billboard_RS = Import_Billboard_St.executeQuery(Billboard_Table);


                            while (Import_Billboard_RS.next()) {

                                //String Import_Bgcolor = Import_Billboard_RS.getString(Q_Bgcolor);
                                //String Import_Text = Import_Billboard_RS.getString(Q_MessageTxt);
                                //String Import_Textcolor = Import_Billboard_RS.getString(Q_MessageColor);
                                //String Import_Infotext = Import_Billboard_RS.getString(Q_InfoTxt);
                                //String Import_Infocolor = Import_Billboard_RS.getString(Q_InfoColor);
                                //String Import_Picture = Import_Billboard_RS.getString(Q_Picture);

                                E_name.setText(Sel_Billboard);
                                //E_bgColorText.setText(Import_Bgcolor);
                                //E_text.setText(Import_Text);
                                //E_txtColorText.setText(Import_Textcolor);
                                //E_InformationTxt.setText(Import_Infotext);
                                //E_InformationColor.setText(Import_Infocolor);
                                //E_picture.setText(Import_Picture);

                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                        //XML File select
                        String Sel_fileName = String.valueOf(ListBox.getSelectedValuesList());
                        String x = Sel_fileName;
                        int idx1 = x.indexOf("[");
                        String x1 = x.substring(idx1+1);
                        String x2 = x1.replace("]","");
                        File Sel_file = new File("C:\\Users\\n10324402\\Documents\\CAB302-Group-114-master\\" + x2);



                        // Parsing selected XML File
                        E_submitBtn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                E_getText = E_text.getText();
                                E_getName = E_name.getText();
                                E_getTextColor = E_txtColorText.getText();
                                E_getBgcolor = E_bgColorText.getText();
                                E_getPicture = E_picture.getText();
                                E_getInfoColor = E_InformationColor.getText();
                                E_getInfoText = E_InformationTxt.getText();


                                if( E_getName.length() ==0){
                                    JOptionPane.showMessageDialog(null, "Please fill every parts of the text field to create billboard","ERROR!",JOptionPane.PLAIN_MESSAGE);
                                }
                                else {
                                    try {
                                        String update = "update billboard set name='"+E_getName + "',"
                                                +" bgColor='" + E_getBgcolor+"',"
                                                +" MessageText='" + E_getText + "',"
                                                +" MessageColor='" + E_getTextColor  +"',"
                                                +" InformationText='"+ E_getInfoText +"',"
                                                +" InformationColor='" + E_getInfoColor + "',"
                                                +" URL='" + E_getPicture+"'"
                                                +" where name='" + Sel_Billboard +"'";
                                        Statement update_st;
                                        try {
                                            update_st = instance.createStatement();
                                            update_st.executeUpdate(update);

                                        } catch (SQLException ex) {
                                            ex.printStackTrace();
                                        }

                                        JOptionPane.showMessageDialog(null, "You have successfully edited the file", "SUCCESS", JOptionPane.PLAIN_MESSAGE);

                                        List_Panel.revalidate();
                                        List_Panel.setVisible(true);
                                        List_Edit_Panel.setVisible((false));



                                    } catch (Exception i) {
                                        i.printStackTrace();
                                    }
                                }
                                List_Edit_Panel.revalidate();
                                List_Edit_Panel.repaint();

                            }
                        });



                    }
                });

                // RefreshBtn function
                RefreshBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List_Panel.revalidate();
                        List_Panel.repaint();

                    }
                });
            });

            scheduleBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(SchedulePermission == 1 || id.equals("admin")){
                        conpanel.setVisible(false);
                        Schedule_Panel.setVisible(true);
                        sButton1.setBounds(470,300,100,30);

                        Schedule_Panel.add(sButton1);

                        add(Schedule_Panel);

                        new AwtCalendar();

                        add(Schedule_Panel1);


                        JTextField billName = new JTextField();
                        billName.setBounds(200,100,150,20);

                        JLabel text1 = new JLabel(" Billboard Name: ");
                        text1.setBounds(80,100,150,20);

                        JLabel text2 = new JLabel("Time:");
                        text2.setBounds(120,160,150,20);

                        JButton schedule = new JButton("Schedule");
                        schedule.setBounds(200,300,150,20);

                        JButton sButton1 = new JButton("Return");
                        Schedule_Panel.add(sButton1);
                        sButton1.setBounds(470, 300, 100, 30);


                        String[]list = {"9:00-10:00", "10:00-11:00", "11:00-12:00","12:00-13:00","13:00-14:00","14:00-15:00","15:00-16:00","16:00-17:00","18:00-19:00" };
                        JComboBox<String>jcom = new JComboBox<String>(list);
                        jcom.setBounds(200,160,150,20);

                        Schedule_Panel1.add(jcom);
                        Schedule_Panel1.add(billName);
                        Schedule_Panel1.add(text1);
                        Schedule_Panel1.add(text2);
                        Schedule_Panel1.add(schedule);
                        Schedule_Panel1.add(sButton1);

                        sButton1.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Schedule_Panel.setVisible(false);
                                Schedule_Panel1.setVisible(false);
                                conpanel.setVisible(true);
                            }
                        });
                    }else{
                        JOptionPane.showMessageDialog(null,"You Don't have permission","ERROR", JOptionPane.PLAIN_MESSAGE);
                    }

                }
            });

            editBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                    conpanel.setVisible(false);
                    Edit_panel.setVisible(true);

                    JPanel Delete_panel = new JPanel(); // Delete user section panel
                    Delete_panel.setLayout(null);

                    JPanel Permission_panel = new JPanel();
                    Permission_panel.setLayout(null);

                    JPanel Update_panel = new JPanel();
                    Update_panel.setLayout(null);

                    JButton button_permission = new JButton("Permission");
                    button_permission.setBounds(200,50,200,30);

                    JButton update_button = new JButton("Change password");
                    update_button.setBounds(200,130,200,30);

                    // Declare button in user delete section in edit user
                    JButton user_delete = new JButton("Delete user");
                    user_delete.setBounds(200,210,200,30);


                    JButton return_button = new JButton("Return");
                    return_button.setBounds(450, 300, 100, 30);



                    Edit_panel.add(return_button);
                    Edit_panel.add(user_delete);
                    Edit_panel.add(button_permission);
                    Edit_panel.add(update_button);

                    add(Edit_panel);

                    update_button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Edit_panel.setVisible(false);
                            Update_panel.setVisible(true);

                            // Making return button to go edit panel
                            JButton return_button = new JButton("Return");
                            return_button.setBounds(450, 300, 100, 30);

                            // Making text field to get current password and new password
                            JPasswordField current_pw = new JPasswordField();
                            current_pw.setBounds(230,100,150,20);
                            JPasswordField new_pw = new JPasswordField();
                            new_pw.setBounds(230,150,150,20);

                            // Making labels
                            JLabel text1 = new JLabel("Current password: ");
                            JLabel text2 = new JLabel("New password: ");
                            text1.setBounds(80,100,150,20);
                            text2.setBounds(80,150,150,20);

                            // Making checkbox and funtion for showing password
                            JCheckBox checkEdit = new JCheckBox("Showing");
                            checkEdit.setBounds(400,90,246,39);
                            checkEdit.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    if(checkEdit.isSelected())
                                    {
                                        current_pw.setEchoChar((char)0);
                                        new_pw.setEchoChar((char)0);
                                    }else
                                    {
                                        current_pw.setEchoChar('*');
                                        new_pw.setEchoChar('*');
                                    }
                                }
                            });

                            JButton update_password = new JButton("Update");
                            update_password.setBounds(230,200,150,20);


                            Update_panel.add(return_button);
                            Update_panel.add(current_pw);
                            Update_panel.add(new_pw);
                            Update_panel.add(text1);
                            Update_panel.add(text2);
                            Update_panel.add(checkEdit);
                            Update_panel.add(update_password);

                            add(Update_panel);

                            update_password.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {

                                    String updated_passwords = new String(new_pw.getPassword());
                                    String query = "UPDATE userdata set PASSWORD =" + "'" + updated_passwords + "'" + "WHERE ID =" + "'" + id + "'";
                                    try {
                                        statement.executeQuery(query);
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                    current_pw.setText("");
                                    new_pw.setText("");
                                }
                            });

                            return_button.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Edit_panel.setVisible(true);
                                    Update_panel.setVisible(false);
                                }
                            });
                        }
                    });

                    button_permission.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(id.equals("admin")){

                                int length = 0;
                                ResultSet resultSet2 = null; // This will be use to count row number of table
                                try {
                                    resultSet2 = statement.executeQuery("SELECT COUNT(*) FROM userdata");
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                                try {
                                    resultSet2.next();
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }

                                try {
                                    length = resultSet2.getInt(1); // Count number of the rows of table
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }

                                Edit_panel.setVisible(false);
                                Permission_panel.setVisible(true);

                                JLabel username = new JLabel("Username :");
                                username.setBounds(275,50,100,20);

                                JLabel userlist = new JLabel("User list");
                                userlist.setBounds(110,20,150,30);

                                JButton return_button2 = new JButton("Return");
                                return_button2.setBounds(450, 300, 100, 30);

                                JButton cButton = new JButton("Give Create permission");
                                cButton.setBounds(340,100,200,20);

                                JButton eButton = new JButton("Give Edit permission");
                                eButton.setBounds(340,130,200,20);

                                JButton sButton = new JButton("Give shchedule permission");
                                sButton.setBounds(340,160,200,20);

                                JTextArea userdata = new JTextArea(1,20);
                                userdata.setBounds(50,50,200,300);

                                JTextField per = new JTextField(); // Textfield for typing user name which admin want to give permission
                                per.setBounds(350,55,200,20);

                                for(int i = 0;i < length; i++){
                                    userdata.append(usernames[i] + " \n");
                                }
                                userdata.setEnabled(false);

                                JScrollPane scroll = new JScrollPane(userdata);
                                scroll.setBounds(250,50,10,50);

                                Permission_panel.add(return_button2);
                                Permission_panel.add(userdata);
                                Permission_panel.add(scroll);
                                Permission_panel.add(per);
                                Permission_panel.add(eButton);
                                Permission_panel.add(username);
                                Permission_panel.add(userlist);
                                Permission_panel.add(cButton);
                                Permission_panel.add(sButton);

                                add(Permission_panel);

                                // Give create permission
                                cButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        String getid = per.getText();
                                        String query = "UPDATE userdata SET createPermission = 1 where ID =" + "'" + getid +"'";
                                        try {
                                            statement.executeQuery(query);
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }
                                    }
                                });

                                // Edit create permission
                                eButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        String getid = per.getText();
                                        String query = "UPDATE userdata SET editPermission = 1 where ID =" + "'" + getid +"'";
                                        try {
                                            statement.executeQuery(query);
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }
                                    }
                                });

                                sButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        String getid = per.getText();
                                        String query = "UPDATE userdata SET schedulePermission = 1 where ID =" + "'" + getid +"'";
                                        try {
                                            statement.executeQuery(query);
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }

                                    }
                                });

                                return_button2.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        Edit_panel.setVisible(true);
                                        Permission_panel.setVisible(false);
                                    }
                                });
                            }
                            else{
                                JOptionPane.showMessageDialog(null,"Administrator only","Error", JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    });

                    user_delete.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(id.equals("admin")){
                                int length = 0;
                                ResultSet resultSet2 = null; // This will be use to count row number of table
                                try {
                                    resultSet2 = statement.executeQuery("SELECT COUNT(*) FROM userdata");
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                                try {
                                    resultSet2.next();
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }

                                try {
                                    length = resultSet2.getInt(1); // Count number of the rows of table
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                                Edit_panel.setVisible(false);
                                Delete_panel.setVisible(true);

                                JLabel userlist = new JLabel("User list");
                                userlist.setBounds(110,20,150,30);

                                JLabel username = new JLabel("User to delete: ");
                                username.setBounds(300,130,150,20);

                                JButton return_button2 = new JButton("Return");
                                return_button2.setBounds(450, 300, 100, 30);

                                JButton dButton = new JButton("Delete");
                                dButton.setBounds(460,150,100,20);

                                JTextArea userdata = new JTextArea(1,20);
                                userdata.setBounds(50,50,200,300);

                                JTextField delete = new JTextField();
                                delete.setBounds(300,150,150,20);

                                for(int i = 0;i < length; i++){
                                    userdata.append(usernames[i] + " \n");
                                }
                                userdata.setEnabled(false);

                                JScrollPane scroll = new JScrollPane(userdata);
                                scroll.setBounds(250,50,10,50);

                                Delete_panel.add(return_button2);
                                Delete_panel.add(userdata);
                                Delete_panel.add(scroll);
                                Delete_panel.add(delete);
                                Delete_panel.add(dButton);
                                Delete_panel.add(userlist);
                                Delete_panel.add(username);

                                add(Delete_panel);

                                dButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        String getid = delete.getText();

                                        if(getid.equals("admin")){
                                            getid = "";
                                            JOptionPane.showMessageDialog(null,"Administrator's accont cannot be deleted","ERROR", JOptionPane.PLAIN_MESSAGE);
                                        }

                                        String query = "Delete from userdata where ID = " + "'" + getid + "'";
                                        try {
                                            statement.executeQuery(query);
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }
                                        delete.setText("");

                                        userdata.setText("");

                                        int length = 0;
                                        ResultSet resultSet2 = null; // This will be use to count row number of table
                                        try {
                                            resultSet2 = statement.executeQuery("SELECT COUNT(*) FROM userdata");
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }
                                        try {
                                            resultSet2.next();
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }

                                        try {
                                            length = resultSet2.getInt(1); // Count number of the rows of table
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }
                                        for(int i = 0;i < length; i++){
                                            userdata.append(usernames[i] + " \n");
                                        }
                                    }
                                });


                                return_button2.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        Edit_panel.setVisible(true);
                                        Delete_panel.setVisible(false);
                                    }
                                });
                            }

                            else{
                                JOptionPane.showMessageDialog(null,"Administrator only","Error", JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    });

                    // Return to control panel
                    return_button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Edit_panel.setVisible(false);
                            conpanel.setVisible(true);
                        }
                    });
                }
            });

            // Make logout button to return
            logoutBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null,"You have been successfully logged out!");
                    conpanel.setVisible(false);
                    panel.setVisible(true);
                }
            });
        }


        public static void main(String[] args) throws IOException, SQLException {
            initClient();
            new LOGIN_CLIENT();
        }
    }
}


