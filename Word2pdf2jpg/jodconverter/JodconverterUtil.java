package cn.boltit.s421.common.utils;

import cn.boltit.s421.common.config.Global;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

import java.io.File;

/**
 * Created by Administrator on 2018/6/7.
 * 跨平台openOffice有个各个平台的版本
 */
public class JodconverterUtil {

    public static void main(String[] args) {
        String docurl = "http://47.92.53.234:5984/kalix/2f0b21720b124bc6bff37d96e2769f63/中华人民共和国禁毒法.docx";
        String docfile = "E:/test.docx";
        //docurl = "http://47.92.53.234:5984/kalix/38667ba15b6944519020175a4ff69d26/行政执法评议考核制度.doc";
        docfile = "E:/test.doc";
        //docfile = "/root/test.doc";
        //JacobUtil.downloadFile(docurl, docfile);
        String pdffile = "E:/test.pdf";
        //pdffile = "/root/test.pdf";
        word2pdf(docfile, pdffile);
    }

    public static void word2pdf(String docfile, String pdffile) {
        System.out.println("word文档转换pdf开始...");
        long start = System.currentTimeMillis();
        // 启动服务
        // 这里是OpenOffice的安装目录
        String OpenOffice_HOME = "";
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("linux") >= 0) {
            OpenOffice_HOME = Global.getConfig("linux.openoffice.home");
        } else if (OS.indexOf("windows") >= 0) {
            OpenOffice_HOME = Global.getConfig("windows.openoffice.home");
        } else {
            System.out.println("未找到openoffice安装目录，无法进行转换");
            return;
        }
        if (OpenOffice_HOME.charAt(OpenOffice_HOME.length() - 1) != '/') {
            OpenOffice_HOME += "/";
        }
        String openofficePort = Global.getConfig("openoffice.port");
        int port = Integer.valueOf(openofficePort);
        Process pro = null;
        // 启动OpenOffice的服务
        String command = OpenOffice_HOME + "program/soffice -headless -accept=\"socket,host=127.0.0.1,port="
                + openofficePort + ";urp;\" -nofirststartwizard";
        // connect to an OpenOffice.org instance running on port 8100
        OpenOfficeConnection connection = null;
        try {
            // liunx系统测试未通过，需要代码外启动
            if (OS.indexOf("windows") >= 0) {
                pro = Runtime.getRuntime().exec(command);
            }
            connection = new SocketOpenOfficeConnection("127.0.0.1", port);
            connection.connect();
            // convert
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            File fromfile = new File(docfile);
            File tofile = new File(pdffile);
            if (tofile.exists()) {
                tofile.delete();
            }
            converter.convert(fromfile, tofile);
            long end = System.currentTimeMillis();
            System.out.println("转换完成，用时：" + (end - start) + "ms");
        } catch (Exception e) {
            System.out.println("word文档转换pdf失败，原因：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // close the connection
            if (connection != null) connection.disconnect();
            if (pro != null) pro.destroy();
        }
        System.out.println("word文档转换pdf完毕！");
    }
}
