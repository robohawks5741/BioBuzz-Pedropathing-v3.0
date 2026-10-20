package org.firstinspires.ftc.teamcode.pedro.testOpModes;

import com.bylazar.panels.Panels;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.paths.redPaths;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class limelightPollenTest extends LinearOpMode {
    redPaths paths = new redPaths();
    Follower follower;
    @Override
    public void runOpMode() throws InterruptedException {
        follower = Constants.create(hardwareMap);
        follower.setPose(paths.start);
        follower.update();

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        ArrayList<pollen> pollens = new ArrayList<pollen>();
        /*
        * for (int i = 1; i <= number of pollen; i++) {
        *   create a new "pollen" object with the data (location)
        *   add it to the list "pollens"
        * }
        * */

        for (int i = 1; i <= limelight.getLatestResult().getDetectorResults().size(); i++){
            LLResultTypes.DetectorResult result = limelight.getLatestResult().getDetectorResults().get(i);
            //estimate coordinates, record with id
            double txdeg = result.getTargetXDegrees();
            double yxdeg = result.getTargetYDegrees();
            List<List<Double>> corners = result.getTargetCorners();
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

            for (List<Double> c : corners) {
                minX = Math.min(minX, c.get(0));
                maxX = Math.max(maxX, c.get(0));
                minY = Math.min(minY, c.get(1));
                maxY = Math.max(maxY, c.get(1));
            }

            double pixelWidth  = maxX - minX;
            telemetry.addData("Width in Pixels of:" + i, pixelWidth);
            double pixelHeight = maxY - minY;
            //Figure out parameters for: DistanceEstimator.euclidean()
            double horizontalDistance = DistanceEstimator.horizontalDistance(minX, maxX, minY, maxY, 1.7, false, 1920);

            //adds this pollen to the list of pollen
            pollens.add(createPollen(horizontalDistance, txdeg, i));
        }
        telemetry.update();

    }
    public pollen createPollen(double dist, double angle, int id) {
        double robotAngle = Math.toDegrees(follower.pose().heading());
        pollen p = new pollen();
        p.x = dist * Math.cos(angle+robotAngle) + follower.pose().x();
        p.y = dist * Math.sin(angle+robotAngle) + follower.pose().y();
        p.id = id;
        telemetry.addData("Pollen ID: ", p.id + " X " + p.x + " Y " + p.y);
        return p;
    }
}
