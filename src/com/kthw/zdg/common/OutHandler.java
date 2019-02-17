package com.kthw.zdg.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.kthw.zdg.ballcamera.KeepAliveHandler;



/**
 * FFmpeg �������̵Ĺ����߳�
 * 
 * @author zsx
 * @version 2018��12��16��
 */
public class OutHandler extends Thread  
{  
//    static Logger logger = Logger.getLogger(OutHandler.class);
    // �����߳�״̬  
    volatile boolean desstatus = true;  
    /**�ֽ���*/
    BufferedReader br = null;  
    /**��ȡ����Ϣ���ͣ����� error ��*/
    String type = null;  
    /**��������Ϣ*/
    String cmdId = null;
    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCmdId() {
        return cmdId;
    }

    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
    }

    /**����ID   ��ʽ��zdg_robotId 
     * ��������󣬸���id ������������
     *   */
    private String id = null;
  
    public OutHandler(InputStream is, String id, String cmdId,String type)  
    {  
        try {
            br = new BufferedReader(new InputStreamReader(is,"GB2312"));            //���ñ����ʽ�����������������
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        this.id=id;
        this.type = type;  
        this.cmdId = cmdId;
    }  
    
    /**
     * ��������̣߳�Ĭ�����������̣߳�
     * @return
     */
    public static OutHandler create(InputStream is, String id, String cmdId,String type) {
        return create(is, id, cmdId,type,true);
    }
    


    /**
     * ��������߳�
     * @param start-�Ƿ����������߳�
     * @return
     */
    public static OutHandler create(InputStream is, String id, String cmdId,String type,boolean start) {
        OutHandler out= new OutHandler(is, id, cmdId,type);
        if (start){
          out.start();
        }
        return out;
    }
  
    /** 
     * ��д�߳����ٷ�������ȫ�Ĺر��߳� 
     */  
    @Override  
    public void destroy()  
    {  
        desstatus = false;  
        cmdId=null;
        type=null;
        id=null;                      
        try {
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }  
  
    /** 
     * ִ������߳� 
     */  
    @Override  
    public void run()  
    {  
        String msg = null;  
        int errorIndex = 1;  
        int missIndex = 1;  
        int failIndex=1;
        boolean isBroken=false;

        try {  
            while (desstatus&&( msg = br.readLine())!=null ) { 
                System.out.println(id + "��Ϣ��" + msg);
                    if (msg.indexOf("fail") != -1) {
                        System.err.println(id + "������ܷ������ϣ�" + msg);
                        failIndex++;
                    }else if(msg.indexOf("miss")!= -1) {
//                    
                        missIndex++;
                    }else if(msg.indexOf("Unknown error")!= -1) {
                        System.err.println(id + "��Ϣ��" + msg);
                        isBroken=true;
                       
                    }
                    if (0==missIndex%10||0==errorIndex%5||0==failIndex%3){
                        isBroken=true;
                    }
                    
                    if (isBroken){
                       
                        KeepAliveHandler.add(id);    //   ���뱣������
                        isBroken=false;
                    }   
                }           
        
        } catch (IOException e) {  
            System.out.println("�����ڲ��쳣�����Զ��ر�[" + this.getId() + "]�߳�");  
            destroy();  
        } finally {  
            if (this.isAlive()) {  
                destroy();  
            }  
        }  
    }  
  
}  