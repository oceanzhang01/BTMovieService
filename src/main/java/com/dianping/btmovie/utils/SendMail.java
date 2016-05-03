package com.dianping.btmovie.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

/**
 * Created by oceanzhang on 16/1/14.
 */
public class SendMail {
    public static void sendEmail(){
        try {
            String to = "546107362@qq.com";
            String subject = "UCDisk Cookie update";
            String content = "neet update ucdisk cookie.";
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.sina.com");
            properties.put("mail.smtp.port", "25");
            properties.put("mail.smtp.auth", "true");
            //nieiqmhj997038@sina.com:sunny968
            Authenticator authenticator = new MyAuthenticator("nieiqmhj997038@sina.com", "sunny968");
            javax.mail.Session sendMailSession = javax.mail.Session.getDefaultInstance(properties, authenticator);
            MimeMessage mailMessage = new MimeMessage(sendMailSession);
            mailMessage.setFrom(new InternetAddress("nieiqmhj997038@sina.com"));
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mailMessage.setSubject(subject, "UTF-8");
            mailMessage.setSentDate(new Date());
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();
            html.setContent(content.trim(), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            mailMessage.setContent(mainPart);
            Transport.send(mailMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
//            SendMail.sendMessage("smtp.qq.com", "1099788817@qq.com",
//                    "z930610", "546107362@qq.com", "UCDisk Cookie update",
//                    "neet update ucdisk cookie.",
//                    "text/html;charset=utf-8");
            SendMail.sendEmail();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
class MyAuthenticator extends Authenticator{
    String userName="";
    String password="";
    public MyAuthenticator(){

    }
    public MyAuthenticator(String userName,String password){
        this.userName=userName;
        this.password=password;
    }
    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(userName, password);
    }
}

