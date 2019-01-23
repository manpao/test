package com.huojing.controllers;


import com.huojing.tool.Byte2obj;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


@Controller
public class Command  extends Default
{
    //命令执行
    @RequestMapping("/runtime")
    public ModelAndView execs(HttpServletRequest request)
    {
        String s;
        String a="";
        Process p;
        String cmd=request.getParameter("cmd");
        try {
            p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
            {
                a+=s+"<br>";
            }
            p.waitFor();
            p.destroy();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }

        Map<String,Object> params=new HashMap<>();
        params.put("exec",a);
        ModelAndView index =new ModelAndView("/process/exec");
        index.addAllObjects(params);
        return index;
    }

    //命令执行
    @RequestMapping("/processbuilder")
    public ModelAndView pd(HttpServletRequest request)
    {
        String s;
        String a="";
        Process p;
        String cmd=request.getParameter("cmd");
        try {
            p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
            {
                a+=s+"<br>";
            }
            p.waitFor();
            p.destroy();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }

        Map<String,Object> params=new HashMap<>();
        params.put("exec",a);
        ModelAndView index =new ModelAndView("/process/exec");
        index.addAllObjects(params);
        return index;
    }

    //反序列化命令执行
    @RequestMapping("/unserializable")
    public  void aa(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String str=request.getParameter("seria");
        System.out.println(str);
        Byte2obj.toObject(Base64.decode(str));
    }

    //SSRF
    @RequestMapping("/ssrf")
    public ModelAndView ssrfURLConnection(HttpServletRequest request)
    {
        String s;
        String a="";

        try
        {
            InputStream inputStream = null;
            String uri=request.getParameter("uri");
            URL url = new URL(uri); //
            URLConnection urlConnection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            while ((s = in.readLine()) != null) {
                a+=s+"<br>";
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }

        Map<String,Object> params=new HashMap<>();
        params.put("exec",a);
        ModelAndView index =new ModelAndView("/process/exec");
        index.addAllObjects(params);
        return index;
    }


    @RequestMapping("/upload")
    public ModelAndView userreg(){
        ModelAndView reg=new ModelAndView("/command/upload");
        return reg;
    }
    //xxe
    @RequestMapping("/xxe")
    public ModelAndView xmlupload(MultipartFile file)
    {
        String a="";
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();

        if (!file.isEmpty())
        {
            try
            {
                DocumentBuilder domBuilder = domfac.newDocumentBuilder();
                Document doc = (Document) domBuilder.parse(file.getInputStream());
                Element root = doc.getDocumentElement();
                NodeList books = root.getChildNodes();
                if (books != null)
                {
                    for (int i = 0; i < books.getLength(); i++)
                    {
                        Node book = books.item(i);
                        if (book.getNodeType() == Node.ELEMENT_NODE)
                        {
                            for (Node node = book.getFirstChild(); node != null; node = node.getNextSibling())
                            {
                                if (node.getNodeType() == Node.ELEMENT_NODE)
                                {
                                    if (node.getNodeName().equals("title"))
                                    {
                                        String k = node.getNodeValue();
                                        String s = node.getFirstChild().getNodeValue();
                                        a+=k+"</br>";
                                        a+=s+"</br>";
                                    }
                                    if (node.getNodeName().equals("doc"))
                                    {
                                        String price = node.getFirstChild().getNodeValue();
                                        a+=price+"</br>";
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Map<String,Object> params=new HashMap<>();
        params.put("exec",a);
        ModelAndView index =new ModelAndView("/command/xxe");
        index.addAllObjects(params);
        return index;
    }
}
