package oyz.com.eosapi.util;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import oyz.com.eosapi.model.util.GsonEosTypeAdapterFactory;


/**
 * Created by swapnibble on 2017-10-18.
 */


public class Utils {

    public static byte[] getFileContentFromUri(ContentResolver cr, Uri uri ) {
        if ( null == uri ) {
            return null;
        }

        ParcelFileDescriptor parcelFD = null;
        FileInputStream fi	= null;

        try {
            parcelFD = cr.openFileDescriptor(uri, "r");
            if ( null == parcelFD ) {
                return null;
            }

            long size = parcelFD.getStatSize();
            if ( size <= 0 ){
                return null;
            }

            fi = new FileInputStream( parcelFD.getFileDescriptor() );

            byte[] data = new byte[ (int)size ];
            fi.read(data);

            return data;

        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        finally {
            closeSilently( parcelFD ); // parcel 쪽을 먼저 close 해야 함.
            closeSilently( fi );
        }
    }

    public static String readFile(Context context, String fileName) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            //e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) isr.close();
                if (fIn != null) fIn.close();
                if (input != null) input.close();
            } catch (Exception e2) {
                //e2.getMessage();
                e2.printStackTrace();
            }
        }
        return returnString.toString();
    }


    public static void closeSilently( Closeable c ) {
        if ( null != c ) {
            try {
                c.close();
            } catch (Throwable t) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
        }
    }

    public static long parseLongSafely(String content, int defaultValue) {
        if ( null == content) return defaultValue;

        try {
            return Long.parseLong(content);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int parseIntSafely(String content, int defaultValue) {
        if ( null == content) return defaultValue;

        try {
            return Integer.parseInt(content);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public static String prettyPrintJson(Object object) {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new GsonEosTypeAdapterFactory())
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting().create().toJson( object );
    }
}
