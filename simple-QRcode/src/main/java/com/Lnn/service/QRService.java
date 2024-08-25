package com.Lnn.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.decoder.ec.ErrorCorrection;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRService {

    /**
     * @param content 存储内容
     * @param filePath 存储在本地的位置
     */
    public void generateQRCode(String content, int width, int height, String filePath) throws IOException, WriterException {

        Map<EncodeHintType,Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");//设置字符集
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//设置二维码对误读的校正能力,从低到高L到H
        hints.put(EncodeHintType.MARGIN,2);//二维码的白边宽度

        BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(content, BarcodeFormat.QR_CODE, width,height,hints);

        Path path = FileSystems.getDefault().getPath(filePath); //这里为了简单，直接放在项目的根目录下
        MatrixToImageWriter.writeToPath(bitMatrix,"PNG",path);

    }

}
