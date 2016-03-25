/**
 * Created by Mike Bibb on 2/24/16.
 */

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import org.opencv.core.Core;
import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.opencv.objdetect.*;
import sun.misc.JavaIOAccess;




// Some code gotten from http://docs.opencv.org/2.4/doc/tutorials/introduction/desktop_java/java_dev_intro.html

public class Bibb_Mike_assignment_3 {

    //Change this number for the number of frames that you are taking in and applying Harrcascades
    private static final int numberOfFrames = 187;

    static String dir2 = "/video_repository_39/testvid.movout15.png";
    static String dir;
    static int leftEye[] = new int[10];
    static int rightEye[] = new int[10];
    static int theNose[] = new int[10];
    static int theMouth[] = new int[10];
    static int theFace[] = new int[10];



    public static void main(String args[]) throws ClassNotFoundException, SQLException {



        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        int argsInput = Integer.parseInt(args[0]);

        int lll = dir2.indexOf("movout");
        String getMovieNumber = dir2.substring(29);
        System.out.println(getMovieNumber);
        getMovieNumber = getMovieNumber.substring(6);
        System.out.println(getMovieNumber);
        getMovieNumber = getMovieNumber.replace(".png", "");
        lll = Integer.parseInt((getMovieNumber));

        Connection c = null;
        Statement stmt = null;

        Class.forName("org.postgresql.Driver");

        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cs161",
                "postgres", "postgres");
        c.setAutoCommit(false);
        System.out.println("Opened database successfully");
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM METADATA;");

        while (rs.next()) {
            int id = rs.getInt("videonumber");
            if (id != argsInput) {
                continue;
            }


            int frames = rs.getInt("numofframes");
            int xWidth = rs.getInt("xwidth");
            int yWidth = rs.getInt("ywidth");
            double fps = rs.getDouble("fps");

            System.out.println("ID = " + id);
            System.out.println("FRAMES = " + frames);
            System.out.println("xWidth = " + xWidth);
            System.out.println("xWidth = " + yWidth);
            System.out.println("FPS = " + fps);
            System.out.println();
        }

        // String video = "/video_repository_39/testvid.movout15.png";
        for(int i = 1; i <= numberOfFrames; i++) {

            dir = String.format("/video_repository_39/testvid.movout%d.png", i);
             getMovieNumber = dir.substring(29);
            System.out.println(getMovieNumber);
            getMovieNumber = getMovieNumber.substring(6);
            System.out.println(getMovieNumber);
            getMovieNumber = getMovieNumber.replace(".png", "");
            lll = Integer.parseInt((getMovieNumber));
            getFace(argsInput, lll);
        }
        rs.close();
        stmt.close();
        c.close();

    }

    private static void getFace(int num, int getMovieNumber)throws ClassNotFoundException, SQLException {

        System.out.println("\nRunning FaceDetector");
        CascadeClassifier faceCascade = new CascadeClassifier(Bibb_Mike_assignment_3.class.getResource("haarcascade_frontalface_alt2.xml").getPath());
        Mat image = Highgui.imread(Bibb_Mike_assignment_3.class.getResource(dir).getPath());
        CascadeClassifier eyesCascade = new CascadeClassifier(Bibb_Mike_assignment_3.class.getResource("parojosG.xml").getPath());
        CascadeClassifier mouthCascade = new CascadeClassifier(Bibb_Mike_assignment_3.class.getResource("Mouth.xml").getPath());
        CascadeClassifier leftEyeCascade = new CascadeClassifier(Bibb_Mike_assignment_3.class.getResource("ojoI.xml").getPath());
        CascadeClassifier rightEyeCascade = new CascadeClassifier(Bibb_Mike_assignment_3.class.getResource("ojoD.xml").getPath());
        CascadeClassifier noseCascade = new CascadeClassifier(Bibb_Mike_assignment_3.class.getResource("Nariz_nuevo_20stages.xml").getPath());



        Mat mRgba = new Mat();
        Mat mGrey = new Mat();
        MatOfRect faces = new MatOfRect();
        MatOfRect mouth = new MatOfRect();
        MatOfRect eyes = new MatOfRect();
        MatOfRect lEye = new MatOfRect();
        MatOfRect rEye = new MatOfRect();
        MatOfRect aNose = new MatOfRect();

        image.copyTo(mRgba);
        image.copyTo(mGrey);
        Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(mGrey, mGrey);
        //faceCascade.detectMultiScale(mGrey, faces);



        faceCascade.detectMultiScale(mGrey, faces,1.1, 0,
                Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                        | Objdetect.CASCADE_SCALE_IMAGE, new org.opencv.core.Size(30, 30), new org.opencv.core.Size());
        System.out.println(String.format("Detected %s face(s)", faces.toArray().length));

        //   faceCascade.detectMultiScale(mGrey, faces, 1.1, 5, 0, new Size(30, 30), new Size());
        Rect[] facesArray = faces.toArray();

        for (int i = 0; i < facesArray.length; i++) {
            Core.rectangle(mRgba, new Point(facesArray[i].x, facesArray[i].y), new Point(facesArray[i].x + facesArray[i].width, facesArray[i].y + facesArray[i].height),
                    new Scalar(0, 255, 0));

            // 0 = X
                /*
                0 = x
                1 = y
                2 = width
                3 = height
                 */

            theFace[0] = facesArray[i].x;
            theFace[1] = facesArray[i].y;
            theFace[2] = facesArray[i].width;
            theFace[3] = facesArray[i].height;



            mouthCascade.detectMultiScale(mGrey, mouth,1.1, 100,
                    Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                            | Objdetect.CASCADE_SCALE_IMAGE, new org.opencv.core.Size(15, 15), new org.opencv.core.Size());
            Rect[] mouthsArray = mouth.toArray();

            leftEyeCascade.detectMultiScale(mGrey, lEye, 1.1, 40, 0, new Size(30, 30), new Size());
            Rect[] lEyeArray = lEye.toArray();

            rightEyeCascade.detectMultiScale(mGrey, rEye, 1.1, 40, 0, new Size(30, 30), new Size());
            Rect[] REyeArray = rEye.toArray();

            noseCascade.detectMultiScale(mGrey, aNose,1.1, 15,
                    Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                            | Objdetect.CASCADE_SCALE_IMAGE, new org.opencv.core.Size(15, 15), new org.opencv.core.Size());
            Rect[] noseArray = aNose.toArray();

            for (int k = 0; k < mouthsArray.length; k++){
                if(k > 0) { break; }
                Core.rectangle(mRgba, new Point(mouthsArray[k].x, mouthsArray[k].y), new Point(mouthsArray[k].x + mouthsArray[k].width, mouthsArray[k].y + mouthsArray[k].height),
                        new Scalar(0, 255, 0));

                theMouth[0] = mouthsArray[i].x;
                theMouth[1] = mouthsArray[i].y;
                theMouth[2] = mouthsArray[i].width;
                theMouth[3] = mouthsArray[i].height;


            }
            for (int k = 0; k < lEyeArray.length; k++){
                if(k > 0) { break; }
                Core.rectangle(mRgba, new Point(lEyeArray[k].x, lEyeArray[k].y), new Point(lEyeArray[k].x + lEyeArray[k].width, lEyeArray[k].y + lEyeArray[k].height),
                        new Scalar(0, 255, 0));

                leftEye[0] = lEyeArray[i].x;
                leftEye[1] = lEyeArray[i].y;
                leftEye[2] = lEyeArray[i].width;
                leftEye[3] = lEyeArray[i].height;


            }
            for (int k = 0; k < REyeArray.length; k++){
                if(k > 0) { break; }
                Core.rectangle(mRgba, new Point(REyeArray[k].x, REyeArray[k].y), new Point(REyeArray[k].x + REyeArray[k].width, REyeArray[k].y + REyeArray[k].height),
                        new Scalar(0, 255, 0));
//
                rightEye[0] = REyeArray[i].x;
                rightEye[1] = REyeArray[i].y;
                rightEye[2] = REyeArray[i].width;
                rightEye[3] = REyeArray[i].height;


            }
//
            for (int k = 0; k < noseArray.length; k++){
                if(k > 0) { break; }
                Core.rectangle(mRgba, new Point(noseArray[k].x, noseArray[k].y), new Point(noseArray[k].x + noseArray[k].width, noseArray[k].y + noseArray[k].height),
                        new Scalar(0, 255, 0));

                theNose[0] = noseArray[i].x;
                theNose[1] = noseArray[i].y;
                theNose[2] = noseArray[i].width;
                theNose[3] = noseArray[i].height;

            }


        }


        String sql = "INSERT INTO BOUNDINGBOX (videonumber,frame,widthface,heightface,wholeface,lefteye,heightlefteye,widthlefteye,righteye,heightrighteye," +
                "widthrighteye,nose,widthnose,heightnose,mouth,widthmouth,heightmouth) "
                + "VALUES ("+ num + "," + getMovieNumber + "," + facesArray[0].width + "," + facesArray[0].height + "," + "point(" + facesArray[0].x + "," + facesArray[0].y+ ")"    + "," + "point(" + leftEye[0] + "," + leftEye[1] + ")" + "," + leftEye[3] + "," +leftEye[2] + "," +"point(" + rightEye[0] + "," + rightEye[1] + ")" + "," + rightEye[3] + "," + rightEye[2] + "," + "point(" +  theNose[0] + "," +  theNose[1] + ")" + "," +  theNose[2] + "," +  theNose[3] + "," + "point(" + theMouth[0] + "," + theMouth[1] + ")" + "," + theMouth[2] + "," + theMouth[3] + ");";


        Connection c = null;
        Statement stmt = null;

        Class.forName("org.postgresql.Driver");

        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cs161",
                "postgres", "postgres");
        c.setAutoCommit(false);
        System.out.println("Opened database successfully");
        stmt = c.createStatement();
        stmt.executeUpdate(sql);

        String filename = "ouput.png";
        System.out.println(String.format("Writing %s", filename));
        Highgui.imwrite(filename, mRgba);

        c.commit();
        stmt.close();
        c.close();
    }
}


