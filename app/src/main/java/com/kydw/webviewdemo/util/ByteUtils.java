package com.kydw.webviewdemo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <author>SimonLay</author>
 * <date>2016/11/21</date>
 * <time>15:19</time>
 */

public class ByteUtils {

    // 转换需要用的常量
    private final static String HEX = "0123456789ABCDEF";

    /**
     * HEXString 转 HEXbyte[]
     * HEXString每一个char代表半个字节
     */
    public static byte[] hexString2byteArray(String hexString) {
        char[] hexCharArray = hexString.toCharArray();
        byte[] byteArray = new byte[(int) Math.ceil((double) hexString.length() / 2)];
        int[] temp = new int[2];
        for (int i = 0; i < hexString.length(); i += 2) {
            for (int j = 0; j < 2; j++) {
                if (hexCharArray[i + j] >= '0' && hexCharArray[i + j] <= '9') {
                    temp[j] = (hexCharArray[i + j] - '0');
                } else if (hexCharArray[i + j] >= 'A' && hexCharArray[i + j] <= 'F') {
                    temp[j] = (hexCharArray[i + j] - 'A' + 10);
                } else if (hexCharArray[i + j] >= 'a' && hexCharArray[i + j] <= 'f') {
                    temp[j] = (hexCharArray[i + j] - 'a' + 10);
                }
            }
            temp[0] = (temp[0] & 0xff) << 4;
            temp[1] = (temp[1] & 0xff);
            byteArray[i / 2] = (byte) (temp[0] | temp[1]);
        }
        return byteArray;
    }

    // 合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        if (byte_2 != null) {
            byte[] byte_3 = new byte[byte_1.length + byte_2.length];
            System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
            System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
            return byte_3;
        }
        return byte_1;
    }

    public static String byteArray2HexString(byte[] byteArray) {
        if (byteArray == null)
            return "";
        StringBuffer result = new StringBuffer(2 * byteArray.length);
        for (byte aByteArray : byteArray) {
            appendHex(result, aByteArray);
        }
        return result.toString();
    }

    public static String byteArray2HexStringWithMargin(byte[] byteArray) {
        String content=ByteUtils.byteArray2HexString(byteArray);
        StringBuilder sb=new  StringBuilder();
        for(int i=0;i<content.length();i++){
            sb.append(content.charAt(i));
            if((i+1)%2==0){
                sb.append("  ");
            }
            if((i+1)%20==0){
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    public static String byte2HexString(byte aByte) {
        StringBuffer result = new StringBuffer(2);
        appendHex(result, aByte);
        return result.toString();
    }

    public static List<Byte> byteListAddBytes(List<Byte> byteList, byte[] bytes) {
        for (byte b : bytes) {
            byteList.add(b);
        }
        return byteList;
    }

    public static String byteList2HexString(List<Byte> byteList) {
        if (byteList != null) {
            byte[] bytes = byteList2ByteArray(byteList);
            return byteArray2HexString(bytes);
        }
        return "  ";
    }


    public static byte[] byteList2ByteArray(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        return bytes;
    }



    /**
     * 将byte[]中的部分转换成字符串
     *
     * @param source     源数组
     * @param startIndex 起始位置
     * @param length     String长度
     * @return 转换的部分字符串
     */
    public static String byteArray2String(byte[] source, int startIndex, int length) {
        byte[] resultArray = new byte[length];
        System.arraycopy(source, startIndex, resultArray, 0, length);
        return new String(resultArray);
    }

    public static byte[] int2ByteArraySmall(int aInt) {
        byte[] b = new byte[4];
        b[0] = (byte) (aInt & 0xff);
        b[1] = (byte) (aInt >> 8 & 0xff);
        b[2] = (byte) (aInt >> 16 & 0xff);
        b[3] = (byte) (aInt >> 24 & 0xff);
        return b;
    }

    public static int byteArraySmall2Int(byte[] b) {
        int a = b[1] & 0xff;
        int c = (b[0] & 0xff) << 8;
        return a + c;
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    // 将接收到的数据更易读
    public static String addSpace(String hex) {
        int length = hex.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i += 2) {
            // 长度为单数的情况
            if (i == length - 1) {
                builder.append(hex.substring(i, i + 1));
            } else {
                builder.append(hex.substring(i, i + 2));
                builder.append(" ");
            }
        }
        return builder.toString().trim();
    }

    /**
     * 获得指定文件的byte数组
     */
    private byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static List<Byte> byteArray2ByteList(byte[] bytes) {
        List<Byte> list = new ArrayList<>();
        for (byte b : bytes) {
            list.add(b);
        }
        return list;
    }

    public static List<Byte> hexString2ByteList(String hex) {
        return byteArray2ByteList(hexString2byteArray(hex));
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    public static String toHex(List<Byte> buf) {
        return byteList2HexString(buf);
    }

    public static String calculateCC(byte[] data) {
        byte cc = 0x00;
        for (byte b : data) {
            cc = (byte) (cc + b);
        }
        return byte2HexString(cc);
    }

}
