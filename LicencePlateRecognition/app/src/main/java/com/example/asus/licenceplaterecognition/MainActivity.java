package com.example.asus.licenceplaterecognition;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static org.opencv.core.Core.BORDER_DEFAULT;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.getAffineTransform;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase mOpenCvCameraView;

    Mat image;
    Bitmap imagebmp;
    Mat mRgba;

    static Mat outputMat;
    String OCR_Result = "";


    private TessBaseAPI mTess;
    String datapath = "";


    List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();


    Point pt0 = null,
            pt1 = null,
            pt2 = null,
            pt3 = null,
            ctr;

int ponit=0;
    static double minx;
    static double maxx;

    static double miny;
    static double maxy;


  //   private TextRecognizer detector; ///Google OCR


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    // mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
        mOpenCvCameraView.setMaxFrameSize(1920, 1080);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


        //initialize Tesseract API------------------------------------------------------------------
        String language = "eng";
        datapath = Environment.getExternalStorageDirectory()
                + "/"
                + getApplicationContext().getPackageName()
                + "/";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));

        mTess.init(datapath, language);

        //------------------------------------------------------------------------------------------


        //  detector = new TextRecognizer.Builder(this).build();///Google OCR--------------

    }

    @Override
    public void onCameraViewStarted(int width, int height) {


        mRgba = new Mat(height, width, CvType.CV_8UC4);

    }

    @Override
    public void onCameraViewStopped() {

        mRgba.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


        image = inputFrame.rgba();
        Mat image2=image;

        Log.i("Yusuf","Size-->"+image.size());
        //image2=new Mat(image.size(),image.type(),new Scalar(0,0,0));
       // Imgproc.resize(src, dst, Size(), 0.5, 0.5, interpolation);



        Mat gray = new Mat();
        gray = inputFrame.gray();


        Size sSize5 = new Size(1, 1);


        Imgproc.GaussianBlur(gray, gray, sSize5, 2, 2);

        int iCannyLowerThreshold = 50;
        int iCannyUpperThreshold = 150;

        Imgproc.Canny(gray, gray, iCannyLowerThreshold, iCannyUpperThreshold);
        Imgproc.dilate(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(1, 1)));

findSquares(gray);


       if (!squares.isEmpty()) {


            Bitmap cropped_bmp = Crop_Image(image);

            storeImage(cropped_bmp);

            OCR_Result = OCR_Read(cropped_bmp);
            //  OCR_Result=Google_ocr(cropped_bmp);
ponit=0;
           OCR_Result=OCR_Result.replace("31.","34");
           OCR_Result=OCR_Result.replace("3h","34");

           OCR_Result=OCR_Result.replace("134","34");
           OCR_Result=OCR_Result.replace("135","34");

           OCR_Result=OCR_Result.replace("534","34");
           OCR_Result=OCR_Result.replace("535","34");

           OCR_Result=OCR_Result.replace("E34","34");
           OCR_Result=OCR_Result.replace("E35","34");

           OCR_Result=OCR_Result.replace("3k","34");
           OCR_Result=OCR_Result.replace("34:","34");

           OCR_Result=OCR_Result.replace("3L","34");
           OCR_Result=OCR_Result.replace("31:","34");


           OCR_Result=OCR_Result.replace("[","");
           OCR_Result=OCR_Result.replace(".","");
           OCR_Result=OCR_Result.replace("?","");



            Log.i("Yusuf", "OCR_Result"+OCR_Result);

           /* Imgproc.putText(image2, "p0(" + pt0.x + "," + pt0.y + ")", pt0, Core.FONT_ITALIC, 0.70, new Scalar(255), 2);
            Imgproc.putText(image2, "p1(" + pt1.x + "," + pt1.y + ")", pt1, Core.FONT_ITALIC, 0.70, new Scalar(255), 3);
            Imgproc.putText(image2, "p2(" + pt2.x + "," + pt2.y + ")", pt2, Core.FONT_ITALIC, 0.70, new Scalar(255), 3);
            Imgproc.putText(image2, "p3(" + pt3.x + "," + pt3.y + ")", pt3, Core.FONT_ITALIC, 0.70, new Scalar(255), 3);
            Imgproc.putText(image2, "M( " + ctr.x + "," + ctr.y + ")", ctr, Core.FONT_ITALIC, 0.70, new Scalar(255), 3);
*/


            // storeImage(bmp2);*//*



           Point pt0_ = new Point(squares.get(0).toArray()[0].x-10,squares.get(0).toArray()[0].y-10);
           Point pt1_ = new Point(squares.get(0).toArray()[1].x-10,squares.get(0).toArray()[1].y);
           Point pt2_ = new Point(squares.get(0).toArray()[2].x-10,squares.get(0).toArray()[2].y);
           Point pt3_ = new Point(squares.get(0).toArray()[3].x-10,squares.get(0).toArray()[3].y);



            Imgproc.line( image2, pt0_,pt1_, new Scalar(0,0,255), 1);
            Imgproc.line( image2, pt1_,pt2_, new Scalar(0,0,255), 1);
            Imgproc.line( image2, pt2_,pt3_, new Scalar(0,0,255), 1);
            Imgproc.line( image2, pt3_,pt0_, new Scalar(0,0,255), 1);



           // Imgproc.drawContours(image2, squares, -1, new Scalar(255, 0, 255), 8);




        }



        if (ponit<5)
        Imgproc.putText(image2, OCR_Result, new Point(minx, maxy + 40), Core.FONT_ITALIC, 1.3, new Scalar(255), 5);
ponit++;

        return image2;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }


    ///---------------------------------------------------------------------------------------------
    void findSquares(Mat image) {
        Mat gray = new Mat();

        gray = image;


        squares.clear();
        contours.clear();

        Boolean bool = true;

        Mat hierarchy = new Mat();

        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);


        MatOfPoint approx = new MatOfPoint();


        for (int i = 0; i < contours.size(); i++) {


            approx = approxPolyDP(contours.get(i), Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true) * 0.012, true);


            if (approx.toArray().length == 4 &&
                    Math.abs(Imgproc.contourArea(approx)) > 1000 &&
                    Imgproc.isContourConvex(approx)) {


                double maxCosine = 0;

                for (int j = 2; j < 5; j++) {
                    // find the maximum cosine of the angle between joint edges
                    double cosine = Math.abs(angle(approx.toArray()[j % 4], approx.toArray()[j - 2], approx.toArray()[j - 1]));
                    maxCosine = Math.max(maxCosine, cosine);
                }

                if (maxCosine < 0.35 && bool) {
                    squares.add(approx);
                       bool = false;

                }
            }
        }

    }


    double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        //return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
        double edge1 = Math.sqrt(Math.pow(dx1, 2) + Math.pow(dy1, 2));
        double edge2 = Math.sqrt(Math.pow(dx2, 2) + Math.pow(dy2, 2));
        double oran = edge2 / edge1;
        if ((oran > 0.10 && oran < 0.25) || (oran > 4 && oran < 10)) {
            // Log.i("Oran tmm","---->"+oran);
            return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);


        } else
            return 1;
    }


    MatOfPoint approxPolyDP(MatOfPoint curve, double epsilon, boolean closed) {
        MatOfPoint2f tempMat = new MatOfPoint2f();

        Imgproc.approxPolyDP(new MatOfPoint2f(curve.toArray()), tempMat, epsilon, closed);

        return new MatOfPoint(tempMat.toArray());
    }


    //initialize Tesseract API----------------------------------------------------------------------
    private void checkFile(File dir) {
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        if (dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }


            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------------------------------------


    ///--------Perspective crop-------------------------------------------------------------------------

    Bitmap Crop_Image(Mat uncropped) {
     //  Bitmap Crop_Image(Mat uncropped, Point[] rect) {


        Point pt0_temp = squares.get(0).toArray()[0];
        Point pt1_temp = squares.get(0).toArray()[1];
        Point pt2_temp = squares.get(0).toArray()[2];
        Point pt3_temp = squares.get(0).toArray()[3];


        pt0 = null;
        pt1 = null;
        pt2 = null;
        pt3 = null;

      /*  Point pt0_temp = rect[0];
        Point pt1_temp = rect[1];
        Point pt2_temp = rect[2];
        Point pt3_temp = rect[3];*/
        Point temp = null;


        double minx = Min(pt0_temp.x, pt1_temp.x, pt2_temp.x, pt3_temp.x);
        double maxx = Max(pt0_temp.x, pt1_temp.x, pt2_temp.x, pt3_temp.x);

        double miny = Min(pt0_temp.y, pt1_temp.y, pt2_temp.y, pt3_temp.y);
        double maxy = Max(pt0_temp.y, pt1_temp.y, pt2_temp.y, pt3_temp.y);

        double center_x = (maxx + minx) / 2;
        double center_y = (maxy + miny) / 2;

        ctr = new Point(center_x, center_y);

        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    temp = pt0_temp;
                    break;
                case 1:
                    temp = pt1_temp;
                    break;
                case 2:
                    temp = pt2_temp;
                    break;
                case 3:
                    temp = pt3_temp;
                    break;
            }


            if (temp.x < center_x) {

                if (pt0 != null && temp.y < pt0.y) {
                    pt1 = pt0;
                    pt0 = temp;
                } else if (pt0 != null && temp.y > pt0.y) {
                    pt1 = temp;
                } else {
                    pt0 = temp;

                }


            } else {
                if (pt3 != null && temp.y < pt3.y) {
                    pt2 = pt3;
                    pt3 = temp;
                } else if (pt3 != null && temp.y > pt3.y) {
                    pt2 = temp;
                } else {
                    pt3 = temp;
                }
            }
        }


        Log.i("Yusuf", "koordinat_temp --->" +  " pt0_temp=" + (int)pt0_temp.x + "," + (int)pt0_temp.y + "\n" +
                                                " pt1_temp=" + (int)pt1_temp.x + "," + (int)pt1_temp.y + "\n" +
                                                " pt2_temp=" + (int)pt2_temp.x + "," + (int)pt2_temp.y + "\n" +
                                                " pt3_temp=" + (int)pt3_temp.x + "," + (int)pt3_temp.y);


        Log.i("Yusuf", "koordinat --->" +   " pt0=" + (int)pt0.x + "," + (int)pt0.y + "\n" +
                                            " pt1=" + (int)pt1.x + "," + (int)pt1.y + "\n" +
                                            " pt2=" + (int)pt2.x + "," + (int)pt2.y + "\n" +
                                            " pt3=" + (int)pt3.x + "," + (int)pt3.y);




       /* Bitmap bmp2 = Bitmap.createBitmap(uncropped.cols(), uncropped.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(uncropped, bmp2);*/

        Bitmap pers = warp(uncropped,   new Point(pt0.x, pt0.y),
                                        new Point(pt1.x, pt1.y),
                                        new Point(pt2.x, pt2.y),
                                        new Point(pt3.x, pt3.y));


        return pers;
    }


    public static Bitmap warp(Mat inputMat, Point p1, Point p2, Point p3, Point p4) {
        //  int resultWidth = 500;
        //  int resultHeight = 500;

        minx = Min(p1.x, p2.x, p3.x, p4.x);
        maxx = Max(p1.x, p2.x, p3.x, p4.x);

        miny = Min(p1.y, p2.y, p3.y, p4.y);
        maxy = Max(p1.y, p2.y, p3.y, p4.y);

        double resultWidth = (maxx - minx);
        double resultHeight = (maxy - miny);

       /* Mat inputMat = new Mat(image.getHeight(), image.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(image, inputMat);*/
        outputMat = new Mat((int) resultWidth, (int) resultHeight, CvType.CV_8UC4);

        Point ocvPIn1 = new Point(p1.x, p1.y);
        Point ocvPIn2 = new Point(p2.x, p2.y);
        Point ocvPIn3 = new Point(p3.x, p3.y);
        Point ocvPIn4 = new Point(p4.x, p4.y);
        List<Point> source = new ArrayList<Point>();
        source.add(ocvPIn1);
        source.add(ocvPIn2);
        source.add(ocvPIn3);
        source.add(ocvPIn4);
        Mat startM = Converters.vector_Point2f_to_Mat(source);

        Point ocvPOut1 = new Point(0, 0);
        Point ocvPOut2 = new Point(0, resultHeight);
        Point ocvPOut3 = new Point(resultWidth, resultHeight);
        Point ocvPOut4 = new Point(resultWidth, 0);
        List<Point> dest = new ArrayList<Point>();
        dest.add(ocvPOut1);
        dest.add(ocvPOut2);
        dest.add(ocvPOut3);
        dest.add(ocvPOut4);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat,
                outputMat,
                perspectiveTransform,
                new Size(resultWidth, resultHeight),
                Imgproc.INTER_CUBIC);

        Imgproc.cvtColor(outputMat, outputMat, Imgproc.COLOR_BGR2GRAY);



        Imgproc.threshold(outputMat, outputMat, 0, 255, THRESH_BINARY + Imgproc.THRESH_OTSU);




        Bitmap output = Bitmap.createBitmap((int) resultWidth, (int) resultHeight, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat, output);
        return output;
    }

    //----------------------------------------------------------------------------------------------


    ///----------------4 değer içinde Min Max bulunması-----------------------------------------------
    static double Max(double pnt0, double pnt1, double pnt2, double pnt3) {
        double tmp1 = Math.max(pnt0, pnt1);
        double tmp2 = Math.max(pnt2, pnt3);

        return Math.max(tmp1, tmp2);
    }


    static double Min(double pnt0, double pnt1, double pnt2, double pnt3) {
        double tmp1 = Math.min(pnt0, pnt1);
        double tmp2 = Math.min(pnt2, pnt3);

        return Math.min(tmp1, tmp2);

    }

    //-----------------------------------------------------------------------------------------------


///Tesseract ocr------------------------------------------------------------------------------------

    String OCR_Read(Bitmap bmp) {

        try {


            String OCRresult;
            mTess.setImage(bmp);
            OCRresult = mTess.getUTF8Text();


            Log.i("Yusuf", "BMP TESSERACT" + OCRresult);
            Log.i("Yusuf", "BMP ceviri" + " Tamamlandı");


            return OCRresult;

        } catch (CvException e) {
            Log.d("Mat to Bitmap Error", e.getMessage());

            return "Tess-two error";
        }


    }
///---------------------------------------------------------------------------------------------------


////---------Bitmap kayıp işlemi------------------------------------------------------------------------

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Log.i("save image", "succesfuly");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }


    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    ///---------------------------------------------------------------------------------------------------


    //// Google vision ocr--------------------------------------------------------------------------

   /* String Google_ocr(Bitmap bitmap) {

        try {


            if (detector.isOperational() && null != bitmap) {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> textBlocks = detector.detect(frame);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < textBlocks.size(); ++i) {
                    TextBlock tb = textBlocks.get(i);
                    sb.append(tb.getValue());
                }
                if (textBlocks.size() == 0) {


                    Log.i("google ocr", "Scan failed");
                    return "Scan failed";
                } else {


                    Log.i("google ocr", " " + sb.toString());
                    return sb.toString();
                }
            } else {

                Log.i("google ocr", "Invalid image");
                return "Invalid image";
            }
        } catch (Exception e) {

            Log.i("google ocr", "Problem encoured");
            return "Problem encoured";
        }


    }*/

}



/*
*Google ocr gözlükte play services olmadığı için çalışmıyor. yorum satırları açılırsa yüklenen uygulama açılmıyor.

* ocr için özel kütüphaneler çok verimli çalışmıyor. Bunun için sadece plaka karakterlerinin tanıtılması ile oluşturulan
 özel bir kütüphane yazılırsa daha verimli çalışır. örneğin plaka üzerinde bir leke varsa veya plaka vida ile tutturulmuşsa
 hazır kütüphaneler bunu .(nokta) olarak algılıyor. Ve bunun gibi bir çok hata yapabiliyor.

 * plaka tespitinin daha verimli olması için gözlük çözünürlüğünün daha da yükseltilmesi gerekiyor.Ben full hd
         mOpenCvCameraView.setMaxFrameSize(1920, 1080); ayarlamama rağmen o kendisini 640*480 olarak açıyor. Sitesinde
         yazan teknik özelliklere göre benim dediğim çözünürlüğü destekliyor.

 */
