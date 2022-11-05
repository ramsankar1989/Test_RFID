/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epay_rfid;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
/**
 *
 * @author Pavalavel
 */
public class Epay_RFID {

    /**
     * @param args the command line arguments
     */
    public String barcode_data=null; 
    public static void main(String[] args) throws IOException {
       	//Loading the required JDBC Driver class
        boolean append = true;
        
        FileHandler handler = new FileHandler("Barcode_LogFile.log", append);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  
        Date date = new Date();  
        //System.out.println(formatter.format(date));  
        Logger logger = Logger.getLogger("epay_rfid");
        logger.addHandler(handler); 
        SimpleFormatter simple_formatter = new SimpleFormatter();  
        handler.setFormatter(simple_formatter); 
        try{
             int c;
             Socket s = new Socket("10.13.10.201", 10000);
             InputStream in = s.getInputStream();
             OutputStream out = s.getOutputStream();
             String str = (args.length == 0 ? "osborne.com" : args[0]) + "\n";
             byte buf[] = str.getBytes();
             out.write(buf);             
            /* BufferedReader reader = new BufferedReader(new InputStreamReader(in));
             String line;
             while ((line = reader.readLine()) != null) {
              logger.info(""+ formatter.format(date) +"\t"+  line);
              insert_data(line,logger); 
             }**/
             
            int count=1;
            StringBuffer sb=new StringBuffer(); 
            while ((c = in.read()) != -1) {
           
                if(count>3){
                    //if(!(sb.toString().contains("?"))){
                        System.out.println(sb.toString());
                        logger.info(""+ formatter.format(date) +"\t"+  sb.toString());
                        insert_data(sb.toString(),logger); 
                        count = 1;
                        sb= new StringBuffer();
                    //}
                }   
            count ++;
                if((char) c != '?') {
                sb.append((char) c);
                }
            }
             
 /*            while ((c = in.read()) != -1) {
             logger.info(""+ formatter.format(date) +"\t"+  String.valueOf(c));
             insert_data(String.valueOf(c),logger); 
            }*/
             //logger.info(""+ formatter.format(date) +"\t"+  textBuilder.toString());
             //insert_data(textBuilder.toString(),logger);
             
             s.close();
        
    } catch(Exception e){
        logger.info("ERROR:\t"+ formatter.format(date) +"\t"+  e.toString());
    }  
        

  } 

public static void insert_data(String barcode,Logger logger) throws Exception 
{
    FileInputStream fis=new FileInputStream("connection.prop"); 
                Properties p=new Properties (); 
                p.load (fis);
                
                String dname= (String) p.get ("Dname"); 
                String url= (String) p.get ("URL"); 
                String username= (String) p.get ("Uname"); 
                String password= (String) p.get ("password"); 
                String db_query= (String) p.get ("query");
                String status="pending";
                String flag="N";
                Class.forName(dname);	
		
		//Creating a connection to the database
		Connection conn = DriverManager.getConnection(url);
 		//Executing SQL query and fetching the result
		Statement st = conn.createStatement();
		String sqlStr = db_query;
                String lineId  = "L1";
                String binType = "Gen";
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  
                Date date = new Date();  
                String query = "INSERT INTO tbl_RFBinIN" +
                                "(BinID" +
                                ",LineID" +
                                ",BinType" +
                                ",DateIn" +
                                ",Status" +
                                ",Flag)" +
                                "VALUES" +
                                "("+barcode+
                                ",'"+lineId+
                                "','"+binType+
                                "',"+formatter.format(date)+
                                ",'"+status+
                                "','"+flag+"')";
                
                System.out.println(query);   
                String sql = query;
                st.executeUpdate(sql);
		logger.info("INFO:\t"+ formatter.format(date) +"\t"+  sql);
                conn.close();
}
}