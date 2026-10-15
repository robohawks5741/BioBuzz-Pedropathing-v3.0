package org.firstinspires.ftc.teamcode.pedro.testOpModes;

public class DistanceEstimator {

    static final double HFOV_DEG = 54.5;

    /** Focal length in pixels from horizontal FOV and image width. */
    static double focalPx(int imageWidth, double hfovDeg) {
        return (imageWidth / 2.0) / Math.tan(Math.toRadians(hfovDeg) / 2.0);
    }

    /** Depth along the optical axis, in the same units as realSize. */
    static double depth(double realSize, double pixelSize, int imageWidth) {
        return realSize * focalPx(imageWidth, HFOV_DEG) / pixelSize;
    }

    /** Straight-line distance, accounting for the box's offset from the image center. */
    static double horizontalDistance(double x1, double x2, double y1, double y2,
                                     double realSize, boolean useHeight,
                                     int imageWidth) {
        double f = focalPx(imageWidth, HFOV_DEG);
        double pixelSize = useHeight ? (y2 - y1) : (x2 - x1);
        double z = realSize * f / pixelSize;

        double cx = (x1 + x2) / 2.0;
        double x = (cx - imageWidth / 2.0) * z / f;

        return Math.sqrt(x * x + z * z);
    }

    public static void main(String[] args) {
        // Example: 1.7 m tall person, box from (800,300) to (1000,700), 1920x1080 image
        double z = depth(1.7, 700 - 300, 1920);

        System.out.printf("Depth: %.2f m%n", z);
        //System.out.printf("Straight-line distance: %.2f m%n", );
    }
}
