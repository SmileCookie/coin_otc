package com.world.util.qrcode;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.log4j.Logger;


public class QRCodeGenerator {

	private final static Logger log = Logger.getLogger(QRCodeGenerator.class);

	public QRCodeGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	// 编码
	public static void encode(OutputStream stream, String content, int width, int height) {
		try {
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		    hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 文字编码。
		    hints.put(EncodeHintType.MARGIN, 1); 
		    
			BitMatrix byteMatrix = new MultiFormatWriter().encode(content ,BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToStream(byteMatrix, "png", stream);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}	
	

	// 编码
	public static BufferedImage encode(String content, int width, int height) {
		try {
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		    hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 文字编码。
		    hints.put(EncodeHintType.MARGIN, 0); 
		    
			BitMatrix byteMatrix = new MultiFormatWriter().encode(content ,BarcodeFormat.QR_CODE, width, height, hints);
			return MatrixToImageWriter.getBufferedImage(byteMatrix);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return null;
	}

//	// 解码
//	public void decode() {
//		try {
//			Reader reader = new MultiFormatReader();
//			String imgPath = "f:\\bg.png";
//			File file = new File(imgPath);
//			BufferedImage image;
//			try {
//				image = ImageIO.read(file);
//				if (image == null) {
//					log.info("Could not decode image");
//				}
//				LuminanceSource source = new BufferedImageLuminanceSource(image);
//				BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
//						source));
//				Result result;
//				Hashtable hints = new Hashtable();
//				hints.put(DecodeHintType.CHARACTER_SET, "GBK");
//				result = new MultiFormatReader().decode(bitmap, hints);
//				String resultStr = result.getText();
//				log.info(resultStr);
//
//			} catch (IOException ioe) {
//				log.info(ioe.toString());
//			} catch (ReaderException re) {
//				log.info(re.toString());
//			}
//
//		} catch (Exception ex) {
//
//		}
//	}
}

