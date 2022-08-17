package BillBoard.GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class Home extends JFrame {
    public  Home() throws IOException {

        JFrame frame = new JFrame();
        JPanel homeScreen = new JPanel();
        JPanel buttonPanel = new JPanel();
        BorderLayout layout = new BorderLayout();
        JLabel HomeText = new JLabel("Welcome to your Billboard!",JLabel.CENTER);
        HomeText.setFont(HomeText.getFont().deriveFont(20.0f));

        String file = null;

        JButton viewerBtn = new JButton("Viewer");
        JButton returnBtn = new JButton("Return");

        buttonPanel.add(viewerBtn);
        buttonPanel.add(returnBtn);

        frame.add(homeScreen,layout.NORTH);
        frame.add(buttonPanel,layout.SOUTH);



        frame.setTitle("Billboard Viewer");
        frame.setVisible(true);
        homeScreen.add(HomeText);


        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        viewerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new ServerBillboard();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }


        });
        returnBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        });
    }
    static class BillboardViewer {
        BillboardViewer(String file) {
            JFrame frame = new JFrame();
            JLabel msgLabel;
            JLabel inforLabel;
            frame.setTitle("Billboard");
            int newWidth;
            int newHeight;
            // XML Document parsing
            try {
                File xmlFile = new File(file);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                //get root
                Element root = doc.getDocumentElement();
                //get billboard attribute
                NodeList list = doc.getElementsByTagName("billboard");
                Node bgNode = list.item(0);
                Element bgElement = (Element) bgNode;
                if(bgElement.hasAttribute("background")){
                    String bgColor = bgElement.getAttribute("background");
                    frame.getContentPane().setBackground(Color.decode(bgColor));
                }
                //child node
                NodeList children = root.getChildNodes();

                for(int i =0 ; i< children.getLength(); i++){
                    Node node = children.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE){
                        Element ele = (Element)node;
                        String nodeName = ele.getNodeName();
                        if(nodeName.equals("message")){
                            Node messageNode = node.getFirstChild();
                            String message = messageNode.getNodeValue();
                            msgLabel = new JLabel(message, JLabel.CENTER);
                            msgLabel.setFont(msgLabel.getFont().deriveFont(30.0f));
                            frame.add(msgLabel,BorderLayout.NORTH);
                            if(messageNode.hasAttributes()){
                                String msgColor = ele.getAttribute("colour");
                                msgLabel.setForeground(Color.decode(msgColor));
                            }
                        }
                        if(nodeName.equals("picture")){
                            try{
                                //get url from the node
                                String urlString = ele.getAttribute("url");
                                URL URL = new URL(urlString);
                                Image inputImage = ImageIO.read(URL);
                                //resize the image
                                newWidth = 300;
                                newHeight = 300;
                                Image resizedImage = inputImage.getScaledInstance(newWidth,newHeight,Image.SCALE_SMOOTH);
                                BufferedImage newImage = new BufferedImage(newWidth,newHeight,BufferedImage.TYPE_INT_RGB);
                                Graphics g = newImage.getGraphics();
                                g.drawImage(resizedImage,0,0,null);
                                g.dispose();
                                //add image in the label
                                JLabel imageLabel = new JLabel(new ImageIcon(newImage));
                                imageLabel.setSize(300,300);
                                frame.add(imageLabel,BorderLayout.CENTER);
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(nodeName.equals("information")){
                            Node inforNode = node.getFirstChild();
                            String infor = inforNode.getNodeValue();
                            inforLabel = new JLabel(infor,JLabel.CENTER);
                            inforLabel.setFont(inforLabel.getFont().deriveFont(15.0f));
                            frame.add(inforLabel,BorderLayout.SOUTH);
                            if(inforNode.hasAttributes()){
                                String inforColor = ele.getAttribute("colour");
                                inforLabel.setForeground(Color.decode(inforColor));
                            }
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            frame.setVisible(true);
            frame.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    frame.dispose();
                }
                @Override
                public void mousePressed(MouseEvent e) {
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                }
                @Override
                public void mouseExited(MouseEvent e) {
                }

            });
            frame.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        frame.dispose();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
        }
    }

    public static class ServerBillboard {
        ServerBillboard() throws SQLException {
            Statement st = null;
            ResultSet rs = null;

            Properties props = new Properties();
            FileInputStream in = null;
            try {
                in = new FileInputStream("db.props");
                props.load(in);
                in.close();
                String url = props.getProperty("jdbc.url");
                String username = props.getProperty("jdbc.username");
                String password = props.getProperty("jdbc.password");
                String schema = props.getProperty("jdbc.schema");
                // get a connection
                Connection instance = DriverManager.getConnection(url + "/" + schema, username,
                        password);
                String query = "SELECT * FROM billboard";
                st = instance.createStatement();
                rs = st.executeQuery(query);

                ArrayList<String> name = new ArrayList<String>();
                ArrayList<String> bgColor = new ArrayList<String>();
                ArrayList<String> msgText = new ArrayList<String>();
                ArrayList<String> msgColor = new ArrayList<String>();
                ArrayList<String> inforText = new ArrayList<String>();
                ArrayList<String> inforColor = new ArrayList<String>();
                ArrayList<String> URL = new ArrayList<String>();

                while (rs.next()) {
                    for(int i =0; i<rs.getString("name").length(); i++){
                        name.add(rs.getString(1));
                        bgColor.add(rs.getString(2));
                        msgText.add(rs.getString(3));
                        msgColor.add(rs.getString(4));
                        inforText.add(rs.getString(5));
                        inforColor.add(rs.getString(6));
                        URL.add(rs.getString(7));
                        // create xml file from the data in the server.

                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder;
                        dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.newDocument();
                        Element rootElement = doc.createElement("billboard");
                        if (rs.getString(2).length() != 0) {
                            rootElement.setAttribute("background", rs.getString(2));
                            // append root element to document
                        } else {
                            rootElement.setAttribute("background", "#FFFFFF");
                        }
                        doc.appendChild(rootElement);
                        //create a message element from the server data
                        if (rs.getString(3).length() != 0) {
                            Element element_message = doc.createElement("message");
                            element_message.setTextContent(rs.getString(3));
                            if (rs.getString(4).length() != 0) {
                                element_message.setAttribute("colour", rs.getString(4));
                            }
                            rootElement.appendChild(element_message);
                        }
                        //create an information element from the server data
                        if (rs.getString(5).length() != 0) {
                            Element element_information = doc.createElement("information");
                            element_information.setTextContent(rs.getString(5));
                            if (rs.getString(6).length() != 0) {
                                element_information.setAttribute("colour", rs.getString(6));
                            }
                            rootElement.appendChild(element_information);
                        }
                        // create a picture element from the server data
                        if (rs.getString(7).length() != 0) {
                            Element element_picture = doc.createElement("picture");
                            element_picture.setAttribute("url", rs.getString(7));
                            rootElement.appendChild(element_picture);
                        }
                        // write the data into xml
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                        DOMSource source = new DOMSource(doc);
                        StreamResult file = new StreamResult(new File("billboards\\"+ rs.getString(1) + ".xml"));
                        transformer.transform(source,file);
                        long start = System.currentTimeMillis();
                        long end = start + 15 ;
                        while (start < end) {
                            new BillboardViewer(rs.getString("name")+".xml");
                        }
                    }
                }
                st.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }


    }


    public static void main(String[] args) throws IOException {
        new Home();
    }
}