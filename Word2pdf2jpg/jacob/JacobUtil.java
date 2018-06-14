package cn.boltit.s421.common.utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.lowagie.text.pdf.PdfReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jfree.util.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2018/6/7.
 * 仅支持windows
 */
public class JacobUtil {
    public static final String DOC = "doc";
    public static final String DOCX = "docx";
    public static final String PDF = "pdf";
    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";
    public static final String MP4 = "mp4";
    public static final String PPT = "ppt";
    public static final String PPTX = "pptx";

    // 8 代表word保存成html
    public static final int WORD2HTML = 8;
    // 17代表word保存成pdf
    public static final int WD2PDF = 17;
    public static final int PPT2PDF = 32;
    public static final int XLS2PDF = 0;

    public static void main(String[] args) {
//        String pptfile = "C:/Users/Administrator/Desktop/ceshi.pptx";
//        String pdffile = "C:/Users/Administrator/Desktop/数字模拟电路.pdf";
//        ppt2pdf(pptfile,pdffile);
//        pdf2Image(pdffile);
        String docurl = "http://47.92.53.234:5984/kalix/2f0b21720b124bc6bff37d96e2769f63/中华人民共和国禁毒法.docx";
        String docfile = "E:/test.docx";
        docurl = "http://47.92.53.234:5984/kalix/38667ba15b6944519020175a4ff69d26/行政执法评议考核制度.doc";
        docfile = "E:/test.doc";
        downloadFile(docurl, docfile);
        String pdffile = "E:/test.pdf";
        word2pdf(docfile, pdffile);
        pdf2Image(pdffile, 96);
    }

    public static void downloadFile(String fileUrl, String localFile) {
        URL urlfile = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File f = new File(localFile);
        try {
            int index = fileUrl.lastIndexOf("/");
            String fileName = URLEncoder.encode(fileUrl.substring(index + 1), "utf-8");
            fileUrl = fileUrl.substring(0, index + 1);
            urlfile = new URL(fileUrl + fileName);
            httpUrl = (HttpURLConnection) urlfile.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(f));
            int len = 2048;
            byte[] b = new byte[len];
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            bos.flush();
            System.out.println("文件下载保存成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
                if (httpUrl != null) httpUrl.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void word2pdf(String docfile, String pdffile) {
        System.out.println("word文档转换pdf开始...");
        long start = System.currentTimeMillis();
        // 启动word应用程序(Microsoft Office Word 2007+)
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent("Word.Application");
            // 设置word应用程序不可见
            //app.setProperty("Visible", new Variant(false));
            app.setProperty("Visible", false);
            // documents表示word程序的所有文档窗口(word是多文档应用程序)
            Dispatch docs = app.getProperty("Documents").toDispatch();
            // 打开要转换的word文件
            /*doc = Dispatch.invoke(docs, "Open", Dispatch.Method,
                    new Object[] { docfile, new Variant(false), new Variant(true) }, new int[1]).toDispatch();*/
            doc = Dispatch.call(docs, "Open", docfile, false, true).toDispatch();
            // 调用Document对象的saveAs方法,将文档保存为pdf格式
            /*Dispatch.invoke(doc, "ExportAsFixedFormat", Dispatch.Method, new Object[] {
                    pdffile, new Variant(wdFormatPDF) }, new int[1]);*/
            //Dispatch.call(doc, "SaveAs", pdffile, WD2PDF);
            File tofile = new File(pdffile);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(doc, "ExportAsFixedFormat", pdffile, WD2PDF);
            long end = System.currentTimeMillis();
            System.out.println("转换完成，用时：" + (end - start) + "ms");
        } catch (Exception e) {
            System.out.println("word文档转换pdf失败，原因：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭word文件
            Dispatch.call(doc, "Close", false);
            // 关闭word应用程序
            if (app != null) {
                //app.invoke("Quit", new Variant[]{});
                app.invoke("Quit", 0);
            }
            // 关闭winword.exe进程
            ComThread.Release();
        }
        System.out.println("word文档转换pdf完毕！");
    }

    /***
     * PDF文件转jpg图片，全部页数
     *
     * @param pdffile pdf文件，包括完整路径和.pdf文件名称
     * @param dpi     dpi越大转换后越清晰，相对转换速度越慢
     * @return
     * @author hqj:
     * @version 创建时间：2018年06月09日
     */
    public static void pdf2Image(String pdffile, int dpi) {
        System.out.println("pdf文档转换jpg开始...");
        File file = new File(pdffile);
        PDDocument pdDocument = null;
        PDFRenderer renderer = null;
        try {
            String imgPDFPath = file.getParent();
            int dot = file.getName().lastIndexOf('.');
            // 获取图片文件名
            String imagePDFName = file.getName().substring(0, dot);
            pdDocument = PDDocument.load(file);
            renderer = new PDFRenderer(pdDocument);
            /* dpi越大转换后越清晰，相对转换速度越慢 */
            PdfReader reader = new PdfReader(pdffile);
            int pages = reader.getNumberOfPages();
            int pages_len = String.valueOf(pages).length();
            String imgFilePathPrefix = imgPDFPath + File.separator + imagePDFName;
            for (int i = 0; i < pages; i++) {
                StringBuffer imgFilePath = new StringBuffer();
                imgFilePath.append(imgFilePathPrefix);
                imgFilePath.append("_");
                // 数字长度左补0
                String tmpStr = String.valueOf(i + 1);
                int b0 = pages_len - tmpStr.length();
                for (int j = 0; j < b0; j++) {
                    tmpStr = "0" + tmpStr;
                }
                imgFilePath.append(tmpStr);
                imgFilePath.append(".jpg");
                File dstFile = new File(imgFilePath.toString());
                BufferedImage image = renderer.renderImageWithDPI(i, dpi);
                ImageIO.write(image, "jpg", dstFile);
            }
            System.out.println("pdf文档转换jpg成功！");
        } catch (IOException e) {
            System.out.println("pdf文档转换jpg失败，原因：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (pdDocument != null) {
                try {
                    pdDocument.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("pdf文档转换jpg完毕！");
    }

    /**
     * @param resourceType 资源类型
     * @param resourcePath 资源路径
     * @return TODO 文件转换
     * @author hqj:
     * @version 创建时间：2018年6月8日
     */
    public static Integer formatConvert(String resourceType, String resourcePath) {
        Integer pages = 0;
        String resource = resourcePath.substring(0, resourcePath.lastIndexOf("."));
        if (resourceType.equalsIgnoreCase(DOC) || resourceType.equalsIgnoreCase(DOCX)) {
            //word转成pdf和图片
            word2pdf(resourcePath, resource + ".pdf");
            //pages = pdf2Image(resource + ".pdf");
        } else if (resourceType.equalsIgnoreCase(PDF)) {
            //pdf转成图片
            //pages = pdf2Image(resourcePath);
        } else if (resourceType.equalsIgnoreCase(XLS) || resourceType.equalsIgnoreCase(XLSX)) {
            //excel文件转成图片
            excel2pdf(resourcePath, resource + ".pdf");
            //pages = pdf2Image(resource + ".pdf");
        } else if (resourceType.equalsIgnoreCase(PPT) || resourceType.equalsIgnoreCase(PPTX)) {
            //ppt2pdf(resourcePath, resource+".pdf");
            //pages = pdf2Image(resource+".pdf");
            pages = ppt2Image(resourcePath, resource + ".jpg");
        } else if (resourceType.equalsIgnoreCase(MP4)) {
            //视频文件不转换
            pages = 0;
        }
        return pages;
    }

    /**
     * @param pptfile
     * @param imgfile TODO  ppt转换成图片
     * @author shenjianhu:
     * @version 创建时间：2017年4月18日 下午3:08:11
     */
    public static Integer ppt2Image(String pptfile, String imgfile) {
        String imageDir = pptfile.substring(0, pptfile.lastIndexOf("."));
        File dir = new File(imageDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        int length = 0;
        ActiveXComponent app = null;
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent("PowerPoint.Application");
            System.out.println("准备打开ppt文档");
            app.setProperty("Visible", true);
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            Dispatch ppt = Dispatch.call(ppts, "Open", pptfile, true, true, true).toDispatch();
            System.out.println("-----------------ppt开始转换图片---------------");
            Dispatch.call(ppt, "SaveCopyAs", imgfile, 17);
            System.out.println("-----------------ppt转换图片结束---------------");
            Dispatch.call(ppt, "Close");
            System.out.println("关闭ppt文档");
        } catch (Exception e) {
            ComThread.Release();
            e.printStackTrace();
        } finally {
            String files[];
            files = dir.list();
            length = files.length;
            System.out.println(length);
            app.invoke("Quit");
            ComThread.Release();
        }
        return length;
    }

    /**
     * WORD转HTML
     *
     * @param docfile  WORD文件全路�?
     * @param htmlfile 转换后HTML存放路径
     */
    public static void wordToHtml(String docfile, String htmlfile) {
        // 启动word应用程序(Microsoft Office Word 2003)
        ActiveXComponent app = null;
        System.out.println("*****正在转换...*****");
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent("Word.Application");
            // 设置word应用程序不可�?
            app.setProperty("Visible", new Variant(false));
            // documents表示word程序的所有文档窗口，（word是多文档应用程序�?
            Dispatch docs = app.getProperty("Documents").toDispatch();
            // 打开要转换的word文件
            Dispatch doc = Dispatch.invoke(
                    docs,
                    "Open",
                    Dispatch.Method,
                    new Object[]{docfile, new Variant(false),
                            new Variant(true)}, new int[1]).toDispatch();
            // 作为html格式保存到临时文�?
            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[]{
                    htmlfile, new Variant(WORD2HTML)}, new int[1]);
            // 关闭word文件


            Dispatch.call(doc, "Close", new Variant(false));
        } catch (Exception e) {
            ComThread.Release();
            e.printStackTrace();
        } finally {
            //关闭word应用程序
            app.invoke("Quit", new Variant[]{});
            ComThread.Release();
        }
        System.out.println("*****转换完毕********");
    }

    /**
     * @param pdffile TODO pdf文件按页转成图片
     * @author hqj:
     * @version 修改时间：2018年06月09日
     */
    /*public static int pdf2Image(String pdffile) {
        File file = new File(pdffile);
        int pages = 0;
        try {
            ComThread.InitSTA();
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            java.nio.ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            PDFFile pdf = new PDFFile(buf);
            pages = pdf.getNumPages();
            System.out.println("页数："+pdf.getNumPages());
            File direct = new File(pdffile.substring(0, pdffile.lastIndexOf(".")));
            if(!direct.exists()){
                direct.mkdir();
            }
            for(int i=1;i<=pdf.getNumPages();i++){
                PDFPage page = pdf.getPage(i);
                Rectangle rect = new Rectangle(0, 0, (int)(page.getBBox().getWidth()), (int)(page.getBBox().getHeight()));
                int width = (int) (rect.getWidth()*2);
                int height = (int) (rect.getHeight()*2);
                Image image = page.getImage(width, height, rect, null, true, true);
                BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                tag.getGraphics().drawImage(image, 0, 0, width, height, null);
                FileOutputStream out = new FileOutputStream(direct+"/幻灯片"+i+".JPG");
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(tag);
                param.setQuality(1f, false);
                encoder.setJPEGEncodeParam(param);
                encoder.encode(tag);
                out.close();
                System.out.println("image in the page -->"+i);
            }
            buf.clear();
            channel.close();
            raf.close();
            unmap(buf);
        } catch (Exception e) {
            ComThread.Release();
            e.printStackTrace();
        } finally {
            ComThread.Release();
        }
        return pages;
    }*/

    /**
     * TODO pdf转成图片时解除映射，以便后面删除文件时能够删除pdf文件
     *
     * @author hqj:
     * @version 修改时间：2018年06月09日
     */
    /*private static <T> void unmap(final Object buffer) {
        AccessController.doPrivileged(new PrivilegedAction<T>() {
            @Override
            public T run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                    getCleanerMethod.setAccessible(true);
                    Cleaner cleaner = (Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
                    cleaner.clean();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }*/
    public static void ppt2pdf(String pptfile, String pdffile) {
        Log.debug("打开ppt应用");
        ActiveXComponent app = null;
        Log.debug("设置可见性");
        //app.setProperty("Visible", new Variant(false));
        Log.debug("打开ppt文件");
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent("PowerPoint.Application");
            Dispatch files = app.getProperty("Presentations").toDispatch();
            Dispatch file = Dispatch.call(files, "open", pptfile, false, true).toDispatch();
            Log.debug("保存为图片");
            Dispatch.call(file, "SaveAs", pdffile, PPT2PDF);
            Log.debug("关闭文档");
            Dispatch.call(file, "Close");
        } catch (Exception e) {
            ComThread.Release();
            e.printStackTrace();
            Log.error("ppt to images error", e);
            //throw e;
        } finally {
            Log.debug("关闭应用");
            app.invoke("Quit");
            ComThread.Release();
        }
    }

    public static void excel2pdf(String excelfile, String pdffile) {
        ActiveXComponent app = null;
        try {
            ComThread.InitSTA(true);
            app = new ActiveXComponent("Excel.Application");
            app.setProperty("Visible", false);
            app.setProperty("AutomationSecurity", new Variant(3));//禁用宏
            Dispatch excels = app.getProperty("Workbooks").toDispatch();
            /*Dispatch excel = Dispatch.invoke(excels, "Open", Dispatch.Method, new Object[]{
                    excelfile,
                    new Variant(false),
                    new Variant(false),
            },new int[9]).toDispatch();*/
            Dispatch excel = Dispatch.call(excels, "Open",
                    excelfile, false, true).toDispatch();
            //转换格式ExportAsFixedFormat
            /*Dispatch.invoke(excel, "ExportAsFixedFormat", Dispatch.Method, new Object[]{
                    new Variant(0),//pdf格式=0
                    pdffile,
                    new Variant(0)//0=标准(生成的pdf图片不会变模糊) 1=最小文件(生成的pdf图片模糊的一塌糊涂)
            }, new int[1]);*/
            Dispatch.call(excel, "ExportAsFixedFormat", XLS2PDF,
                    pdffile);
            Dispatch.call(excel, "Close", false);
            if (app != null) {
                app.invoke("Quit");
                app = null;
            }
        } catch (Exception e) {
            ComThread.Release();
            e.printStackTrace();
        } finally {
            ComThread.Release();
        }
    }

    public static void ppt2html(String pptfile, String htmlfile) {
        ActiveXComponent app = null;
        try {
            ComThread.InitSTA(true);
            app = new ActiveXComponent("PowerPoint.Application");
            //app.setProperty("Visible", false);
            app.setProperty("AutomationSecurity", new Variant(3));//禁用宏
            Dispatch dispatch = app.getProperty("Presentations").toDispatch();
            Dispatch dispatch1 = Dispatch.call(dispatch, "Open",
                    pptfile, false, true).toDispatch();
            Dispatch.call(dispatch1, "SaveAs",
                    htmlfile, new Variant(12));
            Dispatch.call(dispatch1, "Close", false);
            if (app != null) {
                app.invoke("Quit");
                app = null;
            }
        } catch (Exception e) {
            ComThread.Release();
            e.printStackTrace();
        } finally {
            ComThread.Release();
        }
    }
}
