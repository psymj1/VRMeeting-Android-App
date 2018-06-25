package vrmeeting.hexcore.audiotransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by marcu on 21/06/2018.
 */

public class AudioByteArrayTransformer {

    public static int getNumber()
    {
        return 3;
    }

    public static byte[] compress(byte[] originalData)
    {
        System.out.println("Compress inner message");
        byte[] buffer = new byte[8192]; //8kb buffer
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(originalData);
        deflater.finish();
        while(!deflater.finished())
        {
            int outputCount = deflater.deflate(buffer);
            outputStream.write(buffer,0,outputCount);
        }
        deflater.end();

        return outputStream.toByteArray();
    }

    public static byte[] decompress(byte[] compressedData)
    {
        System.out.println("Decompress inner message");
        try
        {
            byte[] buffer = new byte[8192];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);

            while(!inflater.finished())
            {
                int outputCount = inflater.inflate(buffer);
                outputStream.write(buffer,0,outputCount);
            }

            inflater.end();
            outputStream.close();
            return outputStream.toByteArray();
        }catch(UnsupportedOperationException | DataFormatException u) {
            u.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
