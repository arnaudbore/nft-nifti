package com.scilus.nf.test.nifti;

import com.ericbarnhill.niftijio.*;
import com.ericbarnhill.niftijio.tools.*;
import java.io.IOException;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class Methods
{
    public static String Nifti_md5sum(String i_file) throws IOException
    {

        NiftiVolume v = NiftiVolume.read(i_file);
        Nifti1Header h = v.getHeader1();
        NDimensionalArray d = v.getData();

        ArrayList<int[]> indcs = new IndexIterator().iterateReverse(d.getDims());

        StringBuilder md5 = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (int[] indc: indcs) {
                md.update(String.valueOf(d.get(indc)).getBytes("UTF-8"));
            }

            // Replace filename with basename and remove descrip
            h.filename = h.filename.replace("\\", "/");
            int index = h.filename.lastIndexOf("/");
            h.filename = h.filename.substring(index + 1);
            h.descrip = new StringBuffer("");
        
            md.update(h.toString().replace("\0","").getBytes("UTF-8"));
            byte[] theMD5digest = md.digest();

            for (int i = 0; i < theMD5digest.length; i++) {
                String hex = Integer.toHexString(0xFF & theMD5digest[i]);
                if (hex.length() == 1) {
                    md5.append('0');
                }
                md5.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e) {
            System.err.println("I'm sorry, but MD5 is not a valid message digest algorithm");
        }       	

        return h.filename + ":md5," + md5.toString();
    }
}