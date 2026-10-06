package org.firstinspires.ftc.teamcode.pedro.paths;

import static com.pedropathing.api.Paths.*;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;

public class bluePaths {

    private final PoseFactory poseFactory = PoseFactory.degrees().mirrorX(70.75);

    private final Pose start = poseFactory.of(9, 133, 90);
    private final Pose path1 = poseFactory.of(58, 35, 90);
    private final Pose path1Control1 = poseFactory.of(63.0389, 130.0955, 0);
    private final Pose path1Control2 = poseFactory.of(61.1358, 99.6726, 0);
    private final Pose path1Segment1Start = poseFactory.of(58, 35, 0);
    private final Pose path1Segment1End = poseFactory.of(58, 35, 90);
    private final Pose path1Segment2Heading = poseFactory.of(58, 35, 90);
    private final Pose point2 = poseFactory.of(8.25, 8.5, 180);
    private final Pose point2Control1 = poseFactory.of(57.6, 4.1377, 0);
    private final Pose point2Control2 = poseFactory.of(42, 8.7, 0);
    private final Pose point2Segment1Start = poseFactory.of(8.25, 8.5, 90);
    private final Pose point2Segment1End = poseFactory.of(8.25, 8.5, 180);
    private final Pose point2Segment2Heading = poseFactory.of(8.25, 8.5, 180);
    private final Pose point3 = poseFactory.of(58, 110, -90);
    private final Pose point3Control1 = poseFactory.of(70.7585, 5.5472, 0);
    private final Pose point3Control2 = poseFactory.of(59.1123, 49.0585, 0);
    private final Pose point3Segment2Start = poseFactory.of(58, 110, -88.4966);
    private final Pose point3Segment2End = poseFactory.of(58, 110, -90);
    private final Pose point4 = poseFactory.of(10.9481, 116.567, 0);

    public Path path1() {
        return curve(start, path1Control1, path1Control2, path1).heading(Interpolator.piecewise().until(0.5969, Interpolator.linear(path1Segment1Start, path1Segment1End)).until(1, Interpolator.constant(path1Segment2Heading)));
    }

    public Path path2() {
        return curve(path1, point2Control1, point2Control2, point2).heading(Interpolator.piecewise().until(0.45, Interpolator.linear(point2Segment1Start, point2Segment1End)).until(1, Interpolator.constant(point2Segment2Heading)));
    }

    public Path path3() {
        return curve(point2, point3Control1, point3Control2, point3).heading(Interpolator.piecewise().until(0.7, Interpolator.tangent.reverse()).until(1, Interpolator.linear(point3Segment2Start, point3Segment2End)));
    }

    public Path path4() {
        return line(point3, point4).linear(point3, point4);
    }
}